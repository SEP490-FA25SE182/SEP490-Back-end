package com.sep.googlespeechservice.controller;

import com.sep.googlespeechservice.model.Audio;
import com.sep.googlespeechservice.repository.AudioRepository;
import com.sep.googlespeechservice.service.AudioService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rookie/audios")
@RequiredArgsConstructor
public class AudioController {
    private final AudioService audioService;
    private final AudioRepository audioRepository;

    @GetMapping
    public List<Audio> getUsers() {
        return audioService.getAllAudios();
    }

    @PostMapping
    @CacheEvict(value = "allAudios", allEntries = true)
    public Audio createAudio(@RequestBody Audio audio) {
        return audioRepository.save(audio);
    }

}