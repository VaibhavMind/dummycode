package com.payasia.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author vivekjain
 * 
 */
/**
 * The Interface PaySlipDesignerController.
 */
public interface PaySlipDesignerController {

	/**
	 * purpose : get Label List For PaySlipDesigner.
	 * 
	 * @param HttpServletRequest
	 *            the request
	 * @param HttpServletResponse
	 *            the response
	 * @return PayslipDesignerResponse contains Label List
	 */
	String getLabelListForPaySlipDesigner(HttpServletRequest request,
			HttpServletResponse response);

	/**
	 * purpose : Get XML.
	 * 
	 * @param String
	 *            sectionName
	 * @param HttpServletRequest
	 *            the request
	 * @param HttpServletResponse
	 *            the response
	 */
	String getXML(String sectionName, HttpServletRequest request,
			HttpServletResponse response);

	/**
	 * purpose : generate Payslip Pdf.
	 * 
	 * @param HttpServletRequest
	 *            the request
	 * @param HttpServletResponse
	 *            the response
	 */
	void generatePdf(HttpServletRequest request, HttpServletResponse response);

	/**
	 * purpose : save XML.
	 * 
	 * @param metaData
	 *            the meta data
	 * @param year
	 *            the year
	 * @param month
	 *            the month
	 * @param part
	 *            the part
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return
	 */

	String saveXML(String[] metaData, int year, long month, int part,
			HttpServletRequest request, HttpServletResponse response);

}
