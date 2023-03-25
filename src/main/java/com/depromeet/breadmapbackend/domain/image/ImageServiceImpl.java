package com.depromeet.breadmapbackend.domain.image;

import com.depromeet.breadmapbackend.domain.image.dto.ImageDto;
import com.depromeet.breadmapbackend.global.S3Uploader;
import com.depromeet.breadmapbackend.global.converter.FileConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    private final ImageRepository imageRepository;
    private final FileConverter fileConverter;
    private final S3Uploader s3Uploader;

    @Transactional
    public ImageDto uploadImage(MultipartFile image) throws IOException {
        String hashValue = fileConverter.generateImageHash(image);
        String imagePath = "images/" + hashValue + fileConverter.generateImageExtension(image);

        if (imageRepository.findByHashValue(hashValue).isPresent())
            return ImageDto.builder().imagePath(s3Uploader.alreadyUpload(imagePath)).build();

        imageRepository.save(Image.builder().hashValue(hashValue).build());
        return ImageDto.builder().imagePath(s3Uploader.upload(image, imagePath)).build();
    }
}
