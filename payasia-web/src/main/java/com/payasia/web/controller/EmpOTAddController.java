package com.payasia.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author vivekjain
 * 
 */
/**
 * The Interface EmpOTAddController.
 */
public interface EmpOTAddController {
	String viewEmailTemplates();

	/**
	 * purpose : Get OT Template List.
	 * 
	 * @param HttpServletRequest
	 *            the request
	 * @param HttpServletResponse
	 *            the response
	 * @return EmpOTAddForm contains list
	 */
	String getOTTemplateList(HttpServletRequest request,
			HttpServletResponse response);

	/**
	 * purpose : Get OT Reviewer List.
	 * 
	 * @param Long
	 *            the otTemplateId
	 * @param HttpServletRequest
	 *            the request
	 * @param HttpServletResponse
	 *            the response
	 * @return EmpOTAddForm contains list of reviewer
	 */
	String getOTReviewerList(Long otTemplateId, HttpServletRequest request,
			HttpServletResponse response);

	/**
	 * purpose : Get OT Day Type List.
	 * 
	 * @return EmpOTAddForm List
	 */
	String getOTDayTypeList();

	/**
	 * purpose : add OT Application.
	 * 
	 * @param String
	 *            the metaData
	 * @param String
	 *            the generalRemarks
	 * @param Long
	 *            the otTemplateId
	 * @param HttpServletRequest
	 *            the request
	 * @param HttpServletResponse
	 *            the response
	 */
	void addOTApplication(String metaData, Long otTemplateId,
			String generalRemarks, HttpServletRequest request,
			HttpServletResponse response);
}
