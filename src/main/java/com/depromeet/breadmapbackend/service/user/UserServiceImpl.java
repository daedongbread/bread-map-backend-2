package com.depromeet.breadmapbackend.service.user;

import com.depromeet.breadmapbackend.domain.common.ImageType;
import com.depromeet.breadmapbackend.domain.common.converter.FileConverter;
import com.depromeet.breadmapbackend.domain.exception.DaedongException;
import com.depromeet.breadmapbackend.domain.exception.DaedongStatus;
import com.depromeet.breadmapbackend.domain.flag.FlagBakery;
import com.depromeet.breadmapbackend.domain.flag.repository.FlagBakeryRepository;
import com.depromeet.breadmapbackend.domain.flag.repository.FlagRepository;
import com.depromeet.breadmapbackend.domain.notice.NoticeTokenDeleteEvent;
import com.depromeet.breadmapbackend.domain.notice.repository.NoticeRepository;
import com.depromeet.breadmapbackend.domain.notice.repository.NoticeTokenRepository;
import com.depromeet.breadmapbackend.domain.review.Review;
import com.depromeet.breadmapbackend.domain.review.ReviewStatus;
import com.depromeet.breadmapbackend.domain.review.repository.*;
import com.depromeet.breadmapbackend.domain.user.BlockUser;
import com.depromeet.breadmapbackend.domain.user.Follow;
import com.depromeet.breadmapbackend.domain.user.FollowEvent;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.repository.BlockUserRepository;
import com.depromeet.breadmapbackend.domain.user.repository.FollowRepository;
import com.depromeet.breadmapbackend.domain.user.repository.UserRepository;
import com.depromeet.breadmapbackend.infra.properties.CustomRedisProperties;
import com.depromeet.breadmapbackend.security.token.JwtToken;
import com.depromeet.breadmapbackend.security.token.JwtTokenProvider;
import com.depromeet.breadmapbackend.service.S3Uploader;
import com.depromeet.breadmapbackend.web.controller.user.dto.UserReviewDto;
import com.depromeet.breadmapbackend.web.controller.user.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final FollowRepository followRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final ReviewCommentRepository reviewCommentRepository;
    private final ReviewCommentLikeRepository reviewCommentLikeRepository;
    private final NoticeRepository noticeRepository;
    private final NoticeTokenRepository noticeTokenRepository;
    private final FlagRepository flagRepository;
    private final FlagBakeryRepository flagBakeryRepository;
    private final BlockUserRepository blockUserRepository;
    private final StringRedisTemplate redisTemplate;
    private final ApplicationEventPublisher eventPublisher;
    private final FileConverter fileConverter;
    private final S3Uploader s3Uploader;
    private final CustomRedisProperties customRedisProperties;

    @Transactional(rollbackFor = Exception.class)
    public JwtToken reissue(ReissueRequest request) {
        if(!jwtTokenProvider.verifyToken(request.getRefreshToken())) throw new DaedongException(DaedongStatus.TOKEN_INVALID_EXCEPTION);

        String accessToken = request.getAccessToken();
        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));

        String refreshToken = redisTemplate.opsForValue().get(customRedisProperties.getKey().getRefresh() + ":" + user.getUsername());
        if (refreshToken == null || !refreshToken.equals(request.getRefreshToken())) throw new DaedongException(DaedongStatus.TOKEN_INVALID_EXCEPTION);
//        RefreshToken refreshToken = refreshTokenRepository.findByUsername(username).orElseThrow(RefreshTokenNotFoundException::new);
//        if(!refreshToken.getToken().equals(request.getRefreshToken())) throw new DaedongException(DaedongStatus.TOKEN_INVALID_EXCEPTION);

        JwtToken reissueToken = jwtTokenProvider.createJwtToken(username, user.getRoleType().getCode());
        redisTemplate.opsForValue()
                .set(customRedisProperties.getKey().getRefresh() + ":" + user.getUsername(),
                        reissueToken.getRefreshToken(), jwtTokenProvider.getRefreshTokenExpiredDate(), TimeUnit.MILLISECONDS);
//        refreshToken.updateToken(reissueToken.getRefreshToken());

        return reissueToken;
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ProfileDto profile(String username, Long userId) {
        User me = userRepository.findByUsername(username).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        User other = userRepository.findById(userId).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        Integer followingNum = followRepository.countByFromUser(other);
        Integer followerNum = followRepository.countByToUser(other);
        Boolean isFollow = followRepository.findByFromUserAndToUser(me, other).isPresent();
        List<UserFlagDto> userFlagList = flagRepository.findByUser(other).stream()
                .map(flag -> UserFlagDto.builder()
                        .flagId(flag.getId()).name(flag.getName()).color(flag.getColor())
                        .flagImageList(flag.getFlagBakeryList().stream()
                                .sorted(Comparator.comparing(FlagBakery::getCreatedAt)).limit(3)
                                .map(flagBakery -> flagBakery.getBakery().getImage())
                                .collect(Collectors.toList())).build())
                .collect(Collectors.toList());

        return ProfileDto.builder().user(other)
                .followingNum(followingNum).followerNum(followerNum)
                .userFlagList(userFlagList).isFollow(isFollow).build();
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateNickName(String username, UpdateNickNameRequest request, MultipartFile file) throws IOException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        if (userRepository.findByNickName(request.getNickName()).isEmpty())
            user.updateNickName(request.getNickName());
        else if (!user.equals(userRepository.findByNickName(request.getNickName()).get()))
            throw new DaedongException(DaedongStatus.NICKNAME_DUPLICATE_EXCEPTION);

        if (file != null && !file.isEmpty()) {
            String imagePath = fileConverter.parseFileInfo(file, ImageType.USER_IMAGE, user.getId());
            String image = s3Uploader.upload(file, imagePath);
            user.updateImage(image);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void logout(LogoutRequest request) {
        String accessToken = request.getAccessToken();
        if (!jwtTokenProvider.verifyToken(accessToken)) throw new DaedongException(DaedongStatus.TOKEN_INVALID_EXCEPTION);

        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
        String username = authentication.getName();

        // 알람 가지 않게 처리
        eventPublisher.publishEvent(new NoticeTokenDeleteEvent(username, request.getDeviceToken()));

        // Redis 에서 해당  Refresh Token 이 있는지 여부를 확인 후 있을 경우 삭제
        if (Boolean.TRUE.equals(redisTemplate.hasKey(customRedisProperties.getKey().getRefresh() + ":" + username))) {
            redisTemplate.delete(customRedisProperties.getKey().getRefresh() + ":" + username);
        }

        // 해당 Access Token 남은 유효시간 가지고 와서 BlackList 로 저장하기
        Long expiration = jwtTokenProvider.getExpiration(request.getAccessToken());
        redisTemplate.opsForValue()
                .set(request.getAccessToken(), "logout", expiration, TimeUnit.MILLISECONDS);


    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));

        reviewCommentLikeRepository.deleteByUser(user);
        reviewCommentRepository.deleteByUser(user);
        reviewLikeRepository.deleteByUser(user);
        reviewRepository.findByUser(user).forEach(reviewImageRepository::deleteByReview);
        reviewRepository.deleteByUser(user);

        noticeTokenRepository.deleteByUser(user);
        noticeRepository.deleteByUser(user);

        flagRepository.findByUser(user).forEach(flagBakeryRepository::deleteByFlag);
        flagRepository.deleteByUser(user);

        blockUserRepository.deleteByUser(user);
        blockUserRepository.deleteByBlockUser(user);

        followRepository.deleteByFromUser(user);
        followRepository.deleteByToUser(user);

        userRepository.delete(user);

        redisTemplate.delete(Arrays.asList(customRedisProperties.getKey().getAccess() + ":" + username, customRedisProperties.getKey().getRefresh() + ":" + username));

//        ValueOperations<String, String> redisDeleteUser = redisTemplate.opsForValue();
//        redisDeleteUser.set(customRedisProperties.getKey().getDelete() + ":" + username, username);
//        redisTemplate.expire(customRedisProperties.getKey().getDelete() + ":" + username, 7, TimeUnit.DAYS);
    }

    @Transactional(rollbackFor = Exception.class)
    public void follow(String username, FollowRequest request) {
        User fromUser = userRepository.findByUsername(username).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        User toUser = userRepository.findById(request.getUserId()).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        if(fromUser.equals(toUser)) throw new DaedongException(DaedongStatus.SELF_FOLLOW_EXCEPTION);
        if(followRepository.findByFromUserAndToUser(fromUser, toUser).isPresent()) throw new DaedongException(DaedongStatus.FOLLOW_DUPLICATE_EXCEPTION);
        Follow follow = Follow.builder().fromUser(fromUser).toUser(toUser).build();
        followRepository.save(follow);
        eventPublisher.publishEvent(FollowEvent.builder().userId(toUser.getId()).fromUserId(fromUser.getId()).build());
    }

    @Transactional(rollbackFor = Exception.class)
    public void unfollow(String username, FollowRequest request) {
        User fromUser = userRepository.findByUsername(username).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        User toUser = userRepository.findById(request.getUserId()).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        if(fromUser.equals(toUser)) throw new DaedongException(DaedongStatus.SELF_FOLLOW_EXCEPTION);
        Follow follow = followRepository.findByFromUserAndToUser(fromUser, toUser).orElseThrow(() -> new DaedongException(DaedongStatus.FOLLOW_NOT_FOUND));
        followRepository.delete(follow);
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<FollowUserDto> myFollowerList(String username) { // 나를 팔로우한 사람
        User toUser = userRepository.findByUsername(username).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        return followRepository.findByToUser(toUser).stream()
                .map(follow -> new FollowUserDto(
                        follow.getFromUser(),
                        reviewRepository.countByUser(follow.getFromUser()),
                        followRepository.countByToUser(follow.getFromUser()),
                        followRepository.findByFromUserAndToUser(toUser, follow.getFromUser()).isPresent(), // 내가 팔로우 했는지
                        false
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<FollowUserDto> myFollowingList(String username) { // 내가 팔로우한 사람
        User fromUser = userRepository.findByUsername(username).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        return followRepository.findByFromUser(fromUser).stream()
                .map(follow -> new FollowUserDto(
                        follow.getToUser(),
                        reviewRepository.countByUser(follow.getToUser()),
                        followRepository.countByToUser(follow.getToUser()),
                        followRepository.findByFromUserAndToUser(fromUser, follow.getToUser()).isPresent(), // 내가 팔로우 했는지
                        false
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<FollowUserDto> otherFollowerList(String username, Long userId) { // 타인을 팔로우한 사람
        User me = userRepository.findByUsername(username).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        User toUser = userRepository.findById(userId).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
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
    public List<FollowUserDto> otherFollowingList(String username, Long userId) { // 타인이 팔로우한 사람
        User me = userRepository.findByUsername(username).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        User fromUser = userRepository.findById(userId).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
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

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<BlockUserDto> blockList(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));

        return blockUserRepository.findByUser(user).stream()
                .map(blockUser -> new BlockUserDto(blockUser.getBlockUser(),
                        reviewRepository.countByUser(blockUser.getBlockUser()),
                        followRepository.countByToUser(blockUser.getBlockUser())))
                .collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public void block(String username, BlockRequest request) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        User userToBlock = userRepository.findById(request.getUserId()).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        if(blockUserRepository.findByUserAndBlockUser(user, userToBlock).isPresent()) throw new DaedongException(DaedongStatus.BLOCK_DUPLICATE_EXCEPTION);

        BlockUser blockUser = BlockUser.builder().user(user).blockUser(userToBlock).build();
        blockUserRepository.save(blockUser);
    }

    @Transactional(rollbackFor = Exception.class)
    public void unblock(String username, BlockRequest request) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        User userToUnblock = userRepository.findById(request.getUserId()).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        BlockUser blockUser = blockUserRepository.findByUserAndBlockUser(user, userToUnblock).orElseThrow(() -> new DaedongException(DaedongStatus.BLOCK_NOT_FOUND));

        blockUserRepository.delete(blockUser);
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public AlarmDto getAlarmStatus(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        return new AlarmDto(user.getIsAlarmOn());
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateAlarmStatus(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        user.changeAlarm();
    }
}
