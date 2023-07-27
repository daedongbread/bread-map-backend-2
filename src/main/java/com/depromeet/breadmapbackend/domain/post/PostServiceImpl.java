package com.depromeet.breadmapbackend.domain.post;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.depromeet.breadmapbackend.domain.admin.post.domain.repository.PostAdminRepository;
import com.depromeet.breadmapbackend.domain.post.comment.CommentRepository;
import com.depromeet.breadmapbackend.domain.post.comment.like.CommentLikeRepository;
import com.depromeet.breadmapbackend.domain.post.dto.CommunityCardInfo;
import com.depromeet.breadmapbackend.domain.post.dto.EventCarouselInfo;
import com.depromeet.breadmapbackend.domain.post.dto.PostDetailInfo;
import com.depromeet.breadmapbackend.domain.post.dto.PostRegisterCommand;
import com.depromeet.breadmapbackend.domain.post.dto.PostUpdateCommand;
import com.depromeet.breadmapbackend.domain.post.dto.response.CommunityCardResponse;
import com.depromeet.breadmapbackend.domain.post.like.PostLike;
import com.depromeet.breadmapbackend.domain.post.like.PostLikeRepository;
import com.depromeet.breadmapbackend.domain.report.ReportRepository;
import com.depromeet.breadmapbackend.domain.report.ReportType;
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
	private final PostLikeRepository postLikeRepository;
	private final CommentRepository commentRepository;
	private final CommentLikeRepository commentLikeRepository;
	private final ReportRepository reportRepository;
	private final PostAdminRepository postAdminRepository;

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
	public List<CommunityCardResponse> findHotPosts(final Long userId) {
		return postRepository.findHotPosts(userId)
			.stream()
			.map(CommunityCardInfo::toResponse)
			.toList();
	}

	@Override
	public PostDetailInfo getDetailPost(final Long postId, final Long userId, final PostTopic postTopic) {
		return postRepository.findPostDetailBy(postId, userId, postTopic);
	}

	@Override
	@Transactional
	public void delete(
		final Long postId,
		final PostTopic topic,
		final Long userId
	) {
		final Post post = postRepository.findByPostIdAndUserIdAndPostTopic(postId, userId, topic)
			.orElseThrow(() -> new DaedongException(DaedongStatus.POST_NOT_FOUND));

		final List<Long> commentIdListToDelete = commentRepository.findCommentIdListByPostId(postId);

		commentRepository.deleteAllByIdInBatch(commentIdListToDelete);
		commentLikeRepository.deleteAllByCommentIdList(commentIdListToDelete);
		postLikeRepository.deleteByPostId(postId);
		reportRepository.deleteByPostIdAndReportType(postId, ReportType.of(topic.name()));
		postRepository.delete(post);
	}

	@Override
	@Transactional
	public void update(final Long userId, final PostUpdateCommand command) {
		final Post savedPost =
			postRepository.findByPostIdAndUserIdAndPostTopic(command.postId(), userId, command.postTopic())
				.orElseThrow(() -> new DaedongException(DaedongStatus.POST_NOT_FOUND));
		savedPost.update(command.content(), command.title(), command.images());
		// postRepository.save(savedPost);
	}

	@Transactional
	@Override
	public int toggle(final Long postId, final Long userId) {
		final Optional<PostLike> postLike = postLikeRepository.findByPostIdAndUserId(postId, userId);
		if (postLike.isEmpty()) {
			postLikeRepository.save(new PostLike(postId, userId));
			return 1;
		} else {
			postLikeRepository.delete(postLike.get());
			return 0;
		}
	}

	@Override
	public List<EventCarouselInfo> getEventCarousels() {
		return postAdminRepository.findCarouselPosts().stream()
			.map(EventCarouselInfo::of)
			.toList();

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

	private Page<CommunityCardInfo> getCommunityCardsBy(
		final CommunityPage page,
		final Long userId,
		final PostTopic postTopic
	) {
		return switch (postTopic) {
			case ALL -> postRepository.findAllCommunityCards(page, userId);
			case BREAD_STORY, FREE_TALK -> postRepository.findBreadStoryCards(page, userId, postTopic);
			case REVIEW -> postRepository.findReviewCards(page, userId);
			case EVENT -> postRepository.findEventCards(page, userId);
		};
	}
}
