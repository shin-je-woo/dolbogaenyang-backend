package com.whatpl.global.common.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
public abstract class BaseTimeEntity {

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    private void onPrePersist(){
        this.createdAt = LocalDateTime.now().withNano(0);
        this.updatedAt = createdAt;
    }

    @PreUpdate
    private void onPreUpdate(){
        this.updatedAt = LocalDateTime.now().withNano(0);
    }
}