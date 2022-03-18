package com.depromeet.breadmapbackend.domain.common;

import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

/*
 * Created by ParkSuHo by 2022/03/18.
 */
@Getter(AccessLevel.PROTECTED)
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
    @CreatedDate
    @Column(name = "created_date_time", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "last_modified_date_time")
    private LocalDateTime modifiedAt;
}