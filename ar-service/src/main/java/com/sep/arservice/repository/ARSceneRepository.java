package com.sep.arservice.repository;

import com.sep.arservice.model.ARScene;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


@Repository
public interface ARSceneRepository extends JpaRepository<ARScene, String> {
    Optional<ARScene> findTopByMarkerIdAndStatusOrderByCreatedAtDesc(String markerId, String status);

    // find latest published scene per markerId (dùng để build manifest)
    Optional<ARScene> findTopByMarkerIdAndStatusOrderByUpdatedAtDesc(String markerId, String status);

    // list scenes by markerIds + status (tối ưu lấy batch)
    List<ARScene> findByMarkerIdInAndStatus(Collection<String> markerIds, String status);

    // latest scene per markerId (không filter status)
    Optional<ARScene> findTopByMarkerIdOrderByCreatedAtDesc(String markerId);

}