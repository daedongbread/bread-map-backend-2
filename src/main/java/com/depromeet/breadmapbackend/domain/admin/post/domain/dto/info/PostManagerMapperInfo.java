package com.depromeet.breadmapbackend.domain.admin.post.domain.dto.info;

import com.depromeet.breadmapbackend.domain.admin.post.domain.PostManagerMapper;
import com.depromeet.breadmapbackend.domain.post.Post;

/**
 * PostManagerMapperInfo
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/27
 */
public record PostManagerMapperInfo(
    Long managerId,
    String nickname,
    Long userId,
    String title,
    boolean isFixed,
    boolean isCarousel,
    boolean isPosted,
    String createdAt
) {

    public PostManagerMapperInfo(final PostManagerMapper postManagerMapper, final boolean isCarousel) {
        this(
            postManagerMapper.getId(),
            getPost(postManagerMapper).getUser().getUserInfo().getNickName(),
            getPost(postManagerMapper).getUser().getId(),
            getPost(postManagerMapper).getTitle(),
            postManagerMapper.isFixed(),
            isCarousel,
            postManagerMapper.isPosted(),
            getPost(postManagerMapper).getCreatedAt().toString());
    }

    private static Post getPost(final PostManagerMapper postManagerMapper) {
        return postManagerMapper.getPost();
    }
}
