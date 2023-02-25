package com.depromeet.breadmapbackend.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.depromeet.breadmapbackend.infra.properties.CustomAWSS3Properties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3Uploader {
    private final AmazonS3Client amazonS3Client;
    private final CustomAWSS3Properties customAwss3Properties;


    public String upload(MultipartFile multipartFile, String fileName) throws IOException {
        log.info("upload file : " + multipartFile.getOriginalFilename());
//        File uploadFile = convert(multipartFile)  // 파일 변환할 수 없으면 에러
//                .orElseThrow(() -> new IllegalArgumentException("error: MultipartFile -> File convert fail"));
//        upload(uploadFile, fileName);
        newUpload(multipartFile, fileName);
        return customAwss3Properties.getCloudFront() + "/" + fileName;
    }

    // S3로 파일 업로드하기
    private String upload(File uploadFile, String fileName) {
        String uploadImageUrl = putS3(uploadFile, fileName); // s3로 업로드
        removeNewFile(uploadFile);
        return uploadImageUrl;
    }

    // S3로 파일 업로드하기
    private String newUpload(MultipartFile multipartFile, String fileName) throws IOException {
        return newPutS3(multipartFile, fileName); // s3로 업로드
    }

    // S3로 업로드
    private String putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(new PutObjectRequest(customAwss3Properties.getBucket(), fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
        log.info("File putS3 success");
        return amazonS3Client.getUrl(customAwss3Properties.getBucket(), fileName).toString();
    }

    // S3로 업로드
    private String newPutS3(MultipartFile multipartFile, String fileName) throws IOException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(multipartFile.getContentType());
        metadata.setContentLength(multipartFile.getSize());
        amazonS3Client.putObject(
                new PutObjectRequest(customAwss3Properties.getBucket(), fileName, multipartFile.getInputStream(), metadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
        log.info("File putS3 success");
        return amazonS3Client.getUrl(customAwss3Properties.getBucket(), fileName).toString();
    }

    public void copy(String oldSource, String newSource) {
        amazonS3Client.copyObject(customAwss3Properties.getBucket(), oldSource, customAwss3Properties.getBucket(), newSource);
    }

    public String get(String fileName) {
        return customAwss3Properties.getCloudFront() + "/" + fileName;
    }

    // get file
    private String getS3(String fileName) {
        log.info("get file : " + fileName);
        return amazonS3Client.getUrl(customAwss3Properties.getCloudFront(), fileName).toString();
    }

    /*
     * 파일 다운로드
     */
    public byte[] getObject(String fileName) throws IOException{
        fileName = fileName.replace(customAwss3Properties.getCloudFront() + "/", "");
        log.info("Download file : " + fileName);
        S3Object o = amazonS3Client.getObject(new GetObjectRequest(customAwss3Properties.getBucket(), fileName));
        S3ObjectInputStream objectInputStream = o.getObjectContent();
        return IOUtils.toByteArray(objectInputStream);
    }

    // delete file
    public void deleteFileS3(String fileName) {
        if(fileName != null) {
            fileName = fileName.replace(customAwss3Properties.getCloudFront() + "/", "");
            log.info("delete file : " + fileName);
            if (amazonS3Client.doesObjectExist(customAwss3Properties.getBucket(), fileName))
                amazonS3Client.deleteObject(customAwss3Properties.getBucket(), fileName);
        }
    }

    // delete file
    public void deleteFolderS3(String folderName) {
        log.info("delete folder : " + folderName);
        if (amazonS3Client.listObjectsV2(customAwss3Properties.getBucket(), folderName).getKeyCount() > 0) {
            //        ObjectListing objects = amazonS3Client.listObjects(bucket, folderName);
            ListObjectsRequest listObject = new ListObjectsRequest();
            listObject.setBucketName(customAwss3Properties.getBucket());
            listObject.setPrefix(folderName);

            ObjectListing objects;
            do {
                objects = amazonS3Client.listObjects(listObject);
                //1000개 단위로 읽음
                for (S3ObjectSummary objectSummary : objects.getObjectSummaries()) {
                    log.info("key : " + objectSummary.getKey());
                    amazonS3Client.deleteObject(customAwss3Properties.getBucket(), objectSummary.getKey());
                }
                //objects = s3.listNextBatchOfObjects(objects); <--이녀석은 1000개 단위로만 가져옴..
                listObject.setMarker(objects.getNextMarker());
            } while (objects.isTruncated());
        }
    }

    // update file
    public String updateS3(MultipartFile multipartFile, String oldFileName, String newFileName) throws IOException {
        deleteFileS3(oldFileName);
        return upload(multipartFile, newFileName);
    }

    // 로컬에 저장된 이미지 지우기
    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("File delete success");
            return;
        }
        log.info("File delete fail");
    }

    // 로컬에 파일 업로드 하기
    private Optional<File> convert(MultipartFile file) throws IOException {
        File convertFile = new File(System.getProperty("user.dir") + "/" + file.getOriginalFilename());
        if (convertFile.createNewFile()) { // 바로 위에서 지정한 경로에 File이 생성됨 (경로가 잘못되었다면 생성 불가능)
            try (FileOutputStream fos = new FileOutputStream(convertFile)) { // FileOutputStream 데이터를 파일에 바이트 스트림으로 저장하기 위함
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }
        return Optional.empty();
    }
}
