package com.depromeet.breadmapbackend.domain.notice;

import com.depromeet.breadmapbackend.global.BaseEntity;
import com.depromeet.breadmapbackend.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "fromUser_id")
    private User fromUser;

//    /*
//     알림 주 내용
//     */
//    @Column(nullable = false)
//    private String title;

    /*
     내가 쓴 리뷰 아이디 or 내가 쓴 댓글 아이디 or 유저 아이디
     */
    private Long contentId;

    /*
     내가 쓴 리뷰 내용(디자인엔 제목으로 나와있음) or 내가 쓴 댓글 내용
     팔로우/팔로잉 알람은 null
     */
    private String content;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private NoticeType type;

    @Builder
    public Notice(User user, User fromUser, Long contentId, String content, NoticeType type) {
        this.user = user;
        this.fromUser = fromUser;
        this.contentId = contentId;
        this.content = content;
        this.type = type;
    }
}
