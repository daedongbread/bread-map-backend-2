package com.depromeet.breadmapbackend.domain.user;

import com.depromeet.breadmapbackend.domain.user.dto.NoticeTokenRequest;
import com.depromeet.breadmapbackend.domain.notice.token.NoticeToken;
import com.depromeet.breadmapbackend.global.converter.FileConverter;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import com.depromeet.breadmapbackend.domain.flag.FlagBakeryRepository;
import com.depromeet.breadmapbackend.domain.flag.FlagRepository;
import com.depromeet.breadmapbackend.domain.notice.token.NoticeTokenDeleteEvent;
import com.depromeet.breadmapbackend.domain.notice.NoticeRepository;
import com.depromeet.breadmapbackend.domain.notice.token.NoticeTokenRepository;
import com.depromeet.breadmapbackend.domain.review.ReviewRepository;
import com.depromeet.breadmapbackend.domain.review.comment.like.ReviewCommentLikeRepository;
import com.depromeet.breadmapbackend.domain.review.comment.ReviewCommentRepository;
import com.depromeet.breadmapbackend.domain.review.ReviewImageRepository;
import com.depromeet.breadmapbackend.domain.review.like.ReviewLikeRepository;
import com.depromeet.breadmapbackend.domain.user.block.BlockUserRepository;
import com.depromeet.breadmapbackend.domain.user.follow.FollowRepository;
import com.depromeet.breadmapbackend.global.infra.properties.CustomRedisProperties;
import com.depromeet.breadmapbackend.global.security.token.JwtToken;
import com.depromeet.breadmapbackend.global.security.token.JwtTokenProvider;
import com.depromeet.breadmapbackend.global.S3Uploader;
import com.depromeet.breadmapbackend.domain.user.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
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
    private final CustomRedisProperties customRedisProperties;

    @Transactional(rollbackFor = Exception.class)
    public JwtToken reissue(ReissueRequest request) {
        if(!jwtTokenProvider.verifyToken(request.getRefreshToken())) throw new DaedongException(DaedongStatus.TOKEN_INVALID_EXCEPTION);

        String accessToken = request.getAccessToken();
        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken, false);
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));

        String refreshToken = redisTemplate.opsForValue().get(customRedisProperties.getKey().getRefresh() + ":" + user.getUsername());
        if (refreshToken == null || !refreshToken.equals(request.getRefreshToken())) throw new DaedongException(DaedongStatus.TOKEN_INVALID_EXCEPTION);

        JwtToken reissueToken = jwtTokenProvider.createJwtToken(username, user.getRoleType().getCode());
        redisTemplate.opsForValue()
                .set(customRedisProperties.getKey().getRefresh() + ":" + user.getUsername(),
                        reissueToken.getRefreshToken(), jwtTokenProvider.getRefreshTokenExpiredDate(), TimeUnit.MILLISECONDS);

        return reissueToken;
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ProfileDto profile(String username, Long userId) {
        User me = userRepository.findByUsername(username).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        User other = userRepository.findById(userId).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        Integer followingNum = followRepository.countByFromUser(other);
        Integer followerNum = followRepository.countByToUser(other);
        Boolean isFollow = followRepository.findByFromUserAndToUser(me, other).isPresent();

        return ProfileDto.builder().user(other).followingNum(followingNum).followerNum(followerNum).isFollow(isFollow).build();
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateNickName(String username, UpdateNickNameRequest request){
        User me = userRepository.findByUsername(username).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        if (request.getNickName() != null) {
            Optional<User> user = userRepository.findByNickName(request.getNickName());
            if (user.isEmpty()) me.updateNickName(request.getNickName());
            else if (!me.equals(user.get()))
                throw new DaedongException(DaedongStatus.NICKNAME_DUPLICATE_EXCEPTION);
        }

        if (request.getImage() != null) me.updateImage(request.getImage());
    }

    @Transactional(rollbackFor = Exception.class)
    public void logout(LogoutRequest request) {
        String accessToken = request.getAccessToken();
        if (!jwtTokenProvider.verifyToken(accessToken)) throw new DaedongException(DaedongStatus.TOKEN_INVALID_EXCEPTION);

        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken, true);
        String username = authentication.getName();

        // 알람 가지 않게 처리
//        eventPublisher.publishEvent(new NoticeTokenDeleteEvent(username, request.getDeviceToken()));
        User user = userRepository.findByUsername(username).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));

        if(noticeTokenRepository.findByUserAndDeviceToken(user, request.getDeviceToken()).isPresent()) {
            noticeTokenRepository.delete(noticeTokenRepository.findByUserAndDeviceToken(user, request.getDeviceToken()).get());
        }

        // Redis 에서 해당  Refresh Token 이 있는지 여부를 확인 후 있을 경우 삭제
        if (Boolean.TRUE.equals(redisTemplate.hasKey(customRedisProperties.getKey().getRefresh() + ":" + username))) {
            redisTemplate.delete(customRedisProperties.getKey().getRefresh() + ":" + username);
        }

        // 해당 Access Token 남은 유효시간 가지고 와서 BlackList 로 저장하기
        Long expiration = jwtTokenProvider.getExpiration(request.getAccessToken());
        redisTemplate.opsForValue()
                .set(request.getAccessToken(), "logout", expiration, TimeUnit.MILLISECONDS);


    }

//    @Transactional(rollbackFor = Exception.class)
//    public void deleteUser(String username) {
//        User user = userRepository.findByUsername(username).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
//
//        reviewCommentLikeRepository.deleteByUser(user);
//        reviewCommentRepository.deleteByUser(user);
//        reviewLikeRepository.deleteByUser(user);
//        reviewRepository.findByUser(user).forEach(reviewImageRepository::deleteByReview);
//        reviewRepository.deleteByUser(user);
//
//        noticeTokenRepository.deleteByUser(user);
//        noticeRepository.deleteByUser(user);
//
//        flagRepository.findByUser(user).forEach(flagBakeryRepository::deleteByFlag);
//        flagRepository.deleteByUser(user);
//
//        blockUserRepository.deleteByUser(user);
//        blockUserRepository.deleteByBlockUser(user);
//
//        followRepository.deleteByFromUser(user);
//        followRepository.deleteByToUser(user);
//
//        userRepository.delete(user);
//
//        redisTemplate.delete(Arrays.asList(customRedisProperties.getKey().getAccess() + ":" + username, customRedisProperties.getKey().getRefresh() + ":" + username));

//        ValueOperations<String, String> redisDeleteUser = redisTemplate.opsForValue();
//        redisDeleteUser.set(customRedisProperties.getKey().getDelete() + ":" + username, username);
//        redisTemplate.expire(customRedisProperties.getKey().getDelete() + ":" + username, 7, TimeUnit.DAYS);
//    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public AlarmDto getAlarmStatus(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        return new AlarmDto(user.getIsAlarmOn());
    }

    @Transactional(rollbackFor = Exception.class)
    public void alarmChange(String username, NoticeTokenRequest request) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        if(!user.getIsAlarmOn()) { // 알림 off이면
            if (noticeTokenRepository.findByUserAndDeviceToken(user, request.getDeviceToken()).isEmpty()) {
                noticeTokenRepository.save(NoticeToken.builder().user(user).deviceToken(request.getDeviceToken()).build());
            }
            user.alarmOn();
        }
        else { // 알림 on이면
            if(noticeTokenRepository.findByUserAndDeviceToken(user, request.getDeviceToken()).isPresent()) {
                noticeTokenRepository.delete(noticeTokenRepository.findByUserAndDeviceToken(user, request.getDeviceToken()).get());
            }
            user.alarmOff();
        }
    }
}
