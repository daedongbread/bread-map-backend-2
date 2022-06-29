package com.depromeet.breadmapbackend.domain.bkreport;

import com.depromeet.breadmapbackend.domain.common.BaseEntity;
import com.depromeet.breadmapbackend.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BkReport extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String bakeryName;

    @Column(nullable = false)
    private String address;

    @Column
    private String reason;

    @Column(nullable = false, columnDefinition = "integer default 0")
    private Integer status;

    @Column(nullable = false, columnDefinition = "boolean default 1")
    private boolean isUse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public BkReport(Long id, String bakeryName, String address,
                  String reason, Integer status, User user) {
        this.id = id;
        this.bakeryName = bakeryName;
        this.address = address;
        this.reason = reason;
        this.status = status;
        this.user = user;
        this.isUse = true;
    }

    public void updateStatus(Integer status) { this.status = status; }
}
