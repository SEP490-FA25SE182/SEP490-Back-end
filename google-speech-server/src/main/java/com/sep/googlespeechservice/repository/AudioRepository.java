package com.sep.googlespeechservice.repository;

import com.sep.googlespeechservice.model.Audio;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AudioRepository extends MongoRepository<Audio, String> {
    Optional<Audio> findById(String audioId);
}