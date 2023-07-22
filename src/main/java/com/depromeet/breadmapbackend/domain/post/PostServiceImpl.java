package com.depromeet.breadmapbackend.domain.post;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.depromeet.breadmapbackend.domain.post.dto.CommunityCardInfo;
import com.depromeet.breadmapbackend.domain.post.dto.PostDetailInfo;
import com.depromeet.breadmapbackend.domain.post.dto.PostRegisterCommand;
import com.depromeet.breadmapbackend.domain.post.dto.PostReportCommand;
import com.depromeet.breadmapbackend.domain.post.dto.PostUpdateCommand;
import com.depromeet.breadmapbackend.domain.post.dto.response.CommunityCardResponse;
import com.depromeet.breadmapbackend.domain.post.dto.response.PostResponse;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.UserRepository;
import com.depromeet.breadmapbackend.global.dto.PageCommunityResponseDto;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostServiceImpl implements PostService {

	private final PostRepository postRepository;
	private final UserRepository userRepository;

	@Override
	@Transactional
	public void register(
		final PostRegisterCommand command,
		final Long userId
	) {
		final User user = userRepository.findById(userId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));

		postRepository.save(command.toEntity(user));
	}

	@Override
	public PageCommunityResponseDto<CommunityCardResponse> getCommunityCards(
		final CommunityPage page,
		final Long userId,
		final PostTopic postTopic
	) {
		final Page<CommunityCardInfo> communityCards = getCommunityCardsBy(page, userId, postTopic);

		return PageCommunityResponseDto.of(
			communityCards,
			CommunityCardInfo::toResponse,
			getSelectedPostCount(communityCards) + page.postOffset(),
			getSelectedReviewCount(communityCards) + page.postOffset()
		);
	}

	@Override
	public PostDetailInfo getPost(final Long postId, final Long userId, final PostTopic postTopic) {
		return postRepository.findPostBy(postId, userId, postTopic);
	}

	@Override
	public List<PostResponse> getHotPosts(final Long userId) {
		return null;
	}

	@Override
	@Transactional
	public void remove(
		Long userId,
		Long postId
	) {
		postRepository.deletePostById(postId, userId);
	}

	@Override
	public void update(final Long userId, final PostUpdateCommand command) {

	}

	@Override
	public void toggleLike(final Long userId, final Long postId) {

	}

	@Override
	public void report(final Long userId, final PostReportCommand command) {

	}

	private Long getSelectedPostCount(final Page<CommunityCardInfo> communityCards) {
		return communityCards.stream()
			.filter(PostServiceImpl::isPost)
			.count();
	}

	private Long getSelectedReviewCount(final Page<CommunityCardInfo> communityCards) {
		return communityCards.stream()
			.filter(PostServiceImpl::isReview).count();
	}

	private static boolean isReview(final CommunityCardInfo communityCard) {
		return communityCard.topic().equals(PostTopic.REVIEW);
	}

	private static boolean isPost(final CommunityCardInfo communityCard) {
		return !isReview(communityCard);
	}

	private Page<CommunityCardInfo> getCommunityCardsBy(final CommunityPage page, final Long userId,
		final PostTopic postTopic) {
		return switch (postTopic) {
			case ALL -> postRepository.findAllCommunityCards(page, userId);
			case BREAD_STORY -> postRepository.findBreadStoryCards(page, userId);
			case REVIEW -> postRepository.findReviewCards(page, userId);
			case EVENT -> postRepository.findEventCards(page, userId);
		};
	}
}
