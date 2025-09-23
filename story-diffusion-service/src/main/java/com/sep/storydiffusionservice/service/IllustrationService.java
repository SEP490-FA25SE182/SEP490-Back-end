package com.sep.storydiffusionservice.service;

import com.sep.storydiffusionservice.model.Illustration;
import com.sep.storydiffusionservice.repository.IllustrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IllustrationService {
    private final IllustrationRepository illustrationRepository;

    @Cacheable(value = "allIllustrations", key = "'all'")
    public List<Illustration> getAllIllustrations() {
        System.out.println("⏳ Querying DB...");
        return illustrationRepository.findAll();
    }
}