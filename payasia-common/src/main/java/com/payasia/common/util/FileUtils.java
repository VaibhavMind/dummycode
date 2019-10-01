package com.payasia.common.util;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.payasia.common.dto.CompanyDocumentFileDTO;
import com.payasia.common.dto.FilePathGeneratorDTO;
import com.payasia.common.exception.PayAsiaSystemException;

import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicException;
import net.sf.jmimemagic.MagicMatch;
import net.sf.jmimemagic.MagicMatchNotFoundException;
import net.sf.jmimemagic.MagicParseException;
@Component
public class FileUtils {

	@Value("#{payasiaptProperties['payasia.document.path.separator']}")
	private String docPathSeperator;
	
	@Value("#{payasiaptProperties['payasia.application.deployment.location']}")
	private String appDeployLocation;

	/** The Constant logger. */
	private static final Logger LOGGER = Logger.getLogger(FileUtils.class);

	public static void uploadFileTaxPaySlip(CommonsMultipartFile file,
			String fileName, String filePath) {

		File folder = new File(filePath);
		if (!folder.exists()) {
			folder.mkdirs();
		}

		if (!("").equals(fileName)) {

			LOGGER.debug("Server path:" + filePath);
			File newFile = new File(filePath, fileName);

			try {
				FileOutputStream fos = new FileOutputStream(newFile);
				fos.write(file.getBytes());
				fos.flush();
				fos.close();
			} catch (IOException iOException) {
				LOGGER.error(iOException.getMessage(), iOException);
				throw new PayAsiaSystemException(iOException.getMessage(),
						iOException);
			}

		}

	}

	public static String uploadFile(CommonsMultipartFile file, String filePath,
			String fileNameSeperator) {

		File folder = new File(filePath);
		if (!folder.exists()) {
			folder.mkdirs();
		}
		String fileName = file.getOriginalFilename();
		String ext = fileName.substring(fileName.lastIndexOf('.') + 1);

		if (!("").equals(fileName)) {
			UUID uuid = UUID.randomUUID();
			fileName = fileName.substring(0, fileName.indexOf('.'))
					+ fileNameSeperator + uuid + "." + ext;
			File newFile = new File(filePath, fileName);

			try {
				FileOutputStream fos = new FileOutputStream(newFile);
				fos.write(file.getBytes());
				fos.flush();
				fos.close();
			} catch (IOException iOException) {
				LOGGER.error(iOException.getMessage(), iOException);
				throw new PayAsiaSystemException(iOException.getMessage(),
						iOException);
			}

		}
		return fileName;

	}

	public static String uploadEmpDocFile(CommonsMultipartFile file,
			String filePath, String fileNameSeperator) {

		File folder = new File(filePath);
		if (!folder.exists()) {
			folder.mkdirs();
		}
		String fileName = file.getOriginalFilename();
		String ext = fileName.substring(fileName.lastIndexOf('.') + 1);

		if (!("").equals(fileName)) {
			UUID uuid = UUID.randomUUID();
			String fileNameWOExt = fileName.substring(0, fileName.indexOf('.'));
			if (fileNameWOExt.length() > 60) {
				fileNameWOExt = fileNameWOExt.substring(0, 55);
			}
			fileName = fileNameWOExt + fileNameSeperator + uuid + "." + ext;
			File newFile = new File(filePath, fileName);

			try {
				FileOutputStream fos = new FileOutputStream(newFile);
				fos.write(file.getBytes());
				fos.flush();
				fos.close();
			} catch (IOException iOException) {
				LOGGER.error(iOException.getMessage(), iOException);
				throw new PayAsiaSystemException(iOException.getMessage(),
						iOException);
			}

		}
		return fileName;

	}

	public static void deletefile(String file, String path) {

		String filePath = path + file;
		File f1 = new File(filePath);
		f1.delete();
	}

	public static void deletefile(String file) {

		String filePath = file;
		File f1 = new File(filePath);
		f1.delete();
	}

	public static void moveFile(String filePath, String destinationDirPath) {

		File file = new File(filePath);
		File dir = new File(destinationDirPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		file.renameTo(new File(dir, file.getName()));

	}

	public static List<String> validateZipEntriesForbiddenChars(
			byte[] fileData, String pattern) {
		List<String> invaidEntriesList = new ArrayList<String>();

		try (ZipInputStream zin = new ZipInputStream(new ByteArrayInputStream(
				fileData));) {
			ZipEntry zipEntry = null;
			while ((zipEntry = zin.getNextEntry()) != null) {
				String entryName = Paths.get(zipEntry.getName()).getFileName()
						.toString();
				if (entryName.matches(pattern))
					invaidEntriesList.add(entryName);
			}
		} catch (IOException e) {
		}
		return invaidEntriesList;

	}

	public static List<String> uploadZipFile(CommonsMultipartFile fileData,
			String filePath, String fileType, String fileNameSeperator) {

		File folder = new File(filePath);
		if (!folder.exists()) {
			folder.mkdirs();
		}

		List<String> zipFileNames = new ArrayList<String>();
		Enumeration<?> entries;
		ZipFile zipFile;
		String sourceDesc = fileData.getStorageDescription();
		try {
			String fileName = sourceDesc.substring(4).replace("]", "");

			zipFile = new ZipFile(new File(fileName));
			entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) entries.nextElement();
				String entryName = entry.getName();

				if (entry.isDirectory()) {

					continue;
				}
				String ext = entryName
						.substring(entryName.lastIndexOf('.') + 1);
				UUID uuid = UUID.randomUUID();
				entryName = entryName.substring(0, entryName.indexOf('.'))
						+ fileNameSeperator + uuid + "." + ext;

				File file = new File(filePath, entryName.substring(entryName
						.lastIndexOf("/") + 1));
				InputStream inputStream = zipFile.getInputStream(entry);
				zipFileNames
						.add(entryName.substring(entryName.lastIndexOf("/") + 1));
				OutputStream out = new FileOutputStream(file);
				byte[] buf = new byte[1024];
				int len;
				while ((len = inputStream.read(buf)) > 0)
					out.write(buf, 0, len);
				out.close();
				inputStream.close();

			}

			zipFile.close();
		} catch (IOException ioe) {
			LOGGER.error(ioe.getMessage(), ioe);
			throw new PayAsiaSystemException(ioe.getMessage(), ioe);

		}

		return zipFileNames;
	}

	public static List<String> uploadZipFileForTaxDocuments(
			CommonsMultipartFile fileData, String filePath, String fileType,
			List<String> validFileNames) {

		File folder = new File(filePath);
		if (!folder.exists()) {
			folder.mkdirs();
		}
		String entryFileType;

		List<String> zipFileNames = new ArrayList<String>();
		Enumeration<?> entries;
		ZipFile zipFile;
		String sourceDesc = fileData.getStorageDescription();
		try {
			String fileName = sourceDesc.substring(4).replace("]", "");

			zipFile = new ZipFile(new File(fileName));
			entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) entries.nextElement();
				String entryName = entry.getName();
				entryFileType = entryName
						.substring(entryName.lastIndexOf('.') + 1);
				if (entry.isDirectory()) {

					continue;
				}
				if (!"pdf".equalsIgnoreCase(entryFileType)) {

					continue;
				}
				File file = new File(filePath, entryName.substring(entryName
						.lastIndexOf("/") + 1));
				InputStream inputStream = zipFile.getInputStream(entry);
				zipFileNames
						.add(entryName.substring(entryName.lastIndexOf("/") + 1));
				OutputStream out = new FileOutputStream(file);
				byte[] buf = new byte[1024];
				int len;
				while ((len = inputStream.read(buf)) > 0)
					out.write(buf, 0, len);
				out.close();
				inputStream.close();

			}

			zipFile.close();
		} catch (IOException ioe) {
			LOGGER.error(ioe.getMessage(), ioe);
			throw new PayAsiaSystemException(ioe.getMessage(), ioe);

		}

		return zipFileNames;
	}

	public static List<CompanyDocumentFileDTO> getFileNamesFromZip(
			CommonsMultipartFile fileData) {

		List<CompanyDocumentFileDTO> companyDocumentFileDTOs = new ArrayList<CompanyDocumentFileDTO>();
		String entryFileType;

		List<String> zipFileNames = new ArrayList<String>();
		Enumeration<?> entries;
		ZipFile zipFile;
		String sourceDesc = fileData.getStorageDescription();
		try {
			String fileName = sourceDesc.substring(4).replace("]", "");

			zipFile = new ZipFile(new File(fileName));
			entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				CompanyDocumentFileDTO companyDocumentFileDTO = new CompanyDocumentFileDTO();
				ZipEntry entry = (ZipEntry) entries.nextElement();
				String entryName = entry.getName();
				entryFileType = entryName
						.substring(entryName.lastIndexOf('.') + 1);
				if (entry.isDirectory()) {

					continue;
				}
				if (!"pdf".equalsIgnoreCase(entryFileType)) {

					continue;
				}
				companyDocumentFileDTO.setInputStream(zipFile
						.getInputStream(entry));
				companyDocumentFileDTO.setTempFileName(entryName);
				companyDocumentFileDTO.setFileName(entryName
						.substring(entryName.lastIndexOf("/") + 1));
				companyDocumentFileDTO.setZipEntry(entry);
				zipFileNames
						.add(entryName.substring(entryName.lastIndexOf("/") + 1));

				companyDocumentFileDTOs.add(companyDocumentFileDTO);

			}

			zipFile.close();
		} catch (IOException ioe) {
			LOGGER.error(ioe.getMessage(), ioe);
			throw new PayAsiaSystemException(ioe.getMessage(), ioe);

		}

		return companyDocumentFileDTOs;
	}

	public static void uploadFilesTaxAndPaySlip(
			CompanyDocumentFileDTO companyDocumentFileDTO, String filePath) {

		File folder = new File(filePath);
		if (!folder.exists()) {
			folder.mkdirs();
		}

		try {
			File file = new File(filePath, companyDocumentFileDTO.getFileName());
			OutputStream out = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			int len;

			while ((len = companyDocumentFileDTO.getInputStream().read(buf)) > 0)
				out.write(buf, 0, len);
			out.close();
			companyDocumentFileDTO.getInputStream().close();

		} catch (IOException ioe) {
			LOGGER.error(ioe.getMessage(), ioe);
			throw new PayAsiaSystemException(ioe.getMessage(), ioe);

		}

	}

	static public void zipFolder(String srcFolder, String destZipFile)
			throws Exception {
		ZipOutputStream zip = null;
		FileOutputStream fileWriter = null;

		fileWriter = new FileOutputStream(destZipFile);
		zip = new ZipOutputStream(fileWriter);

		addFolderToZip("", srcFolder, zip);
		zip.flush();
		zip.close();
	}

	static private void addFileToZip(String path, String srcFile,
			ZipOutputStream zip) throws Exception {

		File folder = new File(srcFile);
		if (folder.isDirectory()) {
			addFolderToZip(path, srcFile, zip);
		} else {
			byte[] buf = new byte[1024];
			int len;
			FileInputStream in = null;
			try {
				in = new FileInputStream(srcFile);
				zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
				while ((len = in.read(buf)) > 0) {
					zip.write(buf, 0, len);
				}
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			} finally {
				if (in != null) {
					in.close();
				}
			}

		}
	}

	static private void addFolderToZip(String path, String srcFolder,
			ZipOutputStream zip) throws Exception {
		File folder = new File(srcFolder);

		for (String fileName : folder.list()) {
			if (path.equals("")) {
				addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip);
			} else {
				addFileToZip(path + "/" + folder.getName(), srcFolder + "/"
						+ fileName, zip);
			}
		}
	}

	public static String uploadEmployeeDocFile(String filePath,
			String destFileName, String destFilePath, String fileNameSeperator) {

		File folder = new File(destFilePath);
		if (!folder.exists()) {
			folder.mkdirs();
		}
		String ext = destFileName.substring(destFileName.lastIndexOf('.') + 1);

		if (!("").equals(destFileName)) {
			UUID uuid = UUID.randomUUID();
			destFileName = destFileName.substring(0, destFileName.indexOf('.'))
					+ fileNameSeperator + uuid + "." + ext;
			File newFile = new File(destFilePath, destFileName);
			Path path = Paths.get(filePath);

			try {
				FileOutputStream fos = new FileOutputStream(newFile);
				fos.write(Files.readAllBytes(path));
				fos.flush();
				fos.close();
			} catch (IOException iOException) {
				LOGGER.error(iOException.getMessage(), iOException);
				throw new PayAsiaSystemException(iOException.getMessage(),
						iOException);
			}
		}
		return destFileName;
	}

	public static String uploadFile(CommonsMultipartFile file, String filePath,
			String fileNameSeperator, Long attachFileId) {

		File folder = new File(filePath);
		if (!folder.exists()) {
			folder.mkdirs();
		}
		String fileName = file.getOriginalFilename();
		String ext = fileName.substring(fileName.lastIndexOf('.') + 1);

		if (!("").equals(fileName)) {
			fileName = attachFileId + "." + ext;
			File newFile = new File(filePath, fileName);

			try {
				FileOutputStream fos = new FileOutputStream(newFile);
				fos.write(file.getBytes());
				fos.flush();
				fos.close();
			} catch (IOException iOException) {
				LOGGER.error(iOException.getMessage(), iOException);
				throw new PayAsiaSystemException(iOException.getMessage(),
						iOException);
			}
		}
		return fileName;

	}

	public static void uploadFile(byte[] imgBytes, String fileName,
			String filePath, String fileNameSeperator, Long attachFileId) {
		File folder = new File(filePath);
		if (!folder.exists()) {
			folder.mkdirs();
		}
		String ext = fileName.substring(fileName.lastIndexOf('.') + 1);
		fileName = attachFileId + "." + ext;
		File newFile = new File(filePath, fileName);

		try {
			FileOutputStream fos = new FileOutputStream(newFile);
			fos.write(imgBytes);
			fos.flush();
			fos.close();
		} catch (IOException iOException) {
			LOGGER.error(iOException.getMessage(), iOException);
			throw new PayAsiaSystemException(iOException.getMessage(),
					iOException);
		}
	}

	public static String uploadFileWOExt(CommonsMultipartFile file,
			String filePath, String fileNameSeperator, Long attachFileId) {

		File folder = new File(filePath);
		if (!folder.exists()) {
			folder.mkdirs();
		}
		String fileName = file.getOriginalFilename();

		if (!("").equals(fileName)) {
			fileName = attachFileId + "";
			File newFile = new File(filePath, fileName);

			try {
				FileOutputStream fos = new FileOutputStream(newFile);
				fos.write(file.getBytes());
				fos.flush();
				fos.close();
			} catch (IOException iOException) {
				LOGGER.error(iOException.getMessage(), iOException);
				throw new PayAsiaSystemException(iOException.getMessage(),
						iOException);
			}
		}
		return fileName;

	}

	public String getGeneratedFilePath(
			FilePathGeneratorDTO filePathGenerator) {
		String completePath = "";
		String commonPath = "";
		
		if (!PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION
				.equalsIgnoreCase(appDeployLocation)) {
			commonPath = filePathGenerator.getDownloadPath()+ docPathSeperator ;
		} 
		
		commonPath = commonPath 
				+ filePathGenerator.getRootDirectoryName()
				+ docPathSeperator + filePathGenerator.getCompanyId()
				+ docPathSeperator
				+ filePathGenerator.getDocDirectoryName()
				+ docPathSeperator;
		
		switch (filePathGenerator.getDocDirectoryName()) {
		case PayAsiaConstants.CLAIM_ATTACHMENT_DIRECTORY_NAME:
			if (PayAsiaConstants.PAYASIA_DOC_UPLOAD_EVENT
					.equalsIgnoreCase(filePathGenerator.getEventName())) {
				completePath = commonPath;
			} else {
				completePath = commonPath + filePathGenerator.getFileName()
						+ PayAsiaConstants.PAYASIA_DOT_CHARACTER
						+ filePathGenerator.getFileExtension();
			}
			break;
		case PayAsiaConstants.LEAVE_ATTACHMENT_DIRECTORY_NAME:
			if (PayAsiaConstants.PAYASIA_DOC_UPLOAD_EVENT
					.equalsIgnoreCase(filePathGenerator.getEventName())) {
				completePath = commonPath;
			} else {
				completePath = commonPath + filePathGenerator.getFileName()
						+ PayAsiaConstants.PAYASIA_DOT_CHARACTER
						+ filePathGenerator.getFileExtension();
			}
			break;
		case PayAsiaConstants.COMPANY_DOCUMENT_GENERAL:
			if (PayAsiaConstants.PAYASIA_DOC_UPLOAD_EVENT
					.equalsIgnoreCase(filePathGenerator.getEventName())) {
				completePath = commonPath;
			} else {
				completePath = commonPath + filePathGenerator.getFileName();
			}
			break;
		case PayAsiaConstants.COMPANY_DOCUMENT_PAYSLIP:
			if (PayAsiaConstants.PAYASIA_DOC_UPLOAD_EVENT
					.equalsIgnoreCase(filePathGenerator.getEventName())) {
				completePath = commonPath + filePathGenerator.getYear()
						+ docPathSeperator;
			} else if (PayAsiaConstants.PAYASIA_DOC_DOWNLOAD_PAYSLIPPDF
					.equalsIgnoreCase(filePathGenerator.getEventName())) {
				completePath = commonPath + filePathGenerator.getYear()
						+ docPathSeperator
						+ filePathGenerator.getEmployeeNumber()
						+ docPathSeperator;
			} else {
				completePath = commonPath + filePathGenerator.getYear()
						+ docPathSeperator
						+ filePathGenerator.getEmployeeNumber()
						+ docPathSeperator + filePathGenerator.getFileName();
			}
			break;
		case PayAsiaConstants.COMPANY_DOCUMENT_TAX_DOCUMENT:
			if (PayAsiaConstants.PAYASIA_DOC_UPLOAD_EVENT
					.equalsIgnoreCase(filePathGenerator.getEventName())) {
				completePath = commonPath + filePathGenerator.getYear()
						+ docPathSeperator;
			} else {
				completePath = commonPath + filePathGenerator.getYear()
						+ docPathSeperator
						+ filePathGenerator.getEmployeeNumber()
						+ docPathSeperator + filePathGenerator.getFileName();
			}
			break;
		case PayAsiaConstants.COMPANY_LOGO_DIRECTORY_NAME:
			if (PayAsiaConstants.PAYASIA_DOC_UPLOAD_EVENT
					.equalsIgnoreCase(filePathGenerator.getEventName())) {
				completePath = commonPath;
			} else {
				completePath = commonPath 
						+ filePathGenerator.getFileName();
			}
			break;
		case PayAsiaConstants.COMPANY_DOCUMENT_DISCUSSION_BOARD:
			if (PayAsiaConstants.PAYASIA_DOC_UPLOAD_EVENT
					.equalsIgnoreCase(filePathGenerator.getEventName())) {
				completePath = commonPath + filePathGenerator.getTopicId()
						+ docPathSeperator;
			} else {
				completePath = commonPath + filePathGenerator.getTopicId()
						+ docPathSeperator + filePathGenerator.getFileName()
						+ PayAsiaConstants.PAYASIA_DOT_CHARACTER
						+ filePathGenerator.getFileExtension();
			}
			break;
		case PayAsiaConstants.EMPLOYEE_PHOTO_DIRECTORY_NAME:
			if (PayAsiaConstants.PAYASIA_DOC_UPLOAD_EVENT
					.equalsIgnoreCase(filePathGenerator.getEventName())) {
				completePath = commonPath;
			} else if (PayAsiaConstants.PAYASIA_DOC_READALL_EVENT
					.equalsIgnoreCase(filePathGenerator.getEventName())) {
				completePath = commonPath
						+ filePathGenerator.getEmployeeNumber();
			} else {
				completePath = commonPath + filePathGenerator.getEmployeeNumber();
			}
			break;
		case PayAsiaConstants.COMPANY_DOCUMENT_EMPLOYEE_LETTER:
			if (PayAsiaConstants.PAYASIA_DOC_UPLOAD_EVENT
					.equalsIgnoreCase(filePathGenerator.getEventName())) {
				completePath = commonPath 
						+ filePathGenerator.getEmployeeNumber()
						+ docPathSeperator;
			} else {
				completePath = commonPath 
						+ filePathGenerator.getEmployeeNumber()
						+ docPathSeperator + filePathGenerator.getFileName();
			}
			break;
		case PayAsiaConstants.COMPANY_DOCUMENT_EMPLOYEE_INFO_DOCUMENT:
			if (PayAsiaConstants.PAYASIA_DOC_UPLOAD_EVENT
					.equalsIgnoreCase(filePathGenerator.getEventName())) {
				completePath = commonPath 
						+ filePathGenerator.getEmployeeNumber()
						+ docPathSeperator;
			} else {
				completePath = commonPath 
						+ filePathGenerator.getEmployeeNumber()
						+ docPathSeperator + filePathGenerator.getFileName();
			}
			break;
		case PayAsiaConstants.COMPANY_DOCUMENT_EMPLOYEE_DOCUMENT:
			if (PayAsiaConstants.PAYASIA_DOC_UPLOAD_EVENT
					.equalsIgnoreCase(filePathGenerator.getEventName())) {
				completePath = commonPath 
						+ filePathGenerator.getEmployeeNumber()
						+ docPathSeperator;
			} else {
				completePath = commonPath + docPathSeperator
						+ filePathGenerator.getEmployeeNumber()
						+ filePathGenerator.getYear() + docPathSeperator
						+ filePathGenerator.getFileName();
			}
			break;
		case PayAsiaConstants.FTP_DOCUMENT_DIRECTORY_NAME:
			if (PayAsiaConstants.PAYASIA_DOC_UPLOAD_EVENT
					.equalsIgnoreCase(filePathGenerator.getEventName())) {
				completePath = PayAsiaConstants.PAYASIA_APPLICATION_DOC_ROOT_DIRECTORY + docPathSeperator + filePathGenerator.getDocDirectoryName() + docPathSeperator+filePathGenerator.getFileName() + docPathSeperator+filePathGenerator.getCompanyId()+docPathSeperator;
			} else if(PayAsiaConstants.PAYASIA_DOC_VIEW_EVENT.equalsIgnoreCase(filePathGenerator.getEventName())){
				completePath = PayAsiaConstants.PAYASIA_APPLICATION_DOC_ROOT_DIRECTORY + docPathSeperator + filePathGenerator.getDocDirectoryName() + docPathSeperator+filePathGenerator.getFileName()+"."+filePathGenerator.getFileExtension();
			}else {
				/*completePath = PayAsiaConstants.PAYASIA_APPLICATION_DOC_ROOT_DIRECTORY + docPathSeperator + filePathGenerator.getDocDirectoryName() + docPathSeperator+filePathGenerator.getCompanyId()
						+docPathSeperator + filePathGenerator.getFileName()+docPathSeperator + PayAsiaConstants.PAYASIA_DOT_CHARACTER
						+ filePathGenerator.getFileExtension();*/
				completePath = PayAsiaConstants.PAYASIA_APPLICATION_DOC_ROOT_DIRECTORY + docPathSeperator + filePathGenerator.getDocDirectoryName() + docPathSeperator+filePathGenerator.getYear() + 
						docPathSeperator+filePathGenerator.getCompanyId()+docPathSeperator+filePathGenerator.getFileName()+"."+filePathGenerator.getFileExtension();
			}
			break;
		}
		return completePath;
	}

	public FilePathGeneratorDTO getFileCommonPath(String downloadPath,
			String rootDirectoryName, long companyId, String docDirectoryName,
			String fileName, String year, String employeeNumber,
			String fileExtension, String eventName, long topicId) {
		FilePathGeneratorDTO filePathGenerator = new FilePathGeneratorDTO();
		filePathGenerator.setDownloadPath(downloadPath);
		filePathGenerator.setRootDirectoryName(rootDirectoryName);
		filePathGenerator.setCompanyId(companyId);
		filePathGenerator.setDocDirectoryName(docDirectoryName);
		filePathGenerator.setFileName(fileName);
		filePathGenerator.setYear(year);
		filePathGenerator.setEmployeeNumber(employeeNumber);
		filePathGenerator.setFileExtension(fileExtension);
		filePathGenerator.setEventName(eventName);
		filePathGenerator.setTopicId(topicId);
		return filePathGenerator;
	}
	
	public static void createFolder(String filePath) {
     
        try
        {
		File folder = new File(filePath);
		if (!folder.exists()) {
			folder.mkdirs();
		}
        }
        catch(Exception exception)
        {
        	LOGGER.error(exception.getMessage()+exception);
        
        }
	
	}
	 
	public static boolean createSubFolder(String filePath) {
		boolean isSubFolderCreation = true;
		try {
			File folder = new File(filePath);
			if (folder.exists()) {
				deleteFolder(filePath);
				folder.mkdirs();
			}

			else {
				
				folder.mkdirs();
			}
		} catch (Exception exception) {
			LOGGER.error(exception.getMessage() + exception);
			isSubFolderCreation = false;
		}
		return isSubFolderCreation;
	}

	public String getAttachmentMailFilePath(
			FilePathGeneratorDTO filePathGenerator) {
		String completePath = "";
		String commonPath = "";
		
		if (!PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION
				.equalsIgnoreCase(appDeployLocation)) {
			commonPath = filePathGenerator.getDownloadPath()+ docPathSeperator ;
		} 
		
		commonPath = commonPath 
				+ filePathGenerator.getRootDirectoryName()
				+ docPathSeperator + filePathGenerator.getCompanyId()
				+ docPathSeperator
				+ filePathGenerator.getDocDirectoryName()
				+ docPathSeperator;
		if(PayAsiaConstants.CLAIM_ATTACHMENT_DIRECTORY_NAME.equalsIgnoreCase( filePathGenerator.getDocDirectoryName()))
				{
		
			if (PayAsiaConstants.PAYASIA_DOC_READALL_EVENT
					.equalsIgnoreCase(filePathGenerator.getEventName())) {
				completePath = commonPath+filePathGenerator.getFileName()+PayAsiaConstants.PAYASIA_DOT_CHARACTER
						+ filePathGenerator.getFileExtension();;
						
			} else {
				completePath = commonPath + filePathGenerator.getFileName()+PayAsiaConstants.PAYASIA_DOT_CHARACTER
						+ filePathGenerator.getFileExtension();;
			}
				}
		return completePath;
	}

	public static void createAttachmentFile(byte[] imgBytes, String fileName,
			String filePath,String fileExtension)
	{
		File folder = new File(filePath);
		if (!folder.exists()) {
			folder.mkdirs();
		}
		//String ext = fileName.substring(fileName.lastIndexOf('.') + 1);
		//fileName = attachFileId + "." + ext;
		File newFile = new File(filePath, fileName);

		try {
			FileOutputStream fos = new FileOutputStream(newFile);
			fos.write(imgBytes);
			fos.flush();
			fos.close();
		} catch (IOException iOException) {
			LOGGER.error(iOException.getMessage(), iOException);
			throw new PayAsiaSystemException(iOException.getMessage(),
					iOException);
		}
		
	}
	
	public static void deleteFolder(String filePath)
	{
		File index = new File(filePath);
		if (index.exists())
		{
			String[]entries = index.list();
			for(String s: entries){
			    File currentFile = new File(index.getPath(),s);
			    currentFile.delete();
			}
			index.delete();
		}
	}
	
	
	public static ArrayList<File> getFiles(String filePath)
	{
		ArrayList<File> filesToAdd = new ArrayList<File>();
		
		File index = new File(filePath);
		if (index.exists())
		{
			String[]entries = index.list();
			for(String s: entries){
			    File currentFile = new File(index.getPath(),s);
			    
			    filesToAdd.add(currentFile);
			   
			}
			
		}
		
		return filesToAdd;
	}

	public static boolean createPasswordProtectedZip(final String zipFileName, final List<File> filesToZip,
			final String pwd) {

		net.lingala.zip4j.core.ZipFile zipFile;
		try {
			zipFile = new net.lingala.zip4j.core.ZipFile(zipFileName);

			ZipParameters zipParameters = new ZipParameters();
			zipParameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
			zipParameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
			zipParameters.setEncryptFiles(true);
			zipParameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);
			zipParameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
			zipParameters.setPassword(pwd);
			zipFile.addFiles(new ArrayList<>(filesToZip), zipParameters);
			return true;
		} catch (ZipException zipException) {
			zipException.printStackTrace();
			LOGGER.error("Error while creating zip file. "+zipException.getMessage(), zipException);
			return false;
		}

	}


	/**
	 * @author manojkumar2
	 * @Param : byte[] fileByteArray, String fileName, String delimitedFileExts, String delimitedFileMIMETypes, Double fileSize
	 * @Param : This Method is used to check file Content type / MINE type & Size  
	*/
	public static boolean isValidFile(byte[] fileByteArray, String fileName, String delimitedFileExts, String delimitedFileMIMETypes, Double fileSize) {
		
		  final String delimiter = PayAsiaConstants.UPLOAD_FILE_DELIMITER;
		 
		 if(fileName != null && delimitedFileExts != null && delimitedFileMIMETypes != null && fileSize != null && delimiter != null) {
	
			delimitedFileExts = delimitedFileExts.toUpperCase();
			String[] extTypeArray = delimitedFileExts.split(delimiter);
	       	List<String> extTypeList = Arrays.asList(extTypeArray);
	       	
	       	delimitedFileMIMETypes = delimitedFileMIMETypes.toUpperCase();
	       	String[] mimeTypeArray = delimitedFileMIMETypes.split(delimiter);
	       	List<String> mimeTypeList = Arrays.asList(mimeTypeArray);    	
	       	String fileExt = fileName.substring(fileName.lastIndexOf('.')+1,fileName.length()); //Getting file extension   
	       	int fileLength = fileByteArray.length;
			final double fileBytes = (double) fileLength;
	       	String fileMIMEType = null;
			try {
				MagicMatch magicMatch = Magic.getMagicMatch(fileByteArray, true);
				fileMIMEType = magicMatch.getMimeType();
			} catch (MagicParseException | MagicMatchNotFoundException | MagicException e) {
				LOGGER.error("Exception While Parsing File Mime Type");
			} 
			if(fileExt != null && fileMIMEType != null && fileSize != null && extTypeList.contains(fileExt.toUpperCase()) 
					&& (fileMIMEType != null && mimeTypeList.contains(fileMIMEType.toUpperCase())) 
					&& fileBytes <= (fileSize*1024*1024)){
				return true;
			}
			else{
				return false;
			}
		}
		else{
			return false;
		}
	}
	
	
	/**
	 * @author manojkumar2
	 * @Param : CommonsMultipartFile commonsMultipartFile, String fileName, String delimitedFileExts,String delimitedFileMIMETypes, Double fileSize
	 * @Param : This Method is used to check file Content type / MINE type & Size & check ZIP file inside file  
	*/
	public static boolean isValidFile(CommonsMultipartFile commonsMultipartFile, String fileName, String delimitedFileExts, 
			String delimitedFileMIMETypes, Double fileSize) {
		fileName = fileName != null ? fileName : commonsMultipartFile.getName();
		final String delimiter = PayAsiaConstants.UPLOAD_FILE_DELIMITER;
		if(commonsMultipartFile != null && fileName != null  && delimitedFileExts != null && delimitedFileMIMETypes != null 
				&& fileSize != null && delimiter != null) {
			
			delimitedFileExts = delimitedFileExts.toUpperCase();
			String[] extTypeArray = delimitedFileExts.split(delimiter);
	       	List<String> extTypeList = Arrays.asList(extTypeArray);
	       	
	       	delimitedFileMIMETypes = delimitedFileMIMETypes.toUpperCase();
	       	String[] mimeTypeArray = delimitedFileMIMETypes.split(delimiter);
	       	List<String> mimeTypeList = Arrays.asList(mimeTypeArray);
	       	
	       	String fileExt = fileName.substring(fileName.lastIndexOf('.')+1,fileName.length()); //Getting file extension
	       	fileExt = fileExt.toUpperCase();
	        if(!StringUtils.isEmpty(fileExt) && fileExt.contains("ZIP")){
	       	   boolean isZipFileVaild = isValidZipFile(commonsMultipartFile, extTypeList);
	       	  if(!isZipFileVaild){
	       		  return false;
	       	  }
	       	}
	       	
	       	final double fileBytes = commonsMultipartFile.getBytes().length;
	       	String fileMIMEType = null;
			try {
				MagicMatch magicMatch = Magic.getMagicMatch(commonsMultipartFile.getBytes(), true);
				fileMIMEType = magicMatch.getMimeType();
			} catch (MagicParseException | MagicMatchNotFoundException | MagicException e) {
				LOGGER.error("Exception While Parsing File Mime Type");
			} 

			if(fileExt != null && fileMIMEType != null && fileSize != null && extTypeList.contains(fileExt.toUpperCase()) 
					&& (fileMIMEType != null && mimeTypeList.contains(fileMIMEType.toUpperCase())) 
					&& fileBytes <= (fileSize*1024*1024)){
				return true;
			}
			else{
				return false;
			}
		}
		else{
			return false;
		}
 }	
	
	/**
	 * @author manojkumar2
	 * @Param : File file, List<String> extTypeList
	 * @Param : This Method is used to check ZIP file inside file  
	*/
	public static boolean isValidZipFile(CommonsMultipartFile commonsMultipartFile, List<String> extTypeList){
		ZipFile zipFile = null;
		Enumeration<?> entries;
		String sourceDesc = commonsMultipartFile.getStorageDescription();
		String fileName = sourceDesc.substring(4).replace("]", "");
		try {
			zipFile = new ZipFile(new File(fileName));
			entries = zipFile.entries();
			while(entries.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) entries.nextElement();
				String entryName = entry.getName();
				String entryFileType = entryName.substring(entryName.lastIndexOf("/") + 1);
				String fileExt = entryFileType.substring(entryFileType.lastIndexOf('.') + 1, entryFileType.length());
				fileExt = fileExt.toUpperCase();
				if(!fileExt.isEmpty() && !extTypeList.contains(fileExt)) {
					return false;
				}
			}
		} catch (Exception e) {
			LOGGER.error("Exception While check ZipFile Ext !!" + e.getMessage());
			return false;
		} finally {
			try {
				zipFile.close();
			} catch (IOException e) {
				LOGGER.error("Exception While close ZipFile !!" + e.getMessage());
			}
		}
		return true;
	}
	/**
	 * @author manojkumar2
	 * @Param : MultipartFile multipart
	 * @Param : This Method is used to convert MultipartFile to File
	*/
	public static File multipartToFile(CommonsMultipartFile multipart) {
		File convFile = new File(multipart.getOriginalFilename());
		FileOutputStream fileOuputStream = null;
		try {
		    fileOuputStream = new FileOutputStream(convFile.getName()); 
		    fileOuputStream.write(multipart.getBytes());
		} catch (IOException | IllegalStateException e) {
			LOGGER.error("Exception While MultipartFile convert to File !!" + e.getMessage());
		}finally {
		   try {
			fileOuputStream.close();
		} catch (IOException e) {}
		}
		return convFile;
	}
	
	public static String extractFileName(String fileName) {
		int lastIndexOf = fileName.lastIndexOf(".");
		if(!StringUtils.isEmpty(fileName) && lastIndexOf > 36) {
			String extractFileName = fileName.substring(0, lastIndexOf - 36);
			String ext = fileName.substring(fileName.lastIndexOf('.'), fileName.length());
			if (extractFileName.endsWith("_")) {
				extractFileName = fileName.substring(0, extractFileName.length() - 1);
			}
			return extractFileName.concat(ext);
		} else {
			return fileName;
		}
	}
	
	
	public static void main(String[] args) {	
		 File file = new File("C://Users//manojkumar2.MIND//Desktop//q.xlsx");
		 //System.out.println("is" );
		//boolean  xyz = isValidFile(file, file.getName(),PayAsiaConstants.ALLOWED_STANDARD_UPLOAD_FILE_EXT, PayAsiaConstants.ALLOWED_STANDARD_UPLOAD_FILE_MINE_TYPE, PayAsiaConstants.FILE_SIZE);
		//System.out.println("hii" + xyz );		
		
		 Long l1 = 127l;
		 Long l2 = 127l;
		 System.out.println(l1==l2);
	
		 Long l3 = 129l;
		 Long l4 = 129l;
		 System.out.println(l3==l4);
		 
		 String s = new String("abc");
		 s = s.intern();
		 String s1= "abc";
		 System.out.println(s==s1);
		 
	}
	
}



