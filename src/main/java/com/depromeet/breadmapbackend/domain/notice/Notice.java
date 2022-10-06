package com.depromeet.breadmapbackend.domain.notice;

import com.depromeet.breadmapbackend.domain.common.BaseEntity;
import com.depromeet.breadmapbackend.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "fromUser_id")
    private User fromUser;

    @Column(nullable = false)
    private String title;

    private Long contentId; // 리뷰 아이디 or 댓글 아이디 or 빵집 아이디 or 상품 아이디 or 빵집 관리자 글 아이디

    private String content;

    @Column(nullable = false)
    private NoticeType type;

    @Builder
    public Notice(User user, User fromUser, String title, Long contentId, String content, NoticeType type) {
        this.user = user;
        this.fromUser = fromUser;
        this.title = title;
        this.contentId = contentId;
        this.content = content;
        this.type = type;
    }
}
