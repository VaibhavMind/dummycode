package com.payasia.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.payasia.common.form.ExternalLinkForm;

/**
 * @author vivekjain
 * 
 */
/**
 * The Interface ExternalLinkController.
 */
public interface ExternalLinkController {

	/**
	 * purpose : get ExternalLink Data.
	 * 
	 * @param HttpServletRequest
	 *            the request
	 * @param HttpServletResponse
	 *            the response
	 * @return ExternalLinkForm
	 */
	String getExternalLink(HttpServletRequest request,
			HttpServletResponse response);

	/**
	 * purpose : get ExternalLink Image.
	 * 
	 * @param HttpServletRequest
	 *            the request
	 * @param HttpServletResponse
	 *            the response
	 * @return Image in byte array
	 */
	byte[] getExternalLinkImage(HttpServletRequest request,
			HttpServletResponse response);

	/**
	 * purpose : get ExternalLink Image Size.
	 * 
	 * @param HttpServletRequest
	 *            the request
	 * @param HttpServletResponse
	 *            the response
	 * @return Image Size
	 */
	String getExtLinkImageSize(HttpServletRequest request,
			HttpServletResponse response);

	/**
	 * purpose : Save ExternalLink data.
	 * 
	 * @param HttpServletRequest
	 *            the request
	 * @param ExternalLinkForm
	 *            the externalLinkForm
	 * @return Response
	 */
	String saveExternalLink(ExternalLinkForm externalLinkForm,
			HttpServletRequest request);

	/**
	 * purpose : Get ExternalLink Home Page Image.
	 * 
	 * @param HttpServletRequest
	 *            the request
	 * @param ExternalLinkForm
	 *            the externalLinkForm
	 * @return Image in byte array
	 */
	void getImage(Integer imageWidth, Integer imageHeight,
			HttpServletRequest request, HttpServletResponse response);

}
