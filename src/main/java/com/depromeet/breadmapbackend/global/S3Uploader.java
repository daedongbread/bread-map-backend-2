package com.depromeet.breadmapbackend.global;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.depromeet.breadmapbackend.global.infra.properties.CustomAWSS3Properties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3Uploader {
	private final AmazonS3Client amazonS3Client;
	private final CustomAWSS3Properties customAwss3Properties;

	public String alreadyUpload(String fileName) {
		return customAwss3Properties.getCloudFront() + "/" + fileName;
	}

	public String upload(MultipartFile multipartFile, String fileName) throws IOException {
		log.info("originName : \"{}\", filePath : \"{}\"", multipartFile.getOriginalFilename(), fileName);
		newUpload(multipartFile, fileName);
		return customAwss3Properties.getCloudFront() + "/" + fileName;
	}

	// S3로 파일 업로드하기
	private String newUpload(MultipartFile multipartFile, String fileName) throws IOException {
		return newPutS3(multipartFile, fileName); // s3로 업로드
	}

	// S3로 업로드
	private String newPutS3(MultipartFile multipartFile, String fileName) throws IOException {
		// final TransferManager transferManager =
		// 	TransferManagerBuilder.standard().withS3Client(amazonS3Client).build();

		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentType(multipartFile.getContentType());
		metadata.setContentLength(multipartFile.getSize());
		amazonS3Client.putObject(
			new PutObjectRequest(customAwss3Properties.getBucket(), fileName, multipartFile.getInputStream(),
				metadata));

		// final PutObjectRequest putObjectRequest = new PutObjectRequest(customAwss3Properties.getBucket(), fileName,
		// 	multipartFile.getInputStream(), metadata)
		// 	.withCannedAcl(CannedAccessControlList.PublicRead);

		// transferManager.upload(putObjectRequest);

		log.info("File putS3 success");
		return get(fileName);
	}

	public String get(String fileName) {
		return customAwss3Properties.getCloudFront() + "/" + fileName;
	}

	// delete file
	public void deleteFileS3(String fileName) {
		if (StringUtils.hasText(fileName)) {
			fileName = fileName.replace(customAwss3Properties.getCloudFront() + "/", "");
			log.info("delete file : " + fileName);
			if (amazonS3Client.doesObjectExist(customAwss3Properties.getBucket(), fileName))
				amazonS3Client.deleteObject(customAwss3Properties.getBucket(), fileName);
		}
	}

}
