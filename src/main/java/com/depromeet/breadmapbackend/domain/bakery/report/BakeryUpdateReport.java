package com.depromeet.breadmapbackend.domain.bakery.report;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.global.BaseEntity;
import com.depromeet.breadmapbackend.global.converter.BooleanToYNConverter;
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
public class BakeryUpdateReport extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bakery_id")
    private Bakery bakery;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private BakeryUpdateReason reason;

    @Column(nullable = false)
    private String content;

    @OneToMany(mappedBy = "bakeryUpdateReport", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BakeryUpdateReportImage> images = new ArrayList<>();

    @Column(nullable = false)
    @Convert(converter = BooleanToYNConverter.class)
    private Boolean isChange;

    @Builder
    public BakeryUpdateReport(Bakery bakery, User user, BakeryUpdateReason reason, String content) {
        this.bakery = bakery;
        this.user = user;
        this.reason = reason;
        this.content = content;
        this.isChange = false;
    }

    public void change() {
        this.isChange = true;
    }
}
