package com.depromeet.breadmapbackend.domain.user.block;

import com.depromeet.breadmapbackend.domain.user.block.dto.BlockRequest;
import com.depromeet.breadmapbackend.domain.user.block.dto.BlockUserDto;

import java.util.List;

public interface BlockUserService {
    List<BlockUserDto> blockList(String username);
    void block(String username, BlockRequest request);
    void unblock(String username, BlockRequest request);
}
