package com.depromeet.breadmapbackend.domain.user.block;

import com.depromeet.breadmapbackend.domain.user.block.dto.BlockRequest;
import com.depromeet.breadmapbackend.domain.user.block.dto.BlockUserDto;
import com.depromeet.breadmapbackend.global.dto.ApiResponse;
import com.depromeet.breadmapbackend.global.exception.ValidationSequence;
import com.depromeet.breadmapbackend.global.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class BlockUserController {
    private final BlockUserService blockUserService;

    @GetMapping("/block")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<BlockUserDto>> blockList(@CurrentUser String username) {
        return new ApiResponse<>(blockUserService.blockList(username));
    }

    @PostMapping("/block")
    @ResponseStatus(HttpStatus.CREATED)
    public void block(@CurrentUser String username, @RequestBody @Validated(ValidationSequence.class) BlockRequest request) {
        blockUserService.block(username, request);
    }

    @DeleteMapping("/block")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unblock(@CurrentUser String username, @RequestBody @Validated(ValidationSequence.class) BlockRequest request) {
        blockUserService.unblock(username, request);
    }
}
