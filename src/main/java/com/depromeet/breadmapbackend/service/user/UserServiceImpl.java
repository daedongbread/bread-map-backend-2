package com.depromeet.breadmapbackend.service.user;

import com.depromeet.breadmapbackend.domain.common.ImageType;
import com.depromeet.breadmapbackend.domain.common.converter.FileConverter;
import com.depromeet.breadmapbackend.domain.flag.repository.FlagBakeryRepository;
import com.depromeet.breadmapbackend.domain.flag.repository.FlagRepository;
import com.depromeet.breadmapbackend.domain.notice.NoticeTokenDeleteEvent;
import com.depromeet.breadmapbackend.domain.notice.repository.NoticeRepository;
import com.depromeet.breadmapbackend.domain.notice.repository.NoticeTokenRepository;
import com.depromeet.breadmapbackend.domain.review.ReviewStatus;
import com.depromeet.breadmapbackend.domain.review.repository.*;
import com.depromeet.breadmapbackend.domain.user.BlockUser;
import com.depromeet.breadmapbackend.domain.user.Follow;
import com.depromeet.breadmapbackend.domain.user.FollowEvent;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.exception.*;
import com.depromeet.breadmapbackend.domain.user.repository.BlockUserRepository;
import com.depromeet.breadmapbackend.domain.user.repository.FollowRepository;
import com.depromeet.breadmapbackend.domain.user.repository.UserRepository;
import com.depromeet.breadmapbackend.security.exception.TokenValidFailedException;
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

    @Value("${spring.redis.key.delete}")
    private String REDIS_KEY_DELETE;

    @Value("${spring.redis.key.refresh}")
    private String REDIS_KEY_REFRESH;

    @Value("${spring.redis.key.access}")
    private String REDIS_KEY_ACCESS;

    @Transactional(rollbackFor = Exception.class)
    public JwtToken reissue(ReissueRequest request) {
        if(!jwtTokenProvider.verifyToken(request.getRefreshToken())) throw new TokenValidFailedException();

        String accessToken = request.getAccessToken();
        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);

        String refreshToken = redisTemplate.opsForValue().get(REDIS_KEY_REFRESH + ":" + user.getUsername());
        if (refreshToken == null || !refreshToken.equals(request.getRefreshToken())) throw new TokenValidFailedException();
//        RefreshToken refreshToken = refreshTokenRepository.findByUsername(username).orElseThrow(RefreshTokenNotFoundException::new);
//        if(!refreshToken.getToken().equals(request.getRefreshToken())) throw new TokenValidFailedException();

        JwtToken reissueToken = jwtTokenProvider.createJwtToken(username, user.getRoleType().getCode());
        redisTemplate.opsForValue()
                .set(REDIS_KEY_REFRESH + ":" + user.getUsername(),
                        reissueToken.getRefreshToken(), jwtTokenProvider.getRefreshTokenExpiredDate(), TimeUnit.MILLISECONDS);
//        refreshToken.updateToken(reissueToken.getRefreshToken());

        return reissueToken;
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ProfileDto myProfile(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);

        Integer followingNum = followRepository.countByToUser(user);
        Integer followerNum = followRepository.countByFromUser(user);

        List<UserFlagDto> userFlagList = flagRepository.findByUser(user).stream()
                .map(flag -> UserFlagDto.builder()
                        .flagId(flag.getId()).name(flag.getName()).color(flag.getColor())
                        .flagImageList(flag.getFlagBakeryList().stream().limit(3)
                                .map(flagBakery -> flagBakery.getBakery().getImage())
                                .collect(Collectors.toList())).build())
                .collect(Collectors.toList());
        List<UserReviewDto> userReviewList = reviewRepository.findByUser(user)
                .stream().filter(rv -> rv.getStatus().equals(ReviewStatus.UNBLOCK))
                .map(UserReviewDto::new)
                .sorted(Comparator.comparing(UserReviewDto::getId).reversed())
                .collect(Collectors.toList());

        return ProfileDto.builder().user(user)
                .followingNum(followingNum).followerNum(followerNum)
                .userFlagList(userFlagList).userReviewList(userReviewList).isFollow(false).build();
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ProfileDto otherProfile(String username, Long userId) {
        User me = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        User other = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Integer followingNum = followRepository.countByToUser(other);
        Integer followerNum = followRepository.countByFromUser(other);
        Boolean isFollow = followRepository.findByFromUserAndToUser(me, other).isPresent();
        List<UserFlagDto> userFlagList = flagRepository.findByUser(other).stream()
                .map(flag -> UserFlagDto.builder()
                        .flagId(flag.getId()).name(flag.getName()).color(flag.getColor())
                        .flagImageList(flag.getFlagBakeryList().stream().limit(3)
                                .map(flagBakery -> flagBakery.getBakery().getImage())
                                .collect(Collectors.toList())).build())
                .collect(Collectors.toList());
        List<UserReviewDto> userReviewList = reviewRepository.findByUser(other)
                .stream().filter(rv -> rv.getStatus().equals(ReviewStatus.UNBLOCK))
                .map(UserReviewDto::new)
                .sorted(Comparator.comparing(UserReviewDto::getId).reversed())
                .collect(Collectors.toList());

        return ProfileDto.builder().user(other)
                .followingNum(followingNum).followerNum(followerNum)
                .userFlagList(userFlagList).userReviewList(userReviewList).isFollow(isFollow).build();
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateNickName(String username, UpdateNickNameRequest request, MultipartFile file) throws IOException {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        if (userRepository.findByNickName(request.getNickName()).isEmpty()) {
            user.updateNickName(request.getNickName());
        }
        else throw new NickNameAlreadyException();

        if (!file.isEmpty()) {
            String imagePath = fileConverter.parseFileInfo(file, ImageType.USER_IMAGE, user.getId());
            String image = s3Uploader.upload(file, imagePath);
            user.updateImage(image);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void logout(LogoutRequest request) {
        String accessToken = request.getAccessToken();
        if (!jwtTokenProvider.verifyToken(accessToken)) throw new TokenValidFailedException();

        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
        String username = authentication.getName();

        // 알람 가지 않게 처리
        eventPublisher.publishEvent(new NoticeTokenDeleteEvent(username, request.getDeviceToken()));

        // Redis 에서 해당  Refresh Token 이 있는지 여부를 확인 후 있을 경우 삭제
        if (Boolean.TRUE.equals(redisTemplate.hasKey(REDIS_KEY_REFRESH + ":" + username))) {
            redisTemplate.delete(REDIS_KEY_REFRESH + ":" + username);
        }

        // 해당 Access Token 남은 유효시간 가지고 와서 BlackList 로 저장하기
        Long expiration = jwtTokenProvider.getExpiration(request.getAccessToken());
        redisTemplate.opsForValue()
                .set(request.getAccessToken(), "logout", expiration, TimeUnit.MILLISECONDS);


    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);

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

        redisTemplate.delete(Arrays.asList(REDIS_KEY_ACCESS + ":" + username, REDIS_KEY_REFRESH + ":" + username));

//        ValueOperations<String, String> redisDeleteUser = redisTemplate.opsForValue();
//        redisDeleteUser.set(REDIS_KEY_DELETE + ":" + username, username);
//        redisTemplate.expire(REDIS_KEY_DELETE + ":" + username, 7, TimeUnit.DAYS);
    }

    @Transactional(rollbackFor = Exception.class)
    public void follow(String username, FollowRequest request) {
        User fromUser = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        User toUser = userRepository.findById(request.getUserId()).orElseThrow(UserNotFoundException::new);
        if(followRepository.findByFromUserAndToUser(fromUser, toUser).isPresent()) throw new FollowAlreadyException();
        Follow follow = Follow.builder().fromUser(fromUser).toUser(toUser).build();
        followRepository.save(follow);
        eventPublisher.publishEvent(FollowEvent.builder().userId(toUser.getId()).fromUserId(fromUser.getId()).build());
    }

    @Transactional(rollbackFor = Exception.class)
    public void unfollow(String username, FollowRequest request) {
        User fromUser = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        User toUser = userRepository.findById(request.getUserId()).orElseThrow(UserNotFoundException::new);
        Follow follow = followRepository.findByFromUserAndToUser(fromUser, toUser).orElseThrow(FollowNotFoundException::new);
        followRepository.delete(follow);
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<SimpleUserDto> followerList(String username) {
        User toUser = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        return followRepository.findByToUser(toUser).stream()
                .map(follow -> new SimpleUserDto(follow.getFromUser(),
                        reviewRepository.countByUser(follow.getFromUser()),
                        followRepository.countByToUser(follow.getFromUser())))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<SimpleUserDto> followingList(String username) {
        User fromUser = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        return followRepository.findByToUser(fromUser).stream()
                .map(follow -> new SimpleUserDto(follow.getToUser(),
                        reviewRepository.countByUser(follow.getToUser()),
                        followRepository.countByToUser(follow.getToUser())))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<SimpleUserDto> blockList(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);

        return blockUserRepository.findByUser(user).stream()
                .map(blockUser -> new SimpleUserDto(blockUser.getBlockUser(),
                        reviewRepository.countByUser(blockUser.getBlockUser()),
                        followRepository.countByToUser(blockUser.getBlockUser())))
                .collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public void block(String username, BlockRequest request) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        User userToBlock = userRepository.findById(request.getUserId()).orElseThrow(UserNotFoundException::new);
        if(blockUserRepository.findByUserAndBlockUser(user, userToBlock).isPresent())
            throw new BlockAlreadyException();

        BlockUser blockUser = BlockUser.builder().user(user).blockUser(userToBlock).build();
        blockUserRepository.save(blockUser);
    }

    @Transactional(rollbackFor = Exception.class)
    public void unblock(String username, BlockRequest request) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        User userToUnblock = userRepository.findById(request.getUserId()).orElseThrow(UserNotFoundException::new);
        BlockUser blockUser = blockUserRepository.findByUserAndBlockUser(user, userToUnblock).orElseThrow(BlockNotFoundException::new);

        blockUserRepository.delete(blockUser);
    }
}
