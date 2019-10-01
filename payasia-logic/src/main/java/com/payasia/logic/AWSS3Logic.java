package com.payasia.logic;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

public interface AWSS3Logic {

	String generateSignedURL(String objectKey);

	InputStream readS3ObjectAsStream(String objectKey) throws IOException;

	public void deleteMultiObjectNonVersioned(List<String> keys);

	void uploadCommonMultipartFile(CommonsMultipartFile multipart, String path);

	void uploadByteArrayFile(byte[] fileInByte, String path);

	void uploadInputStreamFile(InputStream inputStream, String path);

	void uploadCommonMultipartFile(MultipartFile multipart, String path);
}
