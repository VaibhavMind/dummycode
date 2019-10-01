package com.payasia.common.form;

import java.io.Serializable;

public class WorkdayDataFtpConfigForm implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String remotePath;
	private String moveToFolderPath;
	private String frequency;
	private Boolean isActive;
	
	public String getRemotePath() {
		return remotePath;
	}
	public void setRemotePath(String remotePath) {
		this.remotePath = remotePath;
	}
	public String getMoveToFolderPath() {
		return moveToFolderPath;
	}
	public void setMoveToFolderPath(String moveToFolderPath) {
		this.moveToFolderPath = moveToFolderPath;
	}
	public String getFrequency() {
		return frequency;
	}
	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}
	public Boolean getIsActive() {
		return isActive == null ? false : isActive;
	}
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive == null ? false : isActive;
	}
}
