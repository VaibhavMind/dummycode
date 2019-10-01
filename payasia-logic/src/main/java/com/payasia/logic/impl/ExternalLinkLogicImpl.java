package com.payasia.logic.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Resource;
import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.payasia.common.form.ExternalLinkForm;
import com.payasia.common.util.ImageUtils;
import com.payasia.common.util.PasswordUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.AppConfigMasterDAO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.CompanyExternalLinkDAO;
import com.payasia.dao.bean.AppConfigMaster;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyExternalLink;
import com.payasia.logic.ExternalLinkLogic;

/**
 * The Class ExternalLinkLogicImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Component
public class ExternalLinkLogicImpl implements ExternalLinkLogic {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(ExternalLinkLogicImpl.class);

	/** The company external link dao. */
	@Resource
	CompanyExternalLinkDAO companyExternalLinkDAO;

	/** The company dao. */
	@Resource
	CompanyDAO companyDAO;

	/** The app config master dao. */
	@Resource
	AppConfigMasterDAO appConfigMasterDAO;

	/** The external link image max width. */
	@Value("#{payasiaptProperties['payasia.external.link.image.max.width']}")
	private String EXTERNAL_LINK_IMAGE_MAX_WIDTH;

	/** The external link image max height. */
	@Value("#{payasiaptProperties['payasia.external.link.image.max.height']}")
	private String EXTERNAL_LINK_IMAGE_MAX_HEIGHT;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.ExternalLinkLogic#saveExternalLink(com.payasia.common
	 * .form.ExternalLinkForm, java.lang.Long)
	 */
	@Override
	public String saveExternalLink(ExternalLinkForm externalLinkForm,
			Long companyId) {

		CompanyExternalLink companyExternalLinkVO = companyExternalLinkDAO
				.findByConditionCompany(companyId);

		if (externalLinkForm.getExtImage().isEmpty()) {
			if (companyExternalLinkVO == null) {
				return "please.choose.the.image.before.save";
			}
		}

		CompanyExternalLink companyExternalLink = new CompanyExternalLink();
		Company company = companyDAO.findById(companyId);
		companyExternalLink.setCompany(company);
		companyExternalLink.setMessage(externalLinkForm.getMessage());
		companyExternalLink.setLink(externalLinkForm.getExtUrl());

		if (!externalLinkForm.getExtImage().isEmpty()) {
			companyExternalLink.setImage(externalLinkForm.getExtImage()
					.getBytes());
			companyExternalLink.setImageName(externalLinkForm.getExtImage()
					.getOriginalFilename());
		} else {
			companyExternalLink.setImage(companyExternalLinkVO.getImage());
			companyExternalLink.setImageName(companyExternalLinkVO
					.getImageName());
		}

		if (companyExternalLinkVO == null) {
			companyExternalLinkDAO.save(companyExternalLink);
			return "payasia.external.link.External.Link.saved.successfully";
		} else {
			companyExternalLink.setExternalLinkId(companyExternalLinkVO
					.getExternalLinkId());
			companyExternalLinkDAO.update(companyExternalLink);
			return "payasia.external.link.External.Link.saved.successfully";
		}

	}

	/**
	 * Check ext link.
	 * 
	 * @param companyId
	 *            the company id
	 * @return true, if successful
	 */
	public boolean checkExtLink(Long companyId) {
		CompanyExternalLink companyExternalLinkVO = companyExternalLinkDAO
				.findByConditionCompany(companyId);
		if (companyExternalLinkVO == null) {
			return false;
		}
		return true;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.ExternalLinkLogic#getExternalLink(java.lang.Long)
	 */
	@Override
	public ExternalLinkForm getExternalLink(Long companyId) {
		ExternalLinkForm externalLinkForm = new ExternalLinkForm();
		CompanyExternalLink companyExternalLinkVO = companyExternalLinkDAO
				.findByConditionCompany(companyId);
		if (companyExternalLinkVO != null) {

			externalLinkForm.setMessage(companyExternalLinkVO.getMessage());
			externalLinkForm.setExtUrl(companyExternalLinkVO.getLink());
			externalLinkForm.setImage(companyExternalLinkVO.getImage());
		}

		return externalLinkForm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.ExternalLinkLogic#getExternalImage(long,
	 * java.lang.String)
	 */
	@Override
	public byte[] getExternalImage(long companyId, String defaultImagePath) {
		CompanyExternalLink companyExternalLinkVO = companyExternalLinkDAO
				.findByConditionCompany(companyId);
		byte[] originalByteFile = null;
		byte[] resizedImageInByte = null;
		if (companyExternalLinkVO != null) {
			originalByteFile = companyExternalLinkVO.getImage();
			int imageNameIndex = companyExternalLinkVO.getImageName()
					.lastIndexOf(".");
			String imageType = companyExternalLinkVO.getImageName().substring(
					imageNameIndex + 1);

			InputStream inputStream = new ByteArrayInputStream(originalByteFile);
			try {
				BufferedImage originalBufferedImage = ImageIO.read(inputStream);
				int maxWidth = Integer.parseInt(EXTERNAL_LINK_IMAGE_MAX_WIDTH);
				int maxHeight = Integer
						.parseInt(EXTERNAL_LINK_IMAGE_MAX_HEIGHT);

				 

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

				int maxWidth = Integer.parseInt(EXTERNAL_LINK_IMAGE_MAX_WIDTH);
				int maxHeight = Integer
						.parseInt(EXTERNAL_LINK_IMAGE_MAX_HEIGHT);

				 

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.ExternalLinkLogic#getExtLinkImageSize()
	 */
	@Override
	public int getExtLinkImageSize() {
		AppConfigMaster appConfigMaster = appConfigMasterDAO
				.findByName(PayAsiaConstants.EXTERNAL_LINK_IMAGE_SIZE);

		if (appConfigMaster != null) {
			int maxImageFileSize = Integer.parseInt(appConfigMaster
					.getParamValue()) * 1024;
			return maxImageFileSize;
		}
		return 0;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.ExternalLinkLogic#getHomePageImage(long,
	 * java.lang.String, int, int)
	 */
	@Override
	public byte[] getHomePageImage(long companyId, String defaultImagePath,
			int imageWidth, int imageHeight) {
		CompanyExternalLink companyExternalLinkVO = companyExternalLinkDAO
				.findByConditionCompany(companyId);
		byte[] originalByteFile = null;
		byte[] resizedImageInByte = null;
		if (companyExternalLinkVO != null) {
			originalByteFile = companyExternalLinkVO.getImage();
			int imageNameIndex = companyExternalLinkVO.getImageName()
					.lastIndexOf(".");
			String imageType = companyExternalLinkVO.getImageName().substring(
					imageNameIndex + 1);

			InputStream inputStream = new ByteArrayInputStream(originalByteFile);
			try {
				BufferedImage originalBufferedImage = ImageIO.read(inputStream);

				 

				BufferedImage convertedBufferdImage = ImageUtils.resize(
						originalBufferedImage, imageWidth, imageHeight, true);

				ByteArrayOutputStream baos = new ByteArrayOutputStream();

				ImageIO.write(convertedBufferdImage, imageType, baos);
				baos.flush();
				resizedImageInByte = baos.toByteArray();
				baos.close();
			} catch (IOException ex) {
				LOGGER.error(ex.getMessage(), ex);
			}
		}
		return resizedImageInByte;
	}
}
