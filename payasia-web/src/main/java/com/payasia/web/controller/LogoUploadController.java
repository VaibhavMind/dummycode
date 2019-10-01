package com.payasia.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.ui.ModelMap;

import com.payasia.common.form.LogoUploadForm;

/**
 * @author vivekjain
 * 
 */
/**
 * The Interface LogoUploadController.
 */
public interface LogoUploadController {
	/**
	 * purpose : get Logo.
	 * 
	 * @param long
	 *            the companyId
	 * @param HttpServletRequest
	 *            the request
	 * @param HttpServletResponse
	 *            the response
	 * @return Logo Image
	 */
	byte[] getLogo(long companyId, HttpServletRequest request,
			HttpServletResponse response);

	/**
	 * purpose : logo Upload.
	 * 
	 * @param LogoUploadForm
	 *            the logoUploadForm
	 * @param HttpServletRequest
	 *            the request
	 * @return
	 */
	String logoUpload(LogoUploadForm logoUploadForm, HttpServletRequest request);

	/**
	 * purpose : get Company Logo Size.
	 * 
	 * @param HttpServletResponse
	 *            the response
	 * @param HttpServletRequest
	 *            the request
	 * @return Logo Size
	 */
	String getCompanyLogoSize(HttpServletRequest request,
			HttpServletResponse response);

	/**
	 * purpose : get Header Logo.
	 * 
	 * @param HttpServletResponse
	 *            the response
	 * @param HttpServletRequest
	 *            the request
	 * @return Logo
	 */
	void getHeaderLogo(HttpServletRequest request,
			HttpServletResponse response, ModelMap map);

	/**
	 * purpose : get Logo Image Width And Height.
	 * 
	 * @param HttpServletResponse
	 *            the response
	 * @param HttpServletRequest
	 *            the request
	 * @return Image Width And Height
	 */
	String getLogoHeightWidth(HttpServletRequest request,
			HttpServletResponse response, ModelMap map, HttpSession session);

}