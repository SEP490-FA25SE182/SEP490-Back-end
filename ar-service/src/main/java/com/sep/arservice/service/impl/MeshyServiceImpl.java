package com.sep.arservice.service.impl;

import com.sep.arservice.config.MeshyProperties;
import com.sep.arservice.dto.Asset3DGenerateRequest;
import com.sep.arservice.dto.MeshyCreateReq;
import com.sep.arservice.dto.MeshyCreateRes;
import com.sep.arservice.dto.MeshyStatusRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeshyServiceImpl {

    private final WebClient meshyWebClient;
    private final MeshyProperties props;

    public MeshyStatusRes generateBlocking(Asset3DGenerateRequest req) {
        final Instant start = Instant.now();

        // ===== 0) Log cấu hình & input =====
        log.info("[Meshy] generateBlocking called: fmt={}, quality={}, timeout={}, pollInterval={}",
                req.getFormat(), req.getQuality(), props.getTimeout(), props.getPollInterval());
        log.debug("[Meshy] prompt='{}'", trim(req.getPrompt(), 200));

        // ===== 1) CREATE preview task =====
        MeshyCreateReq body = new MeshyCreateReq();
        body.setPrompt(req.getPrompt());
        if (req.getFormat()!=null) body.setFormat(req.getFormat().toLowerCase());
        if (req.getQuality()!=null) body.setQuality(req.getQuality());

        log.debug("[Meshy] POST /openapi/v2/text-to-3d body={}", safeBody(body));

        MeshyCreateRes created = meshyWebClient.post()
                .uri("/openapi/v2/text-to-3d")
                .bodyValue(body)
                .retrieve()
                .onStatus(HttpStatusCode::isError, r -> r.bodyToMono(String.class)
                        .map(msg -> {
                            log.warn("[Meshy] CREATE error status={} body={}", r.statusCode(), msg);
                            return new RuntimeException("Meshy create error: " + msg);
                        }))
                .bodyToMono(MeshyCreateRes.class)
                .block();

        String taskId = Optional.ofNullable(created)
                .map(MeshyCreateRes::getResult)
                .filter(s -> !s.isBlank())
                .orElseThrow(() -> {
                    log.error("[Meshy] CREATE returned empty task id. Raw response={}", created);
                    return new RuntimeException("Meshy create returned empty task id");
                });

        log.info("[Meshy] Created taskId={}", taskId);

        // ===== 2) Poll GET /openapi/v2/text-to-3d/{id} =====
        Instant deadline = Instant.now().plus(props.getTimeout());
        int tries = 0;
        MeshyStatusRes status;

        do {
            tries++;
            final int attemptNo = tries; // <— TẠO BIẾN FINAL CHO LẦN NÀY

            status = meshyWebClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/openapi/v2/text-to-3d/{id}").build(taskId))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, r -> r.bodyToMono(String.class)
                            .map(msg -> {
                                // Dùng attemptNo (final), KHÔNG dùng tries
                                log.warn("[Meshy] GET task error (try #{}) status={} body={}", attemptNo, r.statusCode(), msg);
                                return new RuntimeException("Meshy get error: " + msg);
                            }))
                    .bodyToMono(MeshyStatusRes.class)
                    .block();

            if (status == null) {
                log.error("[Meshy] GET returned null status (try #{})", tries);
                throw new RuntimeException("Meshy returned null status");
            }

            Duration elapsed = Duration.between(start, Instant.now());
            Duration remaining = Duration.between(Instant.now(), deadline);

            log.info("[Meshy] Poll #{}, taskId={}, status={}, elapsed={}s, remaining={}s",
                    tries, taskId, status.getStatus(), elapsed.toSeconds(), Math.max(remaining.toSeconds(), 0));

            // Log thêm các trường
            Map<String, String> urls = status.getModel_urls();
            if (urls != null && !urls.isEmpty()) {
                log.debug("[Meshy] model_urls keys={}", urls.keySet());
            }
            if (status.getPreview_image() != null) {
                log.debug("[Meshy] preview_image={}", status.getPreview_image());
            }
            if (status.getError() != null) {
                log.debug("[Meshy] error={}", status.getError());
            }

            if ("SUCCEEDED".equalsIgnoreCase(status.getStatus())) {
                log.info("[Meshy] Task SUCCEEDED taskId={} after {}s", taskId, elapsed.toSeconds());
                return status;
            }
            if ("FAILED".equalsIgnoreCase(status.getStatus())) {
                log.warn("[Meshy] Task FAILED taskId={} error={}", taskId, status.getError());
                return status;
            }

            try {
                Thread.sleep(props.getPollInterval().toMillis());
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                log.warn("[Meshy] Poll interrupted for taskId={}", taskId, ie);
                throw new RuntimeException("Meshy polling interrupted", ie);
            }
        } while (Instant.now().isBefore(deadline));

        MeshyStatusRes timeout = new MeshyStatusRes();
        timeout.setStatus("FAILED");
        timeout.setError("Timeout waiting for Meshy");
        log.warn("[Meshy] Task TIMEOUT taskId={} after {}s", taskId, Duration.between(start, Instant.now()).toSeconds());
        return timeout;
    }

    // ===== Helpers =====
    private static String trim(String s, int max) {
        if (s == null) return null;
        return s.length() <= max ? s : (s.substring(0, max) + "...");
    }

    private static String safeBody(MeshyCreateReq b) {
        return String.format("{mode=%s, format=%s, quality=%s, topology=%s, prompt=%s, negative_prompt=%s}",
                b.getMode(), b.getFormat(), b.getQuality(), b.getTopology(),
                trim(b.getPrompt(), 120), trim(b.getNegative_prompt(), 120));
    }
}

