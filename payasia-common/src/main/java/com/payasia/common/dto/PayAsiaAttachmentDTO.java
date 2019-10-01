package com.payasia.common.dto;

import java.io.File;
import java.io.Serializable;

public class PayAsiaAttachmentDTO implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = -4197796819750808053L;
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
