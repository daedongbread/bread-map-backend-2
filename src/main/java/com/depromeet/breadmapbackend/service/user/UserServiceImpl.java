package com.depromeet.breadmapbackend.service.user;

import com.depromeet.breadmapbackend.domain.flag.repository.FlagBakeryRepository;
import com.depromeet.breadmapbackend.domain.flag.repository.FlagRepository;
import com.depromeet.breadmapbackend.domain.notice.repository.NoticeRepository;
import com.depromeet.breadmapbackend.domain.notice.repository.NoticeTokenRepository;
import com.depromeet.breadmapbackend.domain.review.Review;
import com.depromeet.breadmapbackend.domain.review.repository.ReviewCommentLikeRepository;
import com.depromeet.breadmapbackend.domain.review.repository.ReviewCommentRepository;
import com.depromeet.breadmapbackend.domain.review.repository.ReviewLikeRepository;
import com.depromeet.breadmapbackend.domain.review.repository.ReviewRepository;
import com.depromeet.breadmapbackend.domain.user.BlockUser;
import com.depromeet.breadmapbackend.domain.user.Follow;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.exception.*;
import com.depromeet.breadmapbackend.domain.user.repository.BlockUserRepository;
import com.depromeet.breadmapbackend.domain.user.repository.FollowRepository;
import com.depromeet.breadmapbackend.domain.user.repository.UserRepository;
import com.depromeet.breadmapbackend.security.exception.RefreshTokenNotFoundException;
import com.depromeet.breadmapbackend.security.exception.TokenValidFailedException;
import com.depromeet.breadmapbackend.security.token.JwtToken;
import com.depromeet.breadmapbackend.security.token.JwtTokenProvider;
import com.depromeet.breadmapbackend.security.token.RefreshToken;
import com.depromeet.breadmapbackend.security.token.RefreshTokenRepository;
import com.depromeet.breadmapbackend.web.controller.user.dto.UserReviewDto;
import com.depromeet.breadmapbackend.web.controller.user.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final FollowRepository followRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final ReviewCommentRepository reviewCommentRepository;
    private final ReviewCommentLikeRepository reviewCommentLikeRepository;
    private final NoticeRepository noticeRepository;
    private final NoticeTokenRepository noticeTokenRepository;
    private final FlagRepository flagRepository;
    private final FlagBakeryRepository flagBakeryRepository;
    private final BlockUserRepository blockUserRepository;

    @Transactional
    public JwtToken reissue(TokenRefreshRequest request) {
        if(!jwtTokenProvider.verifyToken(request.getRefreshToken())) throw new TokenValidFailedException();

        String accessToken = request.getAccessToken();
        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        RefreshToken refreshToken = refreshTokenRepository.findByUsername(username).orElseThrow(RefreshTokenNotFoundException::new);
        if(!refreshToken.getToken().equals(request.getRefreshToken())) throw new TokenValidFailedException();
        JwtToken reissueToken = jwtTokenProvider.createJwtToken(username, user.getRoleType().getCode());
        refreshToken.updateToken(reissueToken.getRefreshToken());

        return reissueToken;
    }

    @Transactional(readOnly = true)
    public ProfileDto profile(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        Integer followingNum = followRepository.countByToUser(user);
        Integer followerNum = followRepository.countByFromUser(user);
        List<UserFlagDto> userFlagDtoList = flagRepository.findByUser(user).stream()
                .map(flag -> UserFlagDto.builder()
                        .flagId(flag.getId()).name(flag.getName()).color(flag.getColor())
                        .flagImageList(flag.getFlagBakeryList().stream().limit(3)
                                .map(flagBakery -> flagBakery.getBakery().getImage())
                                .collect(Collectors.toList())).build())
                .collect(Collectors.toList());
        List<UserReviewDto> userReviewDtoList = reviewRepository.findByUser(user)
                .stream().filter(Review::isUse).map(UserReviewDto::new)
                .sorted(Comparator.comparing(UserReviewDto::getId).reversed())
                .collect(Collectors.toList());

        return ProfileDto.builder().user(user)
                .followingNum(followingNum).followerNum(followerNum)
                .userFlagDtoList(userFlagDtoList).userReviewDtoList(userReviewDtoList).build();
    }

    @Transactional
    public void deleteUser(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);

        reviewCommentLikeRepository.deleteByUser(user);
        reviewCommentRepository.deleteByUser(user);
        reviewLikeRepository.deleteByUser(user);
        reviewRepository.findByUser(user);

        noticeTokenRepository.deleteByUser(user);
        noticeRepository.deleteByUser(user);

        flagRepository.findByUser(user).forEach(flagBakeryRepository::deleteByFlag);
        flagRepository.deleteByUser(user);

        blockUserRepository.deleteByUser(user);
        blockUserRepository.deleteByBlockUser(user);

        followRepository.deleteByFromUser(user);
        followRepository.deleteByToUser(user);

        //TODO: access, refresh 처리
    }

    @Transactional
    public void follow(String username, FollowRequest request) {
        User fromUser = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        User toUser = userRepository.findById(request.getUserId()).orElseThrow(UserNotFoundException::new);
        if(followRepository.findByFromUserAndToUser(fromUser, toUser).isPresent()) throw new FollowAlreadyException();
        Follow follow = Follow.builder().fromUser(fromUser).toUser(toUser).build();
        followRepository.save(follow);
    }

    @Transactional
    public void unfollow(String username, FollowRequest request) {
        User fromUser = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        User toUser = userRepository.findById(request.getUserId()).orElseThrow(UserNotFoundException::new);
        Follow follow = followRepository.findByFromUserAndToUser(fromUser, toUser).orElseThrow(FollowNotFoundException::new);
        followRepository.delete(follow);
    }

    @Transactional(readOnly = true)
    public List<SimpleUserDto> followerList(String username) {
        User toUser = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        return followRepository.findByToUser(toUser).stream()
                .map(follow -> new SimpleUserDto(follow.getFromUser(),
                        reviewRepository.countByUser(follow.getFromUser()),
                        followRepository.countByToUser(follow.getFromUser())))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SimpleUserDto> followingList(String username) {
        User fromUser = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        return followRepository.findByToUser(fromUser).stream()
                .map(follow -> new SimpleUserDto(follow.getToUser(),
                        reviewRepository.countByUser(follow.getToUser()),
                        followRepository.countByToUser(follow.getToUser())))
                .collect(Collectors.toList());
    }

    @Override
    public List<SimpleUserDto> blockList(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);

        return blockUserRepository.findByUser(user).stream()
                .map(blockUser -> new SimpleUserDto(blockUser.getBlockUser(),
                        reviewRepository.countByUser(blockUser.getBlockUser()),
                        followRepository.countByToUser(blockUser.getBlockUser())))
                .collect(Collectors.toList());
    }

    @Transactional
    public void block(String username, BlockRequest request) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        User userToBlock = userRepository.findById(request.getUserId()).orElseThrow(UserNotFoundException::new);
        if(blockUserRepository.findByUserAndBlockUser(user, userToBlock).isPresent())
            throw new BlockAlreadyException();

        BlockUser blockUser = BlockUser.builder().user(user).blockUser(userToBlock).build();
        blockUserRepository.save(blockUser);
    }

    @Transactional
    public void unblock(String username, BlockRequest request) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        User userToUnblock = userRepository.findById(request.getUserId()).orElseThrow(UserNotFoundException::new);
        BlockUser blockUser = blockUserRepository.findByUserAndBlockUser(user, userToUnblock).orElseThrow(BlockNotFoundException::new);

        blockUserRepository.delete(blockUser);
    }
}
