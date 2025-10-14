package com.sep.rookieservice.repository;

import com.sep.rookieservice.entity.Notification;
import com.sep.rookieservice.enums.IsActived;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String>, JpaSpecificationExecutor<Notification> {
    long countByIsActived(IsActived isActived);
}