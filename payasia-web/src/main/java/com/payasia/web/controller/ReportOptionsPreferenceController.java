package com.payasia.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author vivekjain
 * 
 */
/**
 * The Interface ReportOptionsPreferenceController.
 */
public  interface ReportOptionsPreferenceController {
	/**
	 * purpose : get ReportFormat Options.
	 * 
	 * @param HttpServletRequest
	 *            the request
	 * @param HttpServletResponse
	 *            the response
	 * @return ReportOptionsPreferenceForm contains All ReportFormat
	 */
	 String getReportFormatOptions(HttpServletRequest request,
			HttpServletResponse response);

	/**
	 * purpose : Save ReportFormat .
	 * 
	 * @param String
	 *            [] the reportIdArr
	 * @param String
	 *            [] the reportFmtMappingIdArr
	 * @param HttpServletRequest
	 *            the request
	 * @param HttpServletResponse
	 *            the response
	 */
	 void saveReportFormat(String[] reportIdArr,
			String[] reportFmtMappingIdArr, HttpServletRequest request,
			HttpServletResponse response);

}
