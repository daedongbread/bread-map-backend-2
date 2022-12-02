package com.depromeet.breadmapbackend.domain.bakery;

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
public class BakeryAddReport extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String location;

    private String content;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BakeryAddReportStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public BakeryAddReport(String name, String location, String content, User user) {
        this.name = name;
        this.location = location;
        this.content = content;
        this.status = BakeryAddReportStatus.BEFORE_REFLECT;
        this.user = user;
    }

    public void updateStatus(BakeryAddReportStatus status) { this.status = status; }
}
