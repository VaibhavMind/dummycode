package com.payasia.common.util;

import java.io.File;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class CustomMultipartFile {
	CommonsMultipartFile getConvertByteArrToCommonMultipartFile(
			String formFieldName, String fileType, String fileName,
			int fileSize, File file) {
		FileItem fileItem = new DiskFileItem(formFieldName, fileType, true,
				fileName, fileSize, file);
		return new CommonsMultipartFile(fileItem);
	}

}
