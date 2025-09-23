package com.sep.storydiffusionservice.repository;

import com.sep.storydiffusionservice.model.Illustration;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface IllustrationRepository extends MongoRepository<Illustration, String> {
    Optional<Illustration> findById(String illustrationId);
}