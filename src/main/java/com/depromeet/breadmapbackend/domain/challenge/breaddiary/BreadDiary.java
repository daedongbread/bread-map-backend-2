package com.depromeet.breadmapbackend.domain.challenge.breaddiary;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.product.Product;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.global.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class BreadDiary extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "bakery_id")
    private Bakery bakery;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private int rating;

    /**
     * 빵집 평가 태그 ,로 join해서 만든 string
     */
    private String bakeryTags;

    /**
     * 빵, 음료 평가 태그 ,로 join해서 만든 string
     */
    private String productTags;

    private BreadDiaryStatus status;

    /**
     * 지급 포인트
     */
    private Integer givenPoint;

    /**
     * 누적 합
     */
    private Integer cusum;

    /**
     * 지급 일자(시간)
     */
    private LocalDateTime givenDatetime;
}
