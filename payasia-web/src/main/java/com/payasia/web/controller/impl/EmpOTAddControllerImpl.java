package com.payasia.web.controller.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.form.EmpOTAddForm;
import com.payasia.common.form.EmpOTAddResponse;
import com.payasia.logic.EmpOTAddLogic;
import com.payasia.web.controller.EmpOTAddController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * The Class EmpOTAddControllerImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Controller
@RequestMapping(value = "/addOT")
public class EmpOTAddControllerImpl implements EmpOTAddController {

	/** The emp ot add logic. */
	@Resource
	EmpOTAddLogic empOTAddLogic;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.EmpOTAddController#viewEmailTemplates()
	 */
	@Override
	@RequestMapping(value = "/viewOT", method = RequestMethod.GET)
	public @ResponseBody
	String viewEmailTemplates() {

		EmpOTAddResponse response = new EmpOTAddResponse();
		List<EmpOTAddForm> list = new ArrayList<EmpOTAddForm>();

		 
		int totalRecords;
		totalRecords = list.size();
		response.setRows(list);
		 
		int recordSize = totalRecords;
		int pageSize = 10;
		int totalPages = recordSize / pageSize;
		if (recordSize % pageSize != 0) {
			totalPages = totalPages + 1;
		}
		if (recordSize == 0) {
			totalPages = 0;
		}

		response.setRecords(String.valueOf(recordSize));
		response.setPage("1");
		response.setTotal(String.valueOf(totalPages));

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(response, jsonConfig);
		return jsonObject.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.EmpOTAddController#getOTTemplateList(javax
	 * .servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/getOTTemplateList", method = RequestMethod.POST)
	public @ResponseBody
	String getOTTemplateList(HttpServletRequest request,
			HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		List<EmpOTAddForm> otTemplateList = empOTAddLogic
				.getOTTemplateList(companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(otTemplateList, jsonConfig);
		return jsonObject.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.EmpOTAddController#getOTDayTypeList()
	 */
	@Override
	@RequestMapping(value = "/getOTDayTypeList", method = RequestMethod.POST)
	public @ResponseBody
	String getOTDayTypeList() {
		List<EmpOTAddForm> otDayTypeList = empOTAddLogic.getOTDayTypeList();
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(otDayTypeList, jsonConfig);
		return jsonObject.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.EmpOTAddController#getOTReviewerList(java.
	 * lang.Long, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/getOTReviewerList", method = RequestMethod.POST)
	public @ResponseBody
	String getOTReviewerList(
			@RequestParam(value = "otTemplateId") Long otTemplateId,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		EmpOTAddForm otTemplateForm = empOTAddLogic.getOTReviewerList(
				otTemplateId, companyId, employeeId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(otTemplateForm,
				jsonConfig);
		return jsonObject.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.EmpOTAddController#addOTApplication(java.lang
	 * .String, java.lang.Long, java.lang.String,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/addOTApplication", method = RequestMethod.POST)
	public @ResponseBody
	void addOTApplication(@RequestParam(value = "metaData") String metaData,
			@RequestParam(value = "otTemplateId") Long otTemplateId,
			@RequestParam(value = "generalRemarks") String generalRemarks,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		empOTAddLogic.addOTApplication(otTemplateId, companyId, employeeId,
				metaData, generalRemarks);
	}

}
