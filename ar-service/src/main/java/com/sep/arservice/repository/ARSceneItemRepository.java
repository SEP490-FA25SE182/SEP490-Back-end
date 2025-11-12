package com.sep.arservice.repository;

import com.sep.arservice.model.ARSceneItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ARSceneItemRepository extends JpaRepository<ARSceneItem, String> {
    List<ARSceneItem> findBySceneIdOrderByOrderIndexAsc(String sceneId);
}