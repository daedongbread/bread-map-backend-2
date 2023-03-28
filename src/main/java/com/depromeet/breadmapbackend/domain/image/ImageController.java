package com.depromeet.breadmapbackend.domain.image;

import com.depromeet.breadmapbackend.domain.image.dto.ImageDto;
import com.depromeet.breadmapbackend.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/v1/images")
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ImageDto> uploadImage(@RequestPart MultipartFile image) throws IOException { // TODO 비동기
        return new ApiResponse<>(imageService.uploadImage(image));
    }
}
