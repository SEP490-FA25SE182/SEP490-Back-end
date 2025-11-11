package com.sep.arservice.service.impl;

import com.sep.arservice.dto.Asset3DGenerateRequest;
import com.sep.arservice.dto.Asset3DResponse;
import com.sep.arservice.dto.Asset3DUploadRequest;
import com.sep.arservice.dto.MeshyStatusRes;
import com.sep.arservice.mapper.Asset3DMapper;
import com.sep.arservice.model.Asset3D;
import com.sep.arservice.model.Asset3DJob;
import com.sep.arservice.repository.Asset3DJobRepository;
import com.sep.arservice.repository.Asset3DRepository;
import com.sep.arservice.service.Asset3DService;
import com.sep.arservice.service.StorageService;
import com.sep.arservice.enums.JobStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class Asset3DServiceImpl implements Asset3DService {

    private final Asset3DRepository repo;
    private final Asset3DJobRepository jobRepo;
    private final Asset3DMapper mapper;
    private final StorageService storage;
    private final MeshyServiceImpl meshy;
    private final WebClient.Builder webClientBuilder = WebClient.builder();

    @Value("${storage.model3d-prefix:models}") private String modelPrefix;

    @Override @Transactional(readOnly = true)
    public List<Asset3DResponse> getAll() {
        return repo.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override @Transactional(readOnly = true)
    public Asset3DResponse getById(String id) {
        return repo.findById(id).map(mapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Asset3D not found: " + id));
    }

    @Override
    public Asset3DResponse upload(MultipartFile file, Asset3DUploadRequest meta) throws IOException {
        String safeBase = sanitizeBaseName(meta.getFileName());
        if (safeBase == null || safeBase.isBlank()) {
            safeBase = sanitizeBaseName(file.getOriginalFilename());
            if (safeBase == null || safeBase.isBlank()) {
                safeBase = "upload";
            }
        }

        // đuôi theo format
        String fmt = (meta.getFormat() != null) ? meta.getFormat().toLowerCase() : null;
        if (fmt == null) {
            String original = file.getOriginalFilename();
            if (original != null && original.toLowerCase().endsWith(".fbx")) fmt = "fbx";
            else if (original != null && original.toLowerCase().endsWith(".obj")) fmt = "obj";
            else fmt = "glb";
        }
        String ext = switch (fmt) { case "fbx" -> "fbx"; case "obj" -> "obj"; default -> "glb"; };

        String suffix = shortMarker8(meta.getMarkerId());
        String finalFileName = safeBase + "-" + suffix;
        if (!finalFileName.toLowerCase().endsWith("." + ext)) {
            finalFileName = finalFileName + "." + ext;
        }

        // ĐƯỜNG DẪN
        String objectPath = "%s/%s".formatted(modelPrefix, finalFileName);

        // upload
        String url = storage.upload(objectPath, file.getInputStream(), file.getContentType(), file.getSize());

        Asset3D e = new Asset3D();
        e.setMarkerId(meta.getMarkerId());
        e.setUserId(meta.getUserId());
        e.setAssetUrl(url);
        e.setPrompt(meta.getPrompt());
        e.setFileName(finalFileName);
        e.setFormat(ext.toUpperCase());
        e.setSource("UPLOAD");
        e.setFileSize(file.getSize());
        e.setScale(meta.getScale());
        return mapper.toResponse(repo.save(e));
    }

    @Override
    public Asset3DResponse generate(Asset3DGenerateRequest req) {
        // Ghi job
        Asset3DJob job = new Asset3DJob();
        job.setUserId(req.getUserId());
        job.setMarkerId(req.getMarkerId());
        job.setPrompt(req.getPrompt());
        job.setFileName(req.getFileName());
        job.setQuality(req.getQuality());
        job.setTargetFormat(req.getFormat());
        job.setStatus(JobStatus.RUNNING);
        job = jobRepo.save(job);

        // Gọi Meshy
        MeshyStatusRes status = meshy.generateBlocking(req);
        String fmt = (req.getFormat() == null ? "glb" : req.getFormat().toLowerCase());
        if (!"SUCCEEDED".equalsIgnoreCase(status.getStatus())) {
            job.setStatus(JobStatus.FAILED);
            job.setErrorMessage(status.getError());
            jobRepo.save(job);
            throw new RuntimeException("Meshy failed: " + status.getError());
        }
        String remoteUrl = status.getModel_urls() != null ? status.getModel_urls().get(fmt) : null;
        if (remoteUrl == null || remoteUrl.isBlank()) {
            job.setStatus(JobStatus.FAILED);
            job.setErrorMessage("Meshy returned no model URL for format: " + fmt);
            jobRepo.save(job);
            throw new RuntimeException("Meshy returned no model URL for format: " + fmt);
        }

        // Tên file
        String safeBase = sanitizeBaseName(req.getFileName());
        if (safeBase == null || safeBase.isBlank()) {
            safeBase = "meshy_" + job.getJobId();
        }
        String suffix = shortMarker8(req.getMarkerId());
        String ext = switch (fmt) { case "fbx" -> "fbx"; case "obj" -> "obj"; default -> "glb"; };

        String finalFileName = safeBase + "-" + suffix;
        if (!finalFileName.toLowerCase().endsWith("." + ext)) {
            finalFileName = finalFileName + "." + ext;
        }

        // ĐƯỜNG DẪN
        String objectPath = "%s/%s".formatted(modelPrefix, finalFileName);

        String contentType = switch (fmt) {
            case "fbx" -> "application/octet-stream";
            case "obj" -> "text/plain";
            default -> "model/gltf-binary";
        };

        log.info("Meshy download url={}", remoteUrl);

        long contentLength;
        String publicUrl;
        try {
            URL url = new URL(remoteUrl);
            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(30_000);
            conn.setReadTimeout(120_000);
            contentLength = conn.getContentLengthLong();

            try (InputStream in = new BufferedInputStream(conn.getInputStream())) {
                publicUrl = storage.upload(objectPath, in, contentType, contentLength);
            }
        } catch (IOException e) {
            job.setStatus(JobStatus.FAILED);
            job.setErrorMessage("Download/Upload error: " + e.getMessage());
            jobRepo.save(job);
            throw new RuntimeException("Failed to fetch/upload Meshy model", e);
        }

        // Cập nhật job & lưu Asset3D
        job.setStatus(JobStatus.DONE);
        job.setResultUrl(remoteUrl);
        jobRepo.save(job);

        Asset3D e = new Asset3D();
        e.setMarkerId(req.getMarkerId());
        e.setUserId(req.getUserId());
        e.setSource("MESHY");
        e.setPrompt(req.getPrompt());
        e.setFormat(ext.toUpperCase());
        e.setFileName(finalFileName);
        e.setAssetUrl(publicUrl);
        if (status.getFace_count() != null) e.setPolycount(status.getFace_count());
        if (contentLength > 0) e.setFileSize(contentLength);
        return mapper.toResponse(repo.save(e));
    }


    @Override public void deleteHard(String id) {
        repo.delete(repo.findById(id).orElseThrow(() -> new RuntimeException("Asset3D not found: " + id)));
    }

    @Override @Transactional(readOnly = true)
    public Page<Asset3DResponse> search(String markerId, String userId, String format, Pageable pageable) {
        Asset3D probe = new Asset3D();
        if (markerId!=null && !markerId.isBlank()) probe.setMarkerId(markerId.trim());
        if (userId!=null && !userId.isBlank()) probe.setUserId(userId.trim());
        if (format!=null && !format.isBlank()) probe.setFormat(format.trim().toUpperCase());

        ExampleMatcher m = ExampleMatcher.matchingAll()
                .withMatcher("markerId", mm -> mm.ignoreCase())
                .withMatcher("userId",   mm -> mm.ignoreCase())
                .withMatcher("format",   mm -> mm.ignoreCase())
                .withIgnoreNullValues();

        return repo.findAll(Example.of(probe, m), pageable).map(mapper::toResponse);
    }

    @Override @Transactional(readOnly = true)
    public Page<Asset3DResponse> searchByMarkerCode(String markerCode, Pageable pageable) {
        return repo.findByMarker_MarkerCodeIgnoreCase(markerCode, pageable).map(mapper::toResponse);
    }

    @Override @Transactional(readOnly = true)
    public List<Asset3DResponse> latestByMarker(String markerId, int limit) {
        return repo.findByMarkerIdOrderByCreatedAtDesc(markerId).stream()
                .limit(limit)
                .map(mapper::toResponse)
                .toList();
    }

    /** Lấy 8 ký tự đầu của markerId*/
    private static String shortMarker8(String markerId) {
        if (markerId == null || markerId.isBlank()) return "unknown";
        int dash = markerId.indexOf('-');
        String head = (dash > 0) ? markerId.substring(0, dash) : markerId;
        return head.length() >= 8 ? head.substring(0, 8) : head;
    }

    /** Chỉ lấy tên-base, loại bỏ thư mục, ký tự lạ và cắt độ dài hợp lý. */
    private static String sanitizeBaseName(String input) {
        if (input == null) return null;
        String name = input.replace("\\", "/");
        int slash = name.lastIndexOf('/');
        if (slash >= 0) name = name.substring(slash + 1);

        int dot = name.lastIndexOf('.');
        if (dot > 0) name = name.substring(0, dot);

        name = name.replaceAll("[^a-zA-Z0-9-_]", "_");
        if (name.isBlank()) return null;
        if (name.length() > 120) name = name.substring(0, 120);
        return name;
    }
}

