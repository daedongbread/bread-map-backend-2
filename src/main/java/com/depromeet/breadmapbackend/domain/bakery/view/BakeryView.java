package com.depromeet.breadmapbackend.domain.bakery.view;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.global.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BakeryView extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Bakery bakery;

    @Column(nullable = false)
    private Integer viewNum;


    @Builder
    public BakeryView(Bakery bakery) {
        this.bakery = bakery;
        this.viewNum = 0;
    }

    public void viewBakery() {
        this.viewNum += 1;
    }
}
