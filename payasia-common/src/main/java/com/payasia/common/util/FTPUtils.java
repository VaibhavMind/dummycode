package com.payasia.common.util;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.xml.stream.XMLStreamException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.payasia.common.dto.FtpData;
import com.payasia.common.dto.FtpFileDataDTO;
import com.payasia.common.exception.PayAsiaBusinessException;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.dao.bean.WorkdayFtpConfig;


public class FTPUtils {
	private static final Logger LOGGER = Logger
			.getLogger(FTPUtils.class);
	@Value("#{payasiaptProperties['payasia.home']}")
	
	private String downloadPath;
	
	
	@SuppressWarnings("unchecked")
	public FtpData getFileDataFromSFTPServer(WorkdayFtpConfig ftpConfig, boolean isEmployeeData) throws PayAsiaBusinessException {
		
		FtpData ftpData = new FtpData();
		FtpFileDataDTO ftpXmlFileData=null;
		List<FtpFileDataDTO> filesDataList=new ArrayList<FtpFileDataDTO>();
		String host = ftpConfig.getFtpServerAddress();
		String username = ftpConfig.getUserName();
		String password = ftpConfig.getPassword();
		Integer port = ftpConfig.getFtpPort();
		String remotePath = (isEmployeeData ? ftpConfig.getEmployeeDataRemotePath() : ftpConfig.getPayrollDataRemotePath()).replace("\\", "/");
		Session session = null;
		Channel channel = null;
		ChannelSftp sftpChannel = null;
		try {
			JSch ssh = new JSch();
			/*String ppkFileName=companyId+"."+"ppk";
			String filePath = downloadPath + "/ftpPPK/" + companyId + "/"+ppkFileName;*/
			//ssh.addIdentity(downloadPath);
			session = ssh.getSession(username, host, port);
			session.setPassword(password);
			java.util.Properties config = new java.util.Properties(); 
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);			 
			session.connect();
			channel = session.openChannel("sftp");
			channel.connect();
			sftpChannel = (ChannelSftp) channel;
			sftpChannel.cd(remotePath);
				
			List<String> allFiles=new ArrayList<String>();
			
			Vector<ChannelSftp.LsEntry> xmlFileList = isEmployeeData ? sftpChannel.ls("PA_EMF*.xml") : sftpChannel.ls("PA_PTRX*.xml");
			for(ChannelSftp.LsEntry entry : xmlFileList) {
				allFiles.add(entry.getFilename());
			}
			if(allFiles.size()==0){
				LOGGER.info("SFTP_FILE_NOT_FOUND. No file found in directory: "+remotePath+" for company : "+ftpConfig.getCompany().getCompanyCode());
			}
			for (String fileName : allFiles) {	
				sftpChannel.cd(remotePath);
					 
				InputStream is2 = sftpChannel.get(fileName);
				byte[] buff = new byte[256];
				int bytesRead = 0;
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				while ((bytesRead = is2.read(buff)) != -1) {
					out.write(buff, 0, bytesRead);
				}
				is2.close();
				
				ftpXmlFileData=new FtpFileDataDTO();
				ftpXmlFileData.setData(out.toByteArray());
				ftpXmlFileData.setFileName(fileName);
				ftpXmlFileData.setEmployeeData(isEmployeeData);
				filesDataList.add(ftpXmlFileData);
			}			
		} catch(Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			try {				
				if (channel != null) {
					channel.disconnect();
				}
				session.disconnect();
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
				throw new PayAsiaSystemException(e.getMessage(), e);
			}
			throw new PayAsiaSystemException(ex.getMessage(), ex);
		}
		ftpData.setFileDataList(filesDataList);
		ftpData.setSession(session);
		ftpData.setSftpChannel(sftpChannel);
		return ftpData;
	}

	public static List<FtpFileDataDTO> getEmployeeData() throws IOException, XMLStreamException  {
		
		List<FtpFileDataDTO> ftpXmlFileDataList = new ArrayList<>();
		FileInputStream is = new FileInputStream("C:/empdataxmlsample3.xml");
		byte[] buff = new byte[256];
		int bytesRead = 0;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		while ((bytesRead = is.read(buff)) != -1) {
			out.write(buff, 0, bytesRead);
		}
		
	//	Map<String,Map<String,List<Map<String,String>>>> employeesListsMap = 
	//			new FTPUtils().getEmployeeListFromByteArray(out.toByteArray(), "Employee_ID");
		
		FtpFileDataDTO f = new FtpFileDataDTO();
		f.setData(out.toByteArray());
		f.setEmployeeData(true);
		ftpXmlFileDataList.add(f);
		is.close();
		return ftpXmlFileDataList;
	}
	
	
	public void moveXmlAfterReading(WorkdayFtpConfig ftpConfig, FtpFileDataDTO ftpXmlFileData, boolean isEmployeeData, ChannelSftp sftpChannel,
			Session session, boolean toBeClosed) {
		
		try {
			String importType;
			String remotePath;
			String moveToFolderPath;
			if(isEmployeeData) {
				importType = "EMF";
				remotePath = ftpConfig.getEmployeeDataRemotePath().replace("\\", "/");
				moveToFolderPath = ftpConfig.getEmployeeDataMoveToFolderPath().replace("\\", "/");
			} else {
				importType = "PTRX";
				remotePath = ftpConfig.getPayrollDataRemotePath().replace("\\", "/");
				moveToFolderPath =  ftpConfig.getPayrollDataMoveToFolderPath().replace("\\", "/");
			}
			LOGGER.info("WD "+importType+ "Import : Moving File : "+remotePath + "/" + ftpXmlFileData.getFileName()+" to "+moveToFolderPath + "/" + ftpXmlFileData.getFileName());
			sftpChannel.cd(remotePath);
			// makeDirectoriesSFTP(moveToFolderPath, sftpChannel);
			sftpChannel.rename(remotePath + "/" + ftpXmlFileData.getFileName(),	moveToFolderPath + "/" + ftpXmlFileData.getFileName());
			LOGGER.info("WD "+importType+ "Import : File Movement completed.");
			/*try {
				sftpChannel.rm(remotePath + "/" + ftpXmlFileData.getFileName());
			} catch (SftpException e) {
				LOGGER.error(e.getMessage(), e);
			}*/
				
		} catch (Exception e) {
			LOGGER.error("Error while moving PICOF XML File. "+e.getMessage(), e);
		} if(toBeClosed) {
			if(sftpChannel != null)
				sftpChannel.disconnect();
			session.disconnect();
		}
	}
}
