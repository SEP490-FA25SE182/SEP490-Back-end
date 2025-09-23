package com.sep.storydiffusionservice.controller;

import com.sep.storydiffusionservice.model.Illustration;
import com.sep.storydiffusionservice.repository.IllustrationRepository;
import com.sep.storydiffusionservice.service.IllustrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rookie/illustrations")
@RequiredArgsConstructor
public class IllustrationController {
    private final IllustrationService illustrationService;
    private final IllustrationRepository illustrationRepository;

    @GetMapping
    public List<Illustration> getUsers() {
        return illustrationService.getAllIllustrations();
    }

    @PostMapping
    @CacheEvict(value = "allIllustrations", allEntries = true)
    public Illustration createIllustration(@RequestBody Illustration illustration) {
        return illustrationRepository.save(illustration);
    }

}