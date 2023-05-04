package com.depromeet.breadmapbackend.domain.bakery.report;

import com.depromeet.breadmapbackend.global.BaseEntity;
import com.depromeet.breadmapbackend.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "bakeryAddReport", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BakeryAddReportImage> images = new ArrayList<>();

    @Builder
    public BakeryAddReport(String name, String location, String content, User user) {
        this.name = name;
        this.location = location;
        this.content = content;
        this.status = BakeryAddReportStatus.BEFORE_REFLECT; // TODO
        this.user = user;
    }

    public void updateStatus(BakeryAddReportStatus status) { this.status = status; }
}
