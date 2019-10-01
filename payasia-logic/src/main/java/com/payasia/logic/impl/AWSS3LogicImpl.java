package com.payasia.logic.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AbortMultipartUploadRequest;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest.KeyVersion;
import com.amazonaws.services.s3.model.DeleteObjectsResult;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.MultiObjectDeleteException;
import com.amazonaws.services.s3.model.MultiObjectDeleteException.DeleteError;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.UploadPartRequest;
import com.payasia.logic.AWSS3Logic;

@Component
public class AWSS3LogicImpl implements AWSS3Logic {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger.getLogger(AWSS3LogicImpl.class);

	@Value("#{payasiaptProperties['payasia.document.root.directory']}")
	private String basePath;

	@Value("#{payasiaptProperties['payasia.s3.bucket.name']}")
	private String bucketName;

	@Value("#{payasiaptProperties['payasia.s3.conf.path']}")
	private String confFilePath;

	@Value("#{payasiaptProperties['payasia.s3.expiration.time.millis']}")
	private Long expirationTimeInMillis;

	@Value("#{payasiaptProperties['payasia.s3.auth.enabled']}")
	private boolean shouldAuthenticated;

	private static AmazonS3 s3ClientGlobal = null;

	public AmazonS3 getS3Client() {
		if (s3ClientGlobal == null) {
			if (shouldAuthenticated) {
				s3ClientGlobal = new AmazonS3Client(
						new ProfileCredentialsProvider(confFilePath, "default"));
			} else {
				// s3ClientGlobal = new AmazonS3Client(
				// new ProfileCredentialsProvider());

				s3ClientGlobal = AmazonS3ClientBuilder
						.standard()
						.withCredentials(
								new InstanceProfileCredentialsProvider(false))
						.build();
			}
		}
		return s3ClientGlobal;
	}

	@Override
	public String generateSignedURL(String path) {

		// path = preparedKey(path);
		String urlToReturn = "";
		try {
			AmazonS3 s3Client = getS3Client();
			LOGGER.debug("Generating pre-signed URL.");
			java.util.Date expiration = new java.util.Date();
			long milliSeconds = expiration.getTime();
			milliSeconds += expirationTimeInMillis;
			expiration.setTime(milliSeconds);
			GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(
					bucketName, path);
			generatePresignedUrlRequest.setMethod(HttpMethod.GET);
			generatePresignedUrlRequest.setExpiration(expiration);

			ObjectMetadata objectMetadata = s3Client.getObjectMetadata(
					bucketName, path);
			// objectMetadata.setContentType(contentType);
			// objectMetadata = s3Client.getObjectMetadata(bucketName, path);
			String contentDisposition = objectMetadata.getContentDisposition();
			if (contentDisposition == null || contentDisposition == "") {
				LOGGER.info("Setting contentDisposition for: " + path);
				String fileName = path.substring(path.lastIndexOf("/") + 1);
				objectMetadata.setContentDisposition("attachment;filename="
						+ fileName);
				final CopyObjectRequest request = new CopyObjectRequest(
						bucketName, path, bucketName, path)
						.withSourceBucketName(bucketName).withSourceKey(path)
						.withNewObjectMetadata(objectMetadata);

				s3Client.copyObject(request);
			}
			URL url = s3Client
					.generatePresignedUrl(generatePresignedUrlRequest);
			urlToReturn = url.toString();
			LOGGER.error("URL :" + url);
		} catch (AmazonServiceException exception) {
			String errorMsg = "Path: [" + path + "]" + "Status Code: ["
					+ exception.getStatusCode() + "]" + " Service Name: ["
					+ exception.getServiceName() + "]" + " Error Code: ["
					+ exception.getErrorCode() + "]" + " Error Type: ["
					+ exception.getErrorType() + "]" + " Request ID: ["
					+ exception.getRequestId() + "]";
			LOGGER.error(errorMsg);
		} catch (AmazonClientException ace) {
			LOGGER.error("Caught an AmazonClientException, "
					+ "which means the client encountered "
					+ "an internal error while trying to communicate"
					+ " with S3, "
					+ "such as not being able to access the network.");
			LOGGER.error("Error Message: " + ace.getMessage());
		} catch (Exception ace) {
			LOGGER.error("Exception Message: " + ace.getMessage());
		}
		return urlToReturn;
	}

	@Override
	public void uploadCommonMultipartFile(CommonsMultipartFile multipart,
			String path) {
		try {
			long contentLength = multipart.getSize();
			InputStream inputStream = multipart.getInputStream();
			uploadMultipart(contentLength, inputStream, path);
		} catch (IOException exception) {
			LOGGER.error(exception.getMessage());
		}
	}
	
	@Override
	public void uploadCommonMultipartFile(MultipartFile multipart,
			String path) {
		try {
			long contentLength = multipart.getSize();
			InputStream inputStream = multipart.getInputStream();
			uploadMultipart(contentLength, inputStream, path);
		} catch (IOException exception) {
			LOGGER.error(exception.getMessage());
		}
	}

	@Override
	public void uploadByteArrayFile(byte[] fileInByte, String path) {
		try {
			long contentLength = fileInByte.length;
			InputStream inputStream = new ByteArrayInputStream(fileInByte);
			uploadMultipart(contentLength, inputStream, path);
		} catch (Exception exception) {
			LOGGER.error(exception.getMessage());
		}

	}

	@Override
	public void uploadInputStreamFile(InputStream inputStream, String path) {
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			int b;
			while ((b = inputStream.read()) != -1)
				os.write(b);
			long contentLength = os.size();
			uploadMultipart(contentLength, inputStream, path);
		} catch (Exception exception) {
			LOGGER.error(exception.getMessage());
		}

	}

	private void uploadMultipart(long contentLength, InputStream inputStream,
			String path) {
		// path = preparedKey(path);
		AmazonS3 s3Client = getS3Client();
		// Create a list of UploadPartResponse objects. You get one of these for
		// each part upload.
		List<PartETag> partETags = new ArrayList<PartETag>();
		// File file = new File(uploadFileName);
		// String keyName = getKey(file.getName());

		// Step 1: Initialize.
		InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(
				bucketName, path);
		initRequest.setCannedACL(CannedAccessControlList.PublicRead);
		InitiateMultipartUploadResult initResponse = s3Client
				.initiateMultipartUpload(initRequest);

		// long contentLength = multipart.getSize();

		long partSize = 25 * 1024 * 1024; // Set part size to 25 MB.
		try {
			// InputStream inputStream = multipart.getInputStream();
			// Step 2: Upload parts.
			long filePosition = 0;
			for (int i = 1; filePosition < contentLength; i++) {
				// Last part can be less than 5 MB. Adjust part size.
				partSize = Math.min(partSize, (contentLength - filePosition));

				// Create request to upload a part.
				UploadPartRequest uploadRequest = new UploadPartRequest()
						.withBucketName(bucketName).withKey(path)
						.withUploadId(initResponse.getUploadId())
						.withPartNumber(i).withFileOffset(filePosition)
						.withInputStream(inputStream).withPartSize(partSize);

				// Upload part and add response to our list.
				partETags.add(s3Client.uploadPart(uploadRequest).getPartETag());

				filePosition += partSize;
			}

			// Step 3: Complete.
			CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest(
					bucketName, path, initResponse.getUploadId(), partETags);

			s3Client.completeMultipartUpload(compRequest);
		} catch (Exception exception) {
			exception.printStackTrace();
			s3Client.abortMultipartUpload(new AbortMultipartUploadRequest(
					bucketName, path, initResponse.getUploadId()));
			LOGGER.error(exception.getMessage());
			LOGGER.error("Amazone S3: uploadMultipart exception: ", exception);
		}
	}

	@Override
	public InputStream readS3ObjectAsStream(String path) throws IOException {
	
		URL url = new URL(generateSignedURL(path));
		URLConnection conn = url.openConnection();
		return conn.getInputStream();
	}

	@Override
	public void deleteMultiObjectNonVersioned(List<String> keys) {

		AmazonS3 s3Client = getS3Client();
		// Multi-object delete by specifying only keys (no version ID).
		DeleteObjectsRequest multiObjectDeleteRequest = new DeleteObjectsRequest(
				bucketName).withQuiet(false);

		// Create request that include only object key names.
		List<KeyVersion> justKeys = new ArrayList<KeyVersion>();
		for (String key : keys) {
			// key = preparedKey(key);
			justKeys.add(new KeyVersion(key));
		}
		multiObjectDeleteRequest.setKeys(justKeys);
		// Execute DeleteObjects - Amazon S3 add delete marker for each object
		// deletion. The objects no disappear from your bucket (verify).
		DeleteObjectsResult delObjRes = null;
		try {
			delObjRes = s3Client.deleteObjects(multiObjectDeleteRequest);
			System.out.format("Successfully deleted all the %s items.\n",
					delObjRes.getDeletedObjects().size());
		} catch (MultiObjectDeleteException mode) {
			LOGGER.error(mode.getMessage());
			printDeleteResults(mode);
		}
	}

	private void printDeleteResults(MultiObjectDeleteException mode) {
		LOGGER.debug("%s \n" + mode.getMessage());
		LOGGER.debug("No. of objects successfully deleted = %s\n"
				+ mode.getDeletedObjects().size());
		LOGGER.debug("No. of objects failed to delete = %s\n"
				+ mode.getErrors().size());
		LOGGER.debug("Printing error data...\n");
		for (DeleteError deleteError : mode.getErrors()) {
			LOGGER.debug("Object Key: %s\t%s\t%s\n" + deleteError.getKey()
					+ " : " + deleteError.getCode() + " : "
					+ deleteError.getMessage());
		}
	}

	private String preparedKey(String path) {
		return bucketName + "/" + path;
	}

}
