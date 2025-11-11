package com.sep.arservice.repository;

import com.sep.arservice.model.Asset3DJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Asset3DJobRepository extends JpaRepository<Asset3DJob, String> {
}
