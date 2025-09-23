package com.sep.googlespeechservice.service;

import com.sep.googlespeechservice.model.Audio;
import com.sep.googlespeechservice.repository.AudioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AudioService {
    private final AudioRepository audioRepository;

    @Cacheable(value = "allAudios", key = "'all'")
    public List<Audio> getAllAudios() {
        System.out.println("⏳ Querying DB...");
        return audioRepository.findAll();
    }
}