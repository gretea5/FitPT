package com.sahur.fitpt.db.repository;

import com.sahur.fitpt.db.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
