package com.depromeet.breadmapbackend.domain.review.dto;

import com.depromeet.breadmapbackend.domain.review.comment.ReviewComment;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class ReviewCommentDto {
    private Long id;

    private Long userId;
    private String userImage;
    private String nickName;

    private String commentNickName;
    private String content;
    private String createdAt;

    private Integer likeNum;

    private List<ReviewCommentDto> commentList;

    @Builder
    public ReviewCommentDto(ReviewComment reviewComment) {
        this.id = reviewComment.getId();
        if(reviewComment.getUser() != null) {
            this.userId = reviewComment.getUser().getId();
            this.userImage = reviewComment.getUser().getImage();
            this.nickName = reviewComment.getUser().getNickName();
        }
        else {
            this.userId = null;
            this.userImage = null;
            this.nickName = null;
        }

        if(reviewComment.getParent() == null) this.commentNickName = null;
        else {
            ReviewComment parent = reviewComment.getParent();
            if(parent.getUser() == null) this.commentNickName = null;
            else this.commentNickName = reviewComment.getParent().getUser().getNickName();
        }

        this.content = reviewComment.getContent();
        this.createdAt = reviewComment.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        this.likeNum = reviewComment.getLikes().size();
        this.commentList = reviewComment.getChildList().stream().map(ReviewCommentDto::new).collect(Collectors.toList());
    }
}
