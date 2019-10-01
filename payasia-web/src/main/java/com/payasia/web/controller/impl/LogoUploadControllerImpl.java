package com.payasia.web.controller.impl;

import java.io.IOException;
import java.net.URLConnection;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.form.LogoUploadForm;
import com.payasia.common.util.FileUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.logic.LogoUploadLogic;
import com.payasia.web.controller.LogoUploadController;
import com.payasia.web.util.PayAsiaSessionAttributes;

/**
 * The Class LogoUploadControllerImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Controller
@RequestMapping(value = { "/admin/logo", "/employee/logo" })
public class LogoUploadControllerImpl implements LogoUploadController {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(LogoUploadControllerImpl.class);

	/** The logo upload logic. */
	@Resource
	LogoUploadLogic logoUploadLogic;
	
	@Autowired
	private ServletContext servletContext;
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.LogoUploadController#logoUpload(com.payasia
	 * .common.form.LogoUploadForm, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	@RequestMapping(value = "/logoUpload.html", method = RequestMethod.POST, produces = "text/html; charset=utf-8")
	@ResponseBody
	public String logoUpload(
			@ModelAttribute("logoUploadForm") LogoUploadForm logoUploadForm,
			HttpServletRequest request) {
		    
		    boolean isValidImg = false;
			
		    if(logoUploadForm.getLogoImage()!=null){
		    	isValidImg = FileUtils.isValidFile(logoUploadForm.getLogoImage(), logoUploadForm.getLogoImage().getOriginalFilename(), 
		        PayAsiaConstants.ALLOWED_UPLOAD_IMAGE_EXT, PayAsiaConstants.ALLOWED_UPLOAD_IMAGE_MINE_TYPE, PayAsiaConstants.IMAGE_SIZE);
			}
		    if(isValidImg) {
			   logoUploadLogic.logoUpload(logoUploadForm,logoUploadForm.getCompanyId());
			   return PayAsiaConstants.PAYASIA_SUCCESS;
		    } else{
			   return PayAsiaConstants.INVALID_FILE;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.LogoUploadController#getLogo(long,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/getLogoImage", method = RequestMethod.GET)
	public @ResponseBody byte[] getLogo(
			@RequestParam(value = "companyId", required = true) long companyId,
			HttpServletRequest request, HttpServletResponse response) {

		String defaultImagePath = servletContext.getRealPath(
				"/resources/images/")
				+ "/company_logo.png";
		byte[] byteFile = logoUploadLogic.getLogo(companyId, defaultImagePath);
		String imageName = byteFile.getClass().getName();
		response.reset();
		String mimeType = URLConnection.guessContentTypeFromName("");
		response.setContentType("png");
		response.setContentLength(byteFile.length);

		response.setHeader("Content-Disposition", "inline;filename="
				+ imageName);
		return byteFile;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.LogoUploadController#getHeaderLogo(long,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, org.springframework.ui.ModelMap)
	 */
	@Override
	@RequestMapping(value = "/getHeaderLogo", method = RequestMethod.GET)
	public @ResponseBody void getHeaderLogo(
			HttpServletRequest request, HttpServletResponse response,
			ModelMap map) {

		
		ServletOutputStream outStream = null;
		try {
			String defaultImagePath = servletContext.getRealPath(
					"/resources/images/")
					+ "/company_logo.png";
			
			Long userCompanyId = Long.valueOf(UserContext.getWorkingCompanyId());
			
			byte[] byteFile = logoUploadLogic.getHeaderLogo(userCompanyId,
					defaultImagePath);
			response.reset();
			String mimeType = URLConnection.guessContentTypeFromName("");
			response.setContentType("png");
			response.setContentLength(byteFile.length);
			String imageName = byteFile.getClass().getName();

			response.setHeader("Content-Disposition", "inline;filename="
					+ imageName);

			outStream = response.getOutputStream();
			outStream.write(byteFile);

		} catch (IOException ex) {
			LOGGER.error(ex.getMessage(), ex);
		} finally {
			try {

				if (outStream != null) {

					outStream.flush();
					outStream.close();
				}

			} catch (IOException ex) {
				LOGGER.error(ex.getMessage(), ex);
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.LogoUploadController#getCompanyLogoSize(javax
	 * .servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/getCompanyLogoSize.html", method = RequestMethod.POST)
	@ResponseBody
	public String getCompanyLogoSize(HttpServletRequest request,
			HttpServletResponse response) {
		String maxImageFileSize = String.valueOf(logoUploadLogic
				.getCompanyLogoSize());

		return maxImageFileSize;

	}

	@Override
	@RequestMapping(value = "/getLogoHeightWidth.html", method = RequestMethod.POST)
	@ResponseBody
	public String getLogoHeightWidth(
			HttpServletRequest request, HttpServletResponse response,
			ModelMap map, HttpSession session) {
		Long userCompanyId = Long.valueOf(UserContext.getWorkingCompanyId());
		Long companyID = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		String newImageWidthheight = null;
		if (companyID.equals(userCompanyId)) {

			String defaultImagePath = servletContext.getRealPath(
					"/resources/images/")
					+ "/company_logo.png";

			newImageWidthheight = logoUploadLogic.getHeaderLogoWidthHeight(
					userCompanyId, defaultImagePath);
		}

		else {
			String defaultImagePath = servletContext.getRealPath(
					"/resources/images/")
					+ "/company_logo.png";

			newImageWidthheight = logoUploadLogic.getHeaderLogoWidthHeight(
					companyID, defaultImagePath);
		}
		return newImageWidthheight;

	}
}