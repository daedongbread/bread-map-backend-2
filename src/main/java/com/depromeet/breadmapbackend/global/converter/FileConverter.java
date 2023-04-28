package com.depromeet.breadmapbackend.global.converter;

import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import com.google.common.hash.Hashing;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Component
public class FileConverter {
//    public String parseFileInfo(MultipartFile multipartFile, ImageType imageType, Long id) {
//        String contentType = multipartFile.getContentType();
//        String originalFileExtension;
//        if (!StringUtils.hasText(contentType)) throw new DaedongException(DaedongStatus.IMAGE_INVALID_EXCEPTION);
//
//        if(contentType.contains("image/jpeg") || contentType.contains("image/jpg")){
//            originalFileExtension = ".jpg";
//        }
//        else if(contentType.contains("image/png")){
//            originalFileExtension = ".png";
//        }
//        else if(contentType.contains("image/gif")) {
//            originalFileExtension = ".gif";
//        } else throw new DaedongException(DaedongStatus.IMAGE_INVALID_EXCEPTION);
//
//        // 파일 이름
//        String fileName = UUID.randomUUID() + originalFileExtension;
//        // 파일 위치
//        String filePath = imageType.getCode() + "/" + id + "/" + fileName; // ex) bakeryImage/1/random_name.jpg
//        log.info("origName : \"{}\", fileName : \"{}\", filePath : \"{}\"",
//                multipartFile.getOriginalFilename() + originalFileExtension, fileName, filePath);
//        return filePath;
//    }

    public String generateImageHash(MultipartFile image) throws IOException {
        byte[] bytes = IOUtils.toByteArray(image.getInputStream());
        return Hashing.murmur3_128().hashBytes(bytes).toString();
    }

    public String generateImageExtension(MultipartFile image){
        String contentType = image.getContentType();
        String imageExtension;
        if (!StringUtils.hasText(contentType)) throw new DaedongException(DaedongStatus.IMAGE_INVALID_EXCEPTION);

        if(contentType.contains("image/jpeg") || contentType.contains("image/jpg")){
            imageExtension = ".jpg";
        }
        else if(contentType.contains("image/png")){
            imageExtension = ".png";
        }
        else if(contentType.contains("image/gif")) {
            imageExtension = ".gif";
        } else throw new DaedongException(DaedongStatus.IMAGE_INVALID_EXCEPTION);

        return imageExtension;
    }
}
