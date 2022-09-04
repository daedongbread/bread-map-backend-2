package com.depromeet.breadmapbackend.domain.review;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.common.BaseEntity;
import com.depromeet.breadmapbackend.domain.common.StringListConverter;
import com.depromeet.breadmapbackend.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
public class Review extends BaseEntity {

    @Id @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "bakery_id")
    private Bakery bakery;

    @Column(nullable = false, length = 200)
    private String content;

    @Convert(converter = StringListConverter.class)
    private List<String> imageList = new ArrayList<>();

    @Column(nullable = false/*, columnDefinition = "boolean default 1"*/)
    @Enumerated(EnumType.STRING)
    private ReviewStatus status;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BreadRating> ratings = new ArrayList<>();

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewLike> likes = new ArrayList<>();

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewComment> comments = new ArrayList<>();

    @Column(nullable = false)
    private Integer views;

    @Builder
    private Review(User user, Bakery bakery, String content) {
        this.user = user;
        this.bakery = bakery;
        this.content = content;
        this.status = ReviewStatus.UNBLOCK;
        this.views = 0;
    }

    public void useChange() {
        if(this.status.equals(ReviewStatus.BLOCK)) this.status = ReviewStatus.UNBLOCK;
        else this.status = ReviewStatus.BLOCK;
    }

    public void addImage(String image) {
        this.imageList.add(image);
    }

    public void addRating(BreadRating breadRating){
        this.ratings.add(breadRating);
    }

    public void plusLike(ReviewLike reviewLike){ this.likes.add(reviewLike); }

    public void minusLike(ReviewLike reviewLike){ this.likes.remove(reviewLike); }

    public void addComment(ReviewComment reviewComment){
        this.comments.add(reviewComment);
    }

    public void removeComment(ReviewComment reviewComment){ this.comments.remove(reviewComment); }

    public void addViews() { this.views += 1; }
}
