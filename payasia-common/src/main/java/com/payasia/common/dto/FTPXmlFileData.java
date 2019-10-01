package com.payasia.common.dto;

import org.apache.commons.net.ftp.FTPClient;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;

public class FTPXmlFileData {
	private byte[] employeeDetails;
	private String fileName;
	private FTPClient ftpClient;
	private Session session;
	private Channel channel;
	private ChannelSftp sftpChannel;
	
	public ChannelSftp getSftpChannel() {
		return sftpChannel;
	}
	public void setSftpChannel(ChannelSftp sftpChannel) {
		this.sftpChannel = sftpChannel;
	}
	public Session getSession() {
		return session;
	}
	public void setSession(Session session) {
		this.session = session;
	}
	public Channel getChannel() {
		return channel;
	}
	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	public FTPClient getFtpClient() {
		return ftpClient;
	}
	public void setFtpClient(FTPClient ftpClient) {
		this.ftpClient = ftpClient;
	}
	public byte[] getEmployeeDetails() {
		return employeeDetails;
	}
	public void setEmployeeDetails(byte[] employeeDetails) {
		this.employeeDetails = employeeDetails;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	

}
