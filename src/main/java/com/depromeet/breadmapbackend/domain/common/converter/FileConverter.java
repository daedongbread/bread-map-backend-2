package com.depromeet.breadmapbackend.domain.common.converter;

import com.depromeet.breadmapbackend.domain.common.ImageFolderPath;
import com.depromeet.breadmapbackend.domain.exception.ImageInvalidException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
@Component
public class FileConverter {
    public String parseFileInfo(MultipartFile multipartFile, ImageFolderPath path, Long id) {
        String contentType = multipartFile.getContentType();
        String originalFileExtension;
        if (!StringUtils.hasText(contentType)) throw new ImageInvalidException();

        if(contentType.contains("image/jpeg") || contentType.contains("image/jpg")){
            originalFileExtension = ".jpg";
        }
        else if(contentType.contains("image/png")){
            originalFileExtension = ".png";
        }
        else if(contentType.contains("image/gif")) {
            originalFileExtension = ".gif";
        } else throw new ImageInvalidException();

        // 파일 이름
        String fileName = UUID.randomUUID() + originalFileExtension;
        // 파일 위치
        String filePath = path + "/" + id + "/" + fileName; // ex) bakeryImage/1/random_name.jpg
        log.info("profileImage : origName : \"{}\", fileName : \"{}\", filePath : \"{}\"",
                multipartFile.getOriginalFilename(), fileName, filePath);
        return filePath;
    }
}
