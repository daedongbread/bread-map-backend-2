package com.depromeet.breadmapbackend.web.controller.review.dto;

import com.depromeet.breadmapbackend.domain.review.ReviewComment;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class ReviewCommentDto {
    private Long id;

    private Long userId;
    private String userImage;
    private String nickName;

    private String content;
    private LocalDateTime createdAt;

    private Integer likeNum;

    private List<ReviewCommentDto> commentDtoList;

    @Builder
    public ReviewCommentDto(ReviewComment reviewComment) {
        this.id = reviewComment.getId();
        if(reviewComment.getUser() != null) {
            this.userId = reviewComment.getUser().getId();
            this.userImage = reviewComment.getUser().getProfileImageUrl();
            this.nickName = reviewComment.getUser().getNickName();
        }
        else {
            this.userId = null;
            this.userImage = null;
            this.nickName = null;
        }
        this.content = reviewComment.getContent();
        this.createdAt = reviewComment.getCreatedAt();
        this.likeNum = reviewComment.getLikes().size();
        this.commentDtoList = reviewComment.getChildList().stream().map(ReviewCommentDto::new).collect(Collectors.toList());
    }
}
