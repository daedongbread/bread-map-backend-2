package com.depromeet.breadmapbackend.domain.image;

import com.depromeet.breadmapbackend.domain.image.dto.ImageDto;
import com.depromeet.breadmapbackend.global.S3Uploader;
import com.depromeet.breadmapbackend.global.converter.FileConverter;
import com.depromeet.breadmapbackend.global.infra.properties.CustomAWSS3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    private final ImageRepository imageRepository;
    private final FileConverter fileConverter;
    private final S3Uploader s3Uploader;
    private final CustomAWSS3Properties customAWSS3Properties;

    @Transactional
    public ImageDto uploadImage(MultipartFile image) throws IOException {
        String hashValue = fileConverter.generateImageHash(image);
        String imagePath = customAWSS3Properties.getDefaultBucket().getImage() + "/" + hashValue + fileConverter.generateImageExtension(image);

        if (imageRepository.findByHashValue(hashValue).isPresent())
            return ImageDto.builder().imagePath(s3Uploader.alreadyUpload(imagePath)).build();

        imageRepository.save(Image.builder().hashValue(hashValue).build());
        return ImageDto.builder().imagePath(s3Uploader.upload(image, imagePath)).build();
    }

    @Transactional
    public List<ImageDto> uploadImages(List<MultipartFile> images) throws IOException {
        List<ImageDto> contents = new ArrayList<>();
        for (MultipartFile image : images) {
            String hashValue = fileConverter.generateImageHash(image);
            String imagePath = customAWSS3Properties.getDefaultBucket().getImage() + "/" + hashValue + fileConverter.generateImageExtension(image);

            if (imageRepository.findByHashValue(hashValue).isPresent())
                contents.add(ImageDto.builder().imagePath(s3Uploader.alreadyUpload(imagePath)).build());
            else {
                imageRepository.save(Image.builder().hashValue(hashValue).build());
                contents.add(ImageDto.builder().imagePath(s3Uploader.upload(image, imagePath)).build());
            }
        }
        return contents;
    }
}
