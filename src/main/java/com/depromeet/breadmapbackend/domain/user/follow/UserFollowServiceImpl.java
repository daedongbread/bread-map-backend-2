package com.depromeet.breadmapbackend.domain.user.follow;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.depromeet.breadmapbackend.domain.notice.dto.NoticeEventDto;
import com.depromeet.breadmapbackend.domain.notice.factory.NoticeType;
import com.depromeet.breadmapbackend.domain.review.ReviewRepository;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.UserRepository;
import com.depromeet.breadmapbackend.domain.user.follow.dto.FollowRequest;
import com.depromeet.breadmapbackend.domain.user.follow.dto.FollowUserDto;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserFollowServiceImpl implements UserFollowService {
	private final UserRepository userRepository;
	private final FollowRepository followRepository;
	private final ReviewRepository reviewRepository;
	private final ApplicationEventPublisher eventPublisher;

	@Transactional(rollbackFor = Exception.class)
	public void follow(String oAuthId, FollowRequest request) {
		User fromUser = userRepository.findByOAuthId(oAuthId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
		User toUser = userRepository.findById(request.getUserId())
			.orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
		if (toUser.isDeRegistered())
			throw new DaedongException(DaedongStatus.USER_NOT_REGISTERED);
		if (fromUser.equals(toUser))
			throw new DaedongException(DaedongStatus.SELF_FOLLOW_EXCEPTION);
		if (followRepository.findByFromUserAndToUser(fromUser, toUser).isPresent())
			throw new DaedongException(DaedongStatus.FOLLOW_DUPLICATE_EXCEPTION);
		Follow follow = Follow.builder().fromUser(fromUser).toUser(toUser).build();
		followRepository.save(follow);
		eventPublisher.publishEvent(
			NoticeEventDto.builder()
				.userId(toUser.getId())
				.contentId(fromUser.getId())
				.noticeType(NoticeType.FOLLOW)
				.build()
		);
	}

	@Transactional(rollbackFor = Exception.class)
	public void unfollow(String oAuthId, FollowRequest request) {
		User fromUser = userRepository.findByOAuthId(oAuthId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
		User toUser = userRepository.findById(request.getUserId())
			.orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
		if (fromUser.equals(toUser))
			throw new DaedongException(DaedongStatus.SELF_FOLLOW_EXCEPTION);
		Follow follow = followRepository.findByFromUserAndToUser(fromUser, toUser)
			.orElseThrow(() -> new DaedongException(DaedongStatus.FOLLOW_NOT_FOUND));
		followRepository.delete(follow);
	}

	@Transactional(readOnly = true, rollbackFor = Exception.class)
	public List<FollowUserDto> followerList(String oAuthId, Long userId) { // 나를 팔로우한 사람
		User me = userRepository.findByOAuthId(oAuthId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
		User toUser = userRepository.findById(userId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
		return followRepository.findByToUser(toUser).stream()
			.map(follow -> new FollowUserDto(
				follow.getFromUser(),
				reviewRepository.countByUser(follow.getFromUser()),
				followRepository.countByToUser(follow.getFromUser()),
				followRepository.findByFromUserAndToUser(me, follow.getFromUser()).isPresent(),
				follow.getFromUser().equals(me)
			))
			.collect(Collectors.toList());
	}

	@Transactional(readOnly = true, rollbackFor = Exception.class)
	public List<FollowUserDto> followingList(String oAuthId, Long userId) { // 내가 팔로우한 사람
		User me = userRepository.findByOAuthId(oAuthId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
		User fromUser = userRepository.findById(userId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
		return followRepository.findByFromUser(fromUser).stream()
			.map(follow -> new FollowUserDto(
				follow.getToUser(),
				reviewRepository.countByUser(follow.getToUser()),
				followRepository.countByToUser(follow.getToUser()),
				followRepository.findByFromUserAndToUser(me, follow.getToUser()).isPresent(),
				follow.getToUser().equals(me)
			))
			.collect(Collectors.toList());
	}
}
