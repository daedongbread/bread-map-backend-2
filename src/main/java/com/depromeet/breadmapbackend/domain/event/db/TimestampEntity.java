package com.depromeet.breadmapbackend.domain.event.db;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@MappedSuperclass
@Getter
public abstract class TimestampEntity {
    @Column(updatable = false)
    @NotNull
    private ZonedDateTime createdAt;
    @NotNull
    private ZonedDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = ZonedDateTime.now();
        updatedAt = ZonedDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = ZonedDateTime.now();
    }
}
