package com.payasia.web.controller.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.form.ExternalLinkForm;
import com.payasia.common.util.FileUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.logic.ExternalLinkLogic;
import com.payasia.web.controller.ExternalLinkController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * The Class ExternalLinkControllerImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Controller
@RequestMapping(value = { "admin/externalLink", "employee/externalLink" })
public class ExternalLinkControllerImpl implements ExternalLinkController {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(ExternalLinkControllerImpl.class);

	/** The external link logic. */
	@Resource
	ExternalLinkLogic externalLinkLogic;

	@Autowired
	private ServletContext servletContext;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.ExternalLinkController#saveExternalLink(com
	 * .payasia.common.form.ExternalLinkForm,
	 * javax.servlet.http.HttpServletRequest)
	 */
	@Override
	@RequestMapping(value = "/saveExternalLink.html", method = RequestMethod.POST)
	@ResponseBody public String saveExternalLink(
			@ModelAttribute(value = "externalLinkForm") ExternalLinkForm externalLinkForm,
			HttpServletRequest request) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		
		boolean isValidImage = FileUtils.isValidFile(externalLinkForm.getExtImage(), externalLinkForm.getExtImage().getOriginalFilename(),
				PayAsiaConstants.ALLOWED_UPLOAD_IMAGE_EXT, PayAsiaConstants.ALLOWED_UPLOAD_IMAGE_MINE_TYPE, PayAsiaConstants.IMAGE_SIZE);

		String extLinkStatus = "";
		if (isValidImage) {
			extLinkStatus = externalLinkLogic.saveExternalLink(
					externalLinkForm, companyId);
		}

		return extLinkStatus;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.ExternalLinkController#getExternalLink(javax
	 * .servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/externalLinkData.html", method = RequestMethod.POST)
	@ResponseBody public String getExternalLink(HttpServletRequest request,
			HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		ExternalLinkForm externalLinkForm = externalLinkLogic
				.getExternalLink(companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(externalLinkForm,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.ExternalLinkController#getExternalLinkImage
	 * (javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/externalLinkImage", method = RequestMethod.GET)
	public @ResponseBody byte[] getExternalLinkImage(
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		String defaultImagePath = servletContext.getRealPath(
				"/resources/images/")
				+ "/noImageAvailable.JPG";
		byte[] byteFile = externalLinkLogic.getExternalImage(companyId,
				defaultImagePath);
		response.reset();
		String mimeType = URLConnection.guessContentTypeFromName(byteFile
				.getClass().getName());
		response.setContentType(mimeType);
		response.setContentLength(byteFile.length);
		String imageName = byteFile.getClass().getName();

		response.setHeader("Content-Disposition", "inline;filename="
				+ imageName);

		return byteFile;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.ExternalLinkController#getImage(java.lang.
	 * Integer, java.lang.Integer, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/homePageImage", method = RequestMethod.GET)
	public @ResponseBody void getImage(
			@RequestParam(value = "imageWidth", required = false) Integer imageWidth,
			@RequestParam(value = "imageHeight", required = false) Integer imageHeight,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		ServletOutputStream outStream = null;
		try {
			String defaultImagePath = servletContext.getRealPath(
					"/resources/images/")
					+ "/company_logo.png";
			byte[] byteFile = externalLinkLogic.getHomePageImage(companyId,
					defaultImagePath, imageWidth, imageHeight);
			response.reset();

			if (byteFile != null) {

				String mimeType = URLConnection
						.guessContentTypeFromName(byteFile.getClass().getName());
				response.setContentType(mimeType);
				response.setContentLength(byteFile.length);
				String imageName = byteFile.getClass().getName();
				response.setHeader("Content-Disposition", "inline;filename="
						+ imageName);

				outStream = response.getOutputStream();
				outStream.write(byteFile);
			} else {
				response.setContentType("image/png");
				response.setHeader("Content-Disposition", "inline;filename="
						+ "");
			}
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			try {

				if (outStream != null) {
					outStream.flush();
					outStream.close();
				}

			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.ExternalLinkController#getExtLinkImageSize
	 * (javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/extLinkImageSize.html", method = RequestMethod.POST)
	@ResponseBody public String getExtLinkImageSize(HttpServletRequest request,
			HttpServletResponse response) {
		Integer imageSize = externalLinkLogic.getExtLinkImageSize();
		String maxImageFileSize = "";
		if (imageSize != 0) {
			maxImageFileSize = String.valueOf(imageSize);
		}

		return maxImageFileSize;

	}
}
