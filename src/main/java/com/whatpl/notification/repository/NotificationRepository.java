package com.whatpl.notification.repository;

import com.whatpl.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("select count(n) from Notification n where n.receiver.id = :receiverId and n.isRead = false")
    int findUnreadCount(long receiverId);
}
