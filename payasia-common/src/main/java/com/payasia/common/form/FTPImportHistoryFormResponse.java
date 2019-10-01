package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

public class FTPImportHistoryFormResponse extends PageResponse implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<FTPImportHistoryForm> ftpImportHistoryList;

	public List<FTPImportHistoryForm> getFtpImportHistoryList() {
		return ftpImportHistoryList;
	}

	public void setFtpImportHistoryList(
			List<FTPImportHistoryForm> ftpImportHistoryList) {
		this.ftpImportHistoryList = ftpImportHistoryList;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
