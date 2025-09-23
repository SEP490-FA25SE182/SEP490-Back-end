package com.sep.googlespeechservice.config;

import com.sep.googlespeechservice.model.Audio;
import com.sep.googlespeechservice.repository.AudioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfig {
    @Bean
    CommandLineRunner testMongo(AudioRepository repo) {
        return args -> {
            Audio audio = new Audio();
            audio.setTextInput("Haha pretty");
            audio.setVoice("i dont know");
            audio.setAssetType("anime");

            repo.save(audio);

            System.out.println("Inserted audio: " + repo.findById(""));
        };
    }
}
