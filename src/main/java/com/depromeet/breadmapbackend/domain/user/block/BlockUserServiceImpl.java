package com.depromeet.breadmapbackend.domain.user.block;

import com.depromeet.breadmapbackend.domain.review.ReviewRepository;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.UserRepository;
import com.depromeet.breadmapbackend.domain.user.block.dto.BlockRequest;
import com.depromeet.breadmapbackend.domain.user.block.dto.BlockUserDto;
import com.depromeet.breadmapbackend.domain.user.follow.FollowRepository;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlockUserServiceImpl implements BlockUserService {
    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final ReviewRepository reviewRepository;
    private final BlockUserRepository blockUserRepository;

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<BlockUserDto> blockList(String oAuthId) {
        User me = userRepository.findByOAuthId(oAuthId).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));

        return blockUserRepository.findByFromUser(me).stream()
                .map(blockUser -> new BlockUserDto(blockUser.getToUser(),
                        reviewRepository.countByUser(blockUser.getToUser()),
                        followRepository.countByToUser(blockUser.getToUser())))
                .collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public void block(String oAuthId, BlockRequest request) {
        User me = userRepository.findByOAuthId(oAuthId).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        if (me.equals(user)) throw new DaedongException(DaedongStatus.BLOCK_MYSELF);
        if (blockUserRepository.findByFromUserAndToUser(me, user).isPresent()) throw new DaedongException(DaedongStatus.BLOCK_DUPLICATE_EXCEPTION);

        BlockUser blockUser = BlockUser.builder().fromUser(me).toUser(user).build();
        blockUserRepository.save(blockUser);
    }

    @Transactional(rollbackFor = Exception.class)
    public void unblock(String oAuthId, BlockRequest request) {
        User me = userRepository.findByOAuthId(oAuthId).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        if (me.equals(user)) throw new DaedongException(DaedongStatus.BLOCK_MYSELF);
        BlockUser blockUser = blockUserRepository.findByFromUserAndToUser(me, user).orElseThrow(() -> new DaedongException(DaedongStatus.BLOCK_NOT_FOUND));

        blockUserRepository.delete(blockUser);
    }
}
