package com.sep.arservice.model;

import com.sep.arservice.enums.JobStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="asset3d_jobs")
public class Asset3DJob implements Serializable {
    @Id
    @Column(name="job_id", length=50)
    @GeneratedValue(strategy=GenerationType.UUID)
    private String jobId;

    @Column(name="user_id", length=50)
    private String userId;

    @Column(name="marker_id", length=50)
    private String markerId;

    @Column(name="prompt", length=500)
    private String prompt;

    @Column(name="file_name", length=200)
    private String fileName;

    @Column(name="quality", length=30)
    private String quality; // e.g. "balanced/high"

    @Column(name="target_format", length=10)
    private String targetFormat; // GLB,…

    @Enumerated(EnumType.STRING)
    @Column(name="status", length=20)
    private JobStatus status = JobStatus.PENDING;

    @Column(name="meshy_task_id", length=100)
    private String meshyTaskId;

    @Column(name="result_url", length=1000)
    private String resultUrl; // URL download từ Meshy

    @Column(name="created_at", updatable=false)
    private Instant createdAt = Instant.now();

    @Column(name="updated_at")
    private Instant updatedAt = Instant.now();

    @Column(name="error_message", length=1000)
    private String errorMessage;
}

