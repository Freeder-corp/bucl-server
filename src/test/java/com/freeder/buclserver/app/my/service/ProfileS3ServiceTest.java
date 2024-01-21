package com.freeder.buclserver.app.my.service;

import static org.mockito.BDDMockito.*;

import java.io.IOException;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@ExtendWith(MockitoExtension.class)
class ProfileS3ServiceTest {

	@InjectMocks
	private ProfileS3Service profileS3Service;

	@Mock
	private S3Client s3Client;

	@Test
	void MultiFile로_파일을_받아_S3에_업로드_한다() throws IOException {
		String fileName = "test";
		String originFileName = "test.png";
		MockMultipartFile mockMultipartFile = new MockMultipartFile(
			fileName, originFileName, MediaType.IMAGE_PNG_VALUE, fileName.getBytes());
		PutObjectRequest putObjectRequest = PutObjectRequest.builder()
			.bucket("bucketName")
			.key("s3Key")
			.contentType(mockMultipartFile.getContentType())
			.build();
		RequestBody requestBody = RequestBody.fromInputStream(mockMultipartFile.getInputStream(),
			mockMultipartFile.getSize());
		// given
		willDoNothing().given(s3Client.putObject(putObjectRequest, requestBody));

		// when
		String uploadFileUrl = profileS3Service.uploadFile(mockMultipartFile);

		// then
		Assertions.assertThat(uploadFileUrl).startsWith("https");
	}
}