package com.payasia.common.dto;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;

public class FtpData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private FTPClient ftpClient;
	private Session session;
	private ChannelSftp sftpChannel;
	private List<FtpFileDataDTO> fileDataList;
	
	public FTPClient getFtpClient() {
		return ftpClient;
	}
	public void setFtpClient(FTPClient ftpClient) {
		this.ftpClient = ftpClient;
	}
	public Session getSession() {
		return session;
	}
	public void setSession(Session session) {
		this.session = session;
	}
	public ChannelSftp getSftpChannel() {
		return sftpChannel;
	}
	public void setSftpChannel(ChannelSftp sftpChannel) {
		this.sftpChannel = sftpChannel;
	}
	public List<FtpFileDataDTO> getFileDataList() {
		return fileDataList;
	}
	public void setFileDataList(List<FtpFileDataDTO> fileDataList) {
		this.fileDataList = fileDataList;
	}
}
