package com.depromeet.breadmapbackend.domain.common.converter;

import com.depromeet.breadmapbackend.domain.admin.AdminBakeryImageType;
import com.depromeet.breadmapbackend.domain.common.ImageType;
import com.depromeet.breadmapbackend.domain.exception.DaedongException;
import com.depromeet.breadmapbackend.domain.exception.DaedongStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
@Component
public class FileConverter {
    public String parseFileInfo(MultipartFile multipartFile, ImageType imageType, Long id) {
        String contentType = multipartFile.getContentType();
        String originalFileExtension;
        if (!StringUtils.hasText(contentType)) throw new DaedongException(DaedongStatus.IMAGE_INVALID_EXCEPTION);

        if(contentType.contains("image/jpeg") || contentType.contains("image/jpg")){
            originalFileExtension = ".jpg";
        }
        else if(contentType.contains("image/png")){
            originalFileExtension = ".png";
        }
        else if(contentType.contains("image/gif")) {
            originalFileExtension = ".gif";
        } else throw new DaedongException(DaedongStatus.IMAGE_INVALID_EXCEPTION);

        // 파일 이름
        String fileName = UUID.randomUUID() + originalFileExtension;
        // 파일 위치
        String filePath = imageType.getCode() + "/" + id + "/" + fileName; // ex) bakeryImage/1/random_name.jpg
        log.info("origName : \"{}\", fileName : \"{}\", filePath : \"{}\"",
                multipartFile.getOriginalFilename() + originalFileExtension, fileName, filePath);
        return filePath;
    }
}
