package com.sep.aiservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "page_audios")
public class PageAudio implements Serializable {
    @Id
    @Column(name = "page_audio_id", length = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String pageAudioId;

    @Column(name = "page_id", length = 50)
    private String pageId;

    @Column(name = "audio_id", length = 50)
    private String audioId;
}