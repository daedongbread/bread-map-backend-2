package com.depromeet.breadmapbackend.domain.user;

import com.depromeet.breadmapbackend.domain.user.dto.NoticeTokenRequest;
import com.depromeet.breadmapbackend.domain.notice.token.NoticeToken;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import com.depromeet.breadmapbackend.domain.notice.token.NoticeTokenRepository;
import com.depromeet.breadmapbackend.domain.user.follow.FollowRepository;
import com.depromeet.breadmapbackend.domain.user.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final NoticeTokenRepository noticeTokenRepository;

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ProfileDto profile(String oAuthId, Long userId) {
        User me = userRepository.findByOAuthId(oAuthId).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        User other = userRepository.findById(userId).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        Integer followingNum = followRepository.countByFromUser(other);
        Integer followerNum = followRepository.countByToUser(other);
        Boolean isFollow = followRepository.findByFromUserAndToUser(me, other).isPresent();

        return ProfileDto.builder().user(other).followingNum(followingNum).followerNum(followerNum).isFollow(isFollow).build();
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateNickName(String oAuthId, UpdateNickNameRequest request){
        User me = userRepository.findByOAuthId(oAuthId).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        if (request.getNickName() != null) {
            Optional<User> user = userRepository.findByNickName(request.getNickName());
            if (user.isEmpty()) me.getUserInfo().updateNickName(request.getNickName());
            else if (!me.equals(user.get()))
                throw new DaedongException(DaedongStatus.NICKNAME_DUPLICATE_EXCEPTION);
        }

        if (request.getImage() != null) me.getUserInfo().updateImage(request.getImage());
    }



//    @Transactional(rollbackFor = Exception.class)
//    public void deleteUser(String username) {
//        User user = userRepository.findByOAuthId(oAuthId).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
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
//        redisTemplate.delete(Arrays.asList(customRedisProperties.getKey().getAccess() + ":" + oAuthId, customRedisProperties.getKey().getRefresh() + ":" + username));

//        ValueOperations<String, String> redisDeleteUser = redisTemplate.opsForValue();
//        redisDeleteUser.set(customRedisProperties.getKey().getDelete() + ":" + oAuthId, username);
//        redisTemplate.expire(customRedisProperties.getKey().getDelete() + ":" + oAuthId, 7, TimeUnit.DAYS);
//    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public AlarmDto getAlarmStatus(String oAuthId) {
        User user = userRepository.findByOAuthId(oAuthId).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        return new AlarmDto(user.getIsAlarmOn());
    }

    @Transactional(rollbackFor = Exception.class)
        public AlarmDto alarmChange(String oAuthId, NoticeTokenRequest request) {
        User user = userRepository.findByOAuthId(oAuthId).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        if(!user.getIsAlarmOn()) { // 알림 off이면
            if (noticeTokenRepository.findByUserAndDeviceToken(user, request.getDeviceToken()).isEmpty()) {
                noticeTokenRepository.save(NoticeToken.builder().user(user).deviceToken(request.getDeviceToken()).build());
            }
            return new AlarmDto(user.alarmOn());
        }
        else { // 알림 on이면
            if(noticeTokenRepository.findByUserAndDeviceToken(user, request.getDeviceToken()).isPresent()) {
                noticeTokenRepository.delete(noticeTokenRepository.findByUserAndDeviceToken(user, request.getDeviceToken()).get());
            }
            return new AlarmDto(user.alarmOff());
        }
    }
}
