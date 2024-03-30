package com.depromeet.breadmapbackend.domain.review.tag;

import com.depromeet.breadmapbackend.global.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BreadTag extends BaseEntity {
    @Id @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private Long order;

    @Enumerated(EnumType.STRING)
    private BakeryTagType reviewTagType;

    @Column(nullable = false)
    private String description;
}
