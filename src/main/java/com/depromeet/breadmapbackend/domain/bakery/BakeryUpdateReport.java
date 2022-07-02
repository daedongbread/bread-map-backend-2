package com.depromeet.breadmapbackend.domain.bakery;

import com.depromeet.breadmapbackend.domain.common.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BakeryUpdateReport extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bakery_id")
    private Bakery bakery;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String content;

    @Builder
    public BakeryUpdateReport(Bakery bakery, String name, String location, String content) {
        this.bakery = bakery;
        this.name = name;
        this.location = location;
        this.content = content;
    }
}
