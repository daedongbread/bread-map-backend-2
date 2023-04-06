package com.depromeet.breadmapbackend.domain.image;

import com.depromeet.breadmapbackend.domain.image.dto.ImageDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ImageService {
    ImageDto uploadImage(MultipartFile image) throws IOException;

    List<ImageDto> uploadImages(List<MultipartFile> images) throws IOException;
}
