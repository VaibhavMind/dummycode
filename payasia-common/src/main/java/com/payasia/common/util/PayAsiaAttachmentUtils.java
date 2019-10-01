package com.payasia.common.util;

import java.io.File;
import java.io.Serializable;

public class PayAsiaAttachmentUtils implements Serializable{

	private static final long serialVersionUID = 1L;

	private String fileName;
	private File file;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

}
