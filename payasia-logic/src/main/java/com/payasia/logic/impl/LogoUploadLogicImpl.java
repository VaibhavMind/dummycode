package com.payasia.logic.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.imageio.ImageIO;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.payasia.common.dto.FilePathGeneratorDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.LogoUploadForm;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.FileUtils;
import com.payasia.common.util.ImageUtils;
import com.payasia.common.util.PasswordUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.AppConfigMasterDAO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.CompanyLogoDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EmployeeRoleMappingDAO;
import com.payasia.dao.bean.AppConfigMaster;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyLogo;
import com.payasia.dao.bean.EmployeeRoleMapping;
import com.payasia.logic.AWSS3Logic;
import com.payasia.logic.LogoUploadLogic;

/**
 * The Class LogoUploadLogicImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Component
public class LogoUploadLogicImpl implements LogoUploadLogic {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(LogoUploadLogicImpl.class);

	/** The company dao. */
	@Resource
	CompanyDAO companyDAO;

	/** The employee dao. */
	@Resource
	EmployeeDAO employeeDAO;

	/** The company logo dao. */
	@Resource
	CompanyLogoDAO companyLogoDAO;

	/** The employee role mapping dao. */
	@Resource
	EmployeeRoleMappingDAO employeeRoleMappingDAO;

	/** The app config master dao. */
	@Resource
	AppConfigMasterDAO appConfigMasterDAO;

	/** The company logo max width. */
	@Value("#{payasiaptProperties['payasia.company.logo.max.width']}")
	private String COMPANY_LOGO_MAX_WIDTH;

	/** The company logo max height. */
	@Value("#{payasiaptProperties['payasia.company.logo.max.height']}")
	private String COMPANY_LOGO_MAX_HEIGHT;

	/** The company logo max width. */
	@Value("#{payasiaptProperties['payasia.company.mobile.logo.max.width']}")
	private String COMPANY_MOBILE_LOGO_MAX_WIDTH;

	/** The company logo max height. */
	@Value("#{payasiaptProperties['payasia.company.mobile.logo.max.height']}")
	private String COMPANY_MOBILE_LOGO_MAX_HEIGHT;

	@Value("#{payasiaptProperties['payasia.home']}")
	private String downloadPath;

	/** The documents Root Directory Name. */
	@Value("#{payasiaptProperties['payasia.document.root.directory']}")
	private String rootDirectoryName;

	@Value("#{payasiaptProperties['payasia.tax.document.document.name.separator']}")
	private String fileNameSeperator;

	@Resource
	FileUtils fileUtils;

	@Resource
	AWSS3Logic awss3LogicImpl;

	@Value("#{payasiaptProperties['payasia.application.deployment.location']}")
	private String appDeployLocation;

	@Value("#{payasiaptProperties['payasia.document.path.separator']}")
	private String docPathSeperator;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.LogoUploadLogic#logoUpload(com.payasia.common.form.
	 * LogoUploadForm, java.lang.Long)
	 */
	@Override
	public void logoUpload(LogoUploadForm logoUploadForm, Long companyId) {
		CompanyLogo companyLogo = new CompanyLogo();

		Company company = companyDAO.findById(companyId);
		companyLogo.setCompany(company);

		String imageName = logoUploadForm.getLogoImage().getOriginalFilename();
		companyLogo.setImageName(imageName);
		companyLogo.setUploadedDate(DateUtils.getCurrentTimestamp());

		CompanyLogo companyLogoData = companyLogoDAO
				.findByConditionCompany(companyId);

		if (companyLogoData == null) {
			CompanyLogo saveReturn = companyLogoDAO.saveReturn(companyLogo);

			// save Employee Photo to file directory
			/*
			 * String filePath = downloadPath + "/company/" + companyId + "/" +
			 * PayAsiaConstants.COMPANY_LOGO_DIRECTORY_NAME + "/";
			 */

			FilePathGeneratorDTO filePathGenerator = fileUtils
					.getFileCommonPath(downloadPath, rootDirectoryName,
							companyId,
							PayAsiaConstants.COMPANY_LOGO_DIRECTORY_NAME, null,
							null, null, null,
							PayAsiaConstants.PAYASIA_DOC_UPLOAD_EVENT, 0);
			String filePath = fileUtils.getGeneratedFilePath(filePathGenerator);

			if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION
					.equalsIgnoreCase(appDeployLocation)) {
				awss3LogicImpl.uploadCommonMultipartFile(
						logoUploadForm.getLogoImage(),
						filePath + saveReturn.getCompanyLogoId());
			} else {
				FileUtils.uploadFileWOExt(logoUploadForm.getLogoImage(),
						filePath, fileNameSeperator,
						saveReturn.getCompanyLogoId());
			}

		} else {

			boolean success = true;
			try {
				/*
				 * String filePath = "/company/" + companyId + "/" +
				 * PayAsiaConstants.COMPANY_LOGO_DIRECTORY_NAME + "/" +
				 * companyLogoData.getCompanyLogoId();
				 */

				FilePathGeneratorDTO filePathGenerator = fileUtils
						.getFileCommonPath(downloadPath, rootDirectoryName,
								companyId,
								PayAsiaConstants.COMPANY_LOGO_DIRECTORY_NAME,
								String.valueOf(companyLogoData
										.getCompanyLogoId()), null, null, null,
								PayAsiaConstants.PAYASIA_DOC_DELETE_EVENT, 0);
				String filePath = fileUtils
						.getGeneratedFilePath(filePathGenerator);
				if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION
						.equalsIgnoreCase(appDeployLocation)) {
					List<String> fileList = new ArrayList<String>();
					fileList.add(filePath);
					awss3LogicImpl.deleteMultiObjectNonVersioned(fileList);
				} else {
					FileUtils.deletefile(filePath);
				}
			} catch (Exception exception) {
				success = false;
				LOGGER.error(exception.getMessage(), exception);
				throw new PayAsiaSystemException(exception.getMessage(),
						exception);
			}
			if (success) {
				// save Employee Photo to file directory
				/*
				 * String filePath = downloadPath + "/company/" + companyId +
				 * "/" + PayAsiaConstants.COMPANY_LOGO_DIRECTORY_NAME + "/";
				 */

				FilePathGeneratorDTO filePathGenerator = fileUtils
						.getFileCommonPath(downloadPath, rootDirectoryName,
								companyId,
								PayAsiaConstants.COMPANY_LOGO_DIRECTORY_NAME,
								null, null, null, null,
								PayAsiaConstants.PAYASIA_DOC_UPLOAD_EVENT, 0);
				String filePath = fileUtils
						.getGeneratedFilePath(filePathGenerator);

				if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION
						.equalsIgnoreCase(appDeployLocation)) {
					awss3LogicImpl.uploadCommonMultipartFile(
							logoUploadForm.getLogoImage(), filePath
									+ companyLogoData.getCompanyLogoId());
				} else {
					FileUtils.uploadFileWOExt(logoUploadForm.getLogoImage(),
							filePath, fileNameSeperator,
							companyLogoData.getCompanyLogoId());
				}

				companyLogoData.setImageName(imageName);
				companyLogoData
						.setUploadedDate(DateUtils.getCurrentTimestamp());
				companyLogoDAO.update(companyLogoData);
			}

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.LogoUploadLogic#getCompanyList()
	 */
	@Override
	public List<LogoUploadForm> getCompanyList(Long employeeId) {
		List<LogoUploadForm> logoCompanyList = new ArrayList<LogoUploadForm>();

		List<EmployeeRoleMapping> employeeRoleMappingVOList = employeeRoleMappingDAO
				.getSwitchCompanyList(employeeId, null, null);
		Set<Company> companySet = new LinkedHashSet<Company>();
		for (EmployeeRoleMapping employeeRoleMappingVO : employeeRoleMappingVOList) {
			companySet.add(employeeRoleMappingVO.getCompany());
		}
		for (Company company : companySet) {
			LogoUploadForm logoUploadForm = new LogoUploadForm();
			logoUploadForm.setCompanyId(company.getCompanyId());
			try {
				logoUploadForm.setCompanyName(URLEncoder.encode(
						company.getCompanyName(), "UTF-8"));
			} catch (UnsupportedEncodingException ex) {
				LOGGER.error(ex.getMessage(), ex);
			}
			logoCompanyList.add(logoUploadForm);
		}
		BeanComparator logoUploadFormComp = new BeanComparator("companyName");
		Collections.sort(logoCompanyList, logoUploadFormComp);
		return logoCompanyList;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.LogoUploadLogic#getLogo(java.lang.Long,
	 * java.lang.String)
	 */
	@Override
	public byte[] getLogo(Long companyId, String defaultImagePath) {

		CompanyLogo companyLogoData = companyLogoDAO
				.findByConditionCompany(companyId);
		byte[] byteFile = null;

		if (companyLogoData != null) {
			/*
			 * String filePath = "company/" +
			 * companyLogoData.getCompany().getCompanyId() + "/" +
			 * PayAsiaConstants.COMPANY_LOGO_DIRECTORY_NAME + "/" +
			 * companyLogoData.getCompanyLogoId();
			 */

			FilePathGeneratorDTO filePathGenerator = fileUtils
					.getFileCommonPath(downloadPath, rootDirectoryName,
							companyLogoData.getCompany().getCompanyId(),
							PayAsiaConstants.COMPANY_LOGO_DIRECTORY_NAME,
							String.valueOf(companyLogoData.getCompanyLogoId()),
							null, null, null,
							PayAsiaConstants.PAYASIA_DOC_READALL_EVENT, 0);
			String filePath = fileUtils.getGeneratedFilePath(filePathGenerator);

			File file = new File(filePath);
			try {
				if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION
						.equalsIgnoreCase(appDeployLocation)) {
					byteFile = org.apache.commons.io.IOUtils
							.toByteArray(awss3LogicImpl
									.readS3ObjectAsStream(filePath));
				} else {
					byteFile = Files.readAllBytes(file.toPath());
				}
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			}

		} else {
			String defaultImgPath = defaultImagePath;

			File defaultImageFile = new File(defaultImgPath);

			try {
				byteFile = PasswordUtils.getBytesFromFile(defaultImageFile);
			} catch (IOException ex) {
				LOGGER.error(ex.getMessage(), ex);
			}
		}
		return byteFile;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.LogoUploadLogic#getHeaderLogo(long,
	 * java.lang.String)
	 */
	@Override
	public byte[] getHeaderLogo(long companyId, String defaultImagePath) {
		CompanyLogo companyLogoData = companyLogoDAO
				.findByConditionCompany(companyId);
		byte[] originalByteFile = null;
		byte[] resizedImageInByte = null;
		if (companyLogoData != null) {
			/*
			 * String filePath = "company/" +
			 * companyLogoData.getCompany().getCompanyId() + "/" +
			 * PayAsiaConstants.COMPANY_LOGO_DIRECTORY_NAME + "/" +
			 * companyLogoData.getCompanyLogoId();
			 */

			FilePathGeneratorDTO filePathGenerator = fileUtils
					.getFileCommonPath(downloadPath, rootDirectoryName,
							companyLogoData.getCompany().getCompanyId(),
							PayAsiaConstants.COMPANY_LOGO_DIRECTORY_NAME,
							String.valueOf(companyLogoData.getCompanyLogoId()),
							null, null, null,
							PayAsiaConstants.PAYASIA_DOC_READALL_EVENT, 0);
			String filePath = fileUtils.getGeneratedFilePath(filePathGenerator);

			File file = new File(filePath);
			try {
				if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION
						.equalsIgnoreCase(appDeployLocation)) {
					originalByteFile = org.apache.commons.io.IOUtils
							.toByteArray(awss3LogicImpl
									.readS3ObjectAsStream(filePath));
				} else {
					originalByteFile = Files.readAllBytes(file.toPath());
				}
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			}

			int imageNameIndex = companyLogoData.getImageName()
					.lastIndexOf(".");
			String imageType = companyLogoData.getImageName().substring(
					imageNameIndex + 1);

			InputStream inputStream = new ByteArrayInputStream(originalByteFile);
			try {
				BufferedImage originalBufferedImage = ImageIO.read(inputStream);
				int maxWidth = Integer.parseInt(COMPANY_LOGO_MAX_WIDTH);
				int maxHeight = Integer.parseInt(COMPANY_LOGO_MAX_HEIGHT);

				BufferedImage convertedBufferdImage = ImageUtils.resize(
						originalBufferedImage, maxWidth, maxHeight, true);

				ByteArrayOutputStream baos = new ByteArrayOutputStream();

				ImageIO.write(convertedBufferdImage, imageType, baos);
				baos.flush();
				resizedImageInByte = baos.toByteArray();
				baos.close();
			} catch (IOException ex) {
				LOGGER.error(ex.getMessage(), ex);
			}
		} else {
			String defaultImgPath = defaultImagePath;

			File defaultImageFile = new File(defaultImgPath);
			try {
				originalByteFile = PasswordUtils
						.getBytesFromFile(defaultImageFile);

				InputStream inputStream = new ByteArrayInputStream(
						originalByteFile);

				BufferedImage originalBufferedImage = ImageIO.read(inputStream);

				int maxWidth = Integer.parseInt(COMPANY_LOGO_MAX_WIDTH);
				int maxHeight = Integer.parseInt(COMPANY_LOGO_MAX_HEIGHT);

				BufferedImage convertedBufferdImage = ImageUtils.resize(
						originalBufferedImage, maxWidth, maxHeight, true);

				ByteArrayOutputStream baos = new ByteArrayOutputStream();

				ImageIO.write(convertedBufferdImage, "png", baos);
				baos.flush();
				resizedImageInByte = baos.toByteArray();
				baos.close();

			} catch (IOException ex) {
				LOGGER.error(ex.getMessage(), ex);
			}
		}
		return resizedImageInByte;
	}

	@Override
	public byte[] getMobileHeaderCompanyLogo(long companyId,
			String defaultImagePath) {
		CompanyLogo companyLogoData = companyLogoDAO
				.findByConditionCompany(companyId);
		byte[] originalByteFile = null;
		byte[] resizedImageInByte = null;
		if (companyLogoData != null) {
			/*
			 * String filePath = "company/" +
			 * companyLogoData.getCompany().getCompanyId() + "/" +
			 * PayAsiaConstants.COMPANY_LOGO_DIRECTORY_NAME + "/" +
			 * companyLogoData.getCompanyLogoId();
			 */

			FilePathGeneratorDTO filePathGenerator = fileUtils
					.getFileCommonPath(downloadPath, rootDirectoryName,
							companyLogoData.getCompany().getCompanyId(),
							PayAsiaConstants.COMPANY_LOGO_DIRECTORY_NAME,
							String.valueOf(companyLogoData.getCompanyLogoId()),
							null, null, null,
							PayAsiaConstants.PAYASIA_DOC_READALL_EVENT, 0);
			String filePath = fileUtils.getGeneratedFilePath(filePathGenerator);

			File file = new File(filePath);
			try {
				if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION
						.equalsIgnoreCase(appDeployLocation)) {
					originalByteFile = org.apache.commons.io.IOUtils
							.toByteArray(awss3LogicImpl
									.readS3ObjectAsStream(filePath));
				} else {
					originalByteFile = Files.readAllBytes(file.toPath());
				}
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			}
			int imageNameIndex = companyLogoData.getImageName()
					.lastIndexOf(".");
			String imageType = companyLogoData.getImageName().substring(
					imageNameIndex + 1);

			InputStream inputStream = new ByteArrayInputStream(originalByteFile);
			try {
				BufferedImage originalBufferedImage = ImageIO.read(inputStream);
				int maxWidth = Integer.parseInt(COMPANY_MOBILE_LOGO_MAX_WIDTH);
				int maxHeight = Integer
						.parseInt(COMPANY_MOBILE_LOGO_MAX_HEIGHT);

				BufferedImage convertedBufferdImage = ImageUtils.resize(
						originalBufferedImage, maxWidth, maxHeight, true);

				ByteArrayOutputStream baos = new ByteArrayOutputStream();

				ImageIO.write(convertedBufferdImage, imageType, baos);
				baos.flush();
				resizedImageInByte = baos.toByteArray();
				baos.close();
			} catch (IOException ex) {
				LOGGER.error(ex.getMessage(), ex);
			}
		} else {
			String defaultImgPath = defaultImagePath;

			File defaultImageFile = new File(defaultImgPath);
			try {
				originalByteFile = PasswordUtils
						.getBytesFromFile(defaultImageFile);

				InputStream inputStream = new ByteArrayInputStream(
						originalByteFile);

				BufferedImage originalBufferedImage = ImageIO.read(inputStream);

				int maxWidth = Integer.parseInt(COMPANY_MOBILE_LOGO_MAX_WIDTH);
				int maxHeight = Integer
						.parseInt(COMPANY_MOBILE_LOGO_MAX_HEIGHT);

				BufferedImage convertedBufferdImage = ImageUtils.resize(
						originalBufferedImage, maxWidth, maxHeight, true);

				ByteArrayOutputStream baos = new ByteArrayOutputStream();

				ImageIO.write(convertedBufferdImage, "png", baos);
				baos.flush();
				resizedImageInByte = baos.toByteArray();
				baos.close();

			} catch (IOException ex) {
				LOGGER.error(ex.getMessage(), ex);
			}
		}
		return resizedImageInByte;
	}

	@Override
	public String getMobileLogoWidthHeight(long companyId,
			String defaultImagePath) {
		CompanyLogo companyLogoData = companyLogoDAO
				.findByConditionCompany(companyId);

		String logoHeight = null;
		String logoWidth = null;
		byte[] originalByteFile = null;
		if (companyLogoData != null) {
			/*
			 * String filePath = "company/" +
			 * companyLogoData.getCompany().getCompanyId() + "/" +
			 * PayAsiaConstants.COMPANY_LOGO_DIRECTORY_NAME + "/" +
			 * companyLogoData.getCompanyLogoId();
			 */

			FilePathGeneratorDTO filePathGenerator = fileUtils
					.getFileCommonPath(downloadPath, rootDirectoryName,
							companyLogoData.getCompany().getCompanyId(),
							PayAsiaConstants.COMPANY_LOGO_DIRECTORY_NAME,
							String.valueOf(companyLogoData.getCompanyLogoId()),
							null, null, null,
							PayAsiaConstants.PAYASIA_DOC_READALL_EVENT, 0);
			String filePath = fileUtils.getGeneratedFilePath(filePathGenerator);

			File file = new File(filePath);
			try {
				if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION
						.equalsIgnoreCase(appDeployLocation)) {
					originalByteFile = org.apache.commons.io.IOUtils
							.toByteArray(awss3LogicImpl
									.readS3ObjectAsStream(filePath));
				} else {
					originalByteFile = Files.readAllBytes(file.toPath());
				}
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			}
			int imageNameIndex = companyLogoData.getImageName()
					.lastIndexOf(".");
			String imageType = companyLogoData.getImageName().substring(
					imageNameIndex + 1);

			InputStream inputStream = new ByteArrayInputStream(originalByteFile);
			try {
				BufferedImage originalBufferedImage = ImageIO.read(inputStream);
				int maxWidth = Integer.parseInt(COMPANY_MOBILE_LOGO_MAX_WIDTH);
				int maxHeight = Integer
						.parseInt(COMPANY_MOBILE_LOGO_MAX_HEIGHT);

				BufferedImage convertedBufferdImage = ImageUtils.resize(
						originalBufferedImage, maxWidth, maxHeight, true);

				logoHeight = String.valueOf(convertedBufferdImage.getHeight());
				logoWidth = String.valueOf(convertedBufferdImage.getWidth());
				ByteArrayOutputStream baos = new ByteArrayOutputStream();

				ImageIO.write(convertedBufferdImage, imageType, baos);
				baos.flush();
				baos.close();
			} catch (IOException ex) {
				LOGGER.error(ex.getMessage(), ex);
			}
		} else {
			String defaultImgPath = defaultImagePath;

			File defaultImageFile = new File(defaultImgPath);
			try {
				originalByteFile = PasswordUtils
						.getBytesFromFile(defaultImageFile);

				InputStream inputStream = new ByteArrayInputStream(
						originalByteFile);

				BufferedImage originalBufferedImage = ImageIO.read(inputStream);

				int maxWidth = Integer.parseInt(COMPANY_MOBILE_LOGO_MAX_WIDTH);
				int maxHeight = Integer
						.parseInt(COMPANY_MOBILE_LOGO_MAX_HEIGHT);

				BufferedImage convertedBufferdImage = ImageUtils.resize(
						originalBufferedImage, maxWidth, maxHeight, true);
				logoHeight = String.valueOf(convertedBufferdImage.getHeight());
				logoWidth = String.valueOf(convertedBufferdImage.getWidth());
				ByteArrayOutputStream baos = new ByteArrayOutputStream();

				ImageIO.write(convertedBufferdImage, "png", baos);
				baos.flush();
				baos.close();

			} catch (IOException ex) {
				LOGGER.error(ex.getMessage(), ex);
			}
		}
		return logoHeight + "/" + logoWidth;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.LogoUploadLogic#getCompanyLogoSize()
	 */
	@Override
	public int getCompanyLogoSize() {
		AppConfigMaster appConfigMaster = appConfigMasterDAO
				.findByName(PayAsiaConstants.COMPANY_lOGO_SIZE);

		int maxImageFileSize = Integer
				.parseInt(appConfigMaster.getParamValue()) * 1024;
		return maxImageFileSize;

	}

	@Override
	public String getHeaderLogoWidthHeight(long companyId,
			String defaultImagePath) {
		CompanyLogo companyLogoData = companyLogoDAO
				.findByConditionCompany(companyId);

		String logoHeight = null;
		String logoWidth = null;
		byte[] originalByteFile = null;
		if (companyLogoData != null) {
			/*
			 * String filePath = "company/" +
			 * companyLogoData.getCompany().getCompanyId() + "/" +
			 * PayAsiaConstants.COMPANY_LOGO_DIRECTORY_NAME + "/" +
			 * companyLogoData.getCompanyLogoId();
			 */

			FilePathGeneratorDTO filePathGenerator = fileUtils
					.getFileCommonPath(downloadPath, rootDirectoryName,
							companyLogoData.getCompany().getCompanyId(),
							PayAsiaConstants.COMPANY_LOGO_DIRECTORY_NAME,
							String.valueOf(companyLogoData.getCompanyLogoId()),
							null, null, null,
							PayAsiaConstants.PAYASIA_DOC_READALL_EVENT, 0);
			String filePath = fileUtils.getGeneratedFilePath(filePathGenerator);

			File file = new File(filePath);
			try {
				if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION
						.equalsIgnoreCase(appDeployLocation)) {
					originalByteFile = org.apache.commons.io.IOUtils
							.toByteArray(awss3LogicImpl
									.readS3ObjectAsStream(filePath));
				} else {
					originalByteFile = Files.readAllBytes(file.toPath());
				}
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			}

			int imageNameIndex = companyLogoData.getImageName()
					.lastIndexOf(".");
			String imageType = companyLogoData.getImageName().substring(
					imageNameIndex + 1);

			InputStream inputStream = new ByteArrayInputStream(originalByteFile);
			try {
				BufferedImage originalBufferedImage = ImageIO.read(inputStream);
				int maxWidth = Integer.parseInt(COMPANY_LOGO_MAX_WIDTH);
				int maxHeight = Integer.parseInt(COMPANY_LOGO_MAX_HEIGHT);

				BufferedImage convertedBufferdImage = ImageUtils.resize(
						originalBufferedImage, maxWidth, maxHeight, true);

				logoHeight = String.valueOf(convertedBufferdImage.getHeight());
				logoWidth = String.valueOf(convertedBufferdImage.getWidth());
				ByteArrayOutputStream baos = new ByteArrayOutputStream();

				ImageIO.write(convertedBufferdImage, imageType, baos);
				baos.flush();
				baos.close();
			} catch (IOException ex) {
				LOGGER.error(ex.getMessage(), ex);
			}
		} else {
			String defaultImgPath = defaultImagePath;

			File defaultImageFile = new File(defaultImgPath);
			try {
				originalByteFile = PasswordUtils
						.getBytesFromFile(defaultImageFile);

				InputStream inputStream = new ByteArrayInputStream(
						originalByteFile);

				BufferedImage originalBufferedImage = ImageIO.read(inputStream);

				int maxWidth = Integer.parseInt(COMPANY_LOGO_MAX_WIDTH);
				int maxHeight = Integer.parseInt(COMPANY_LOGO_MAX_HEIGHT);

				BufferedImage convertedBufferdImage = ImageUtils.resize(
						originalBufferedImage, maxWidth, maxHeight, true);
				logoHeight = String.valueOf(convertedBufferdImage.getHeight());
				logoWidth = String.valueOf(convertedBufferdImage.getWidth());
				ByteArrayOutputStream baos = new ByteArrayOutputStream();

				ImageIO.write(convertedBufferdImage, "png", baos);
				baos.flush();
				baos.close();

			} catch (IOException ex) {
				LOGGER.error(ex.getMessage(), ex);
			}
		}
		return logoHeight + "/" + logoWidth;
	}
}