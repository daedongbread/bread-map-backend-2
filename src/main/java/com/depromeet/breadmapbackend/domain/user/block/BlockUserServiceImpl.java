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
        User me = userRepository.findByUsername(username).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        if (me.equals(user)) throw new DaedongException(DaedongStatus.BLOCK_MYSELF);
        if (blockUserRepository.findByUserAndBlockUser(me, user).isPresent()) throw new DaedongException(DaedongStatus.BLOCK_DUPLICATE_EXCEPTION);

        BlockUser blockUser = BlockUser.builder().user(me).blockUser(user).build();
        blockUserRepository.save(blockUser);
    }

    @Transactional(rollbackFor = Exception.class)
    public void unblock(String username, BlockRequest request) {
        User me = userRepository.findByUsername(username).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        if (me.equals(user)) throw new DaedongException(DaedongStatus.BLOCK_MYSELF);
        BlockUser blockUser = blockUserRepository.findByUserAndBlockUser(me, user).orElseThrow(() -> new DaedongException(DaedongStatus.BLOCK_NOT_FOUND));

        blockUserRepository.delete(blockUser);
    }
}
