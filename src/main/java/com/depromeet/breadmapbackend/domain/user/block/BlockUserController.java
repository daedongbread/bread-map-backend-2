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
    public ApiResponse<List<BlockUserDto>> blockList(@CurrentUser String oAuthId) {
        return new ApiResponse<>(blockUserService.blockList(oAuthId));
    }

    @PostMapping("/block")
    @ResponseStatus(HttpStatus.CREATED)
    public void block(@CurrentUser String oAuthId, @RequestBody @Validated(ValidationSequence.class) BlockRequest request) {
        blockUserService.block(oAuthId, request);
    }

    @DeleteMapping("/block")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unblock(@CurrentUser String oAuthId, @RequestBody @Validated(ValidationSequence.class) BlockRequest request) {
        blockUserService.unblock(oAuthId, request);
    }
}
