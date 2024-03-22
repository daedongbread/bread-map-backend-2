package com.depromeet.breadmapbackend.domain.challenge.breaddiary;

import com.depromeet.breadmapbackend.domain.challenge.breaddiary.dto.AddBreadDiaryDto;
import com.depromeet.breadmapbackend.domain.challenge.breaddiary.dto.AddBreadDiaryRequest;
import com.depromeet.breadmapbackend.global.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RequestMapping("/v1/bread-diary")
@RestController
public class BreadDiaryController {

    private final BreadDiaryService breadDiaryService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void addBreadDiary(@CurrentUser String oAuthId, @Valid @RequestBody AddBreadDiaryRequest request) {
        breadDiaryService.addBreadDiary(
                new AddBreadDiaryDto(
                        oAuthId,
                        request.image(),
                        request.bakeryId(),
                        request.productId(),
                        request.rating(),
                        request.bakeryTags(),
                        request.productTags()));
    }

}
