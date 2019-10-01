package com.payasia.web.controller.impl;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.form.ReportOptionsPreferenceForm;
import com.payasia.logic.ReportOptionsPreferenceLogic;
import com.payasia.web.controller.ReportOptionsPreferenceController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

/**
 * The Class ReportOptionsPreferenceControllerImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Controller
@RequestMapping(value = "/admin/reportOptions")
public class ReportOptionsPreferenceControllerImpl implements
		ReportOptionsPreferenceController {

	/** The report options preference logic. */
	@Resource
	ReportOptionsPreferenceLogic reportOptionsPreferenceLogic;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.ReportOptionsPreferenceController#
	 * getReportFormatOptions(javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/getReportFormatOptions.html", method = RequestMethod.POST)
	public @ResponseBody
	String getReportFormatOptions(HttpServletRequest request,
			HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		List<ReportOptionsPreferenceForm> reportOptionsList = reportOptionsPreferenceLogic
				.getReportFormatOptions(companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(reportOptionsList,
				jsonConfig);
		return jsonObject.toString();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.ReportOptionsPreferenceController#saveReportFormat
	 * (java.lang.String[], java.lang.String[],
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/saveReportFormat.html", method = RequestMethod.POST)
	public void saveReportFormat(
			@RequestParam(value = "reportIdArr", required = true) String[] reportIdArr,
			@RequestParam(value = "reportFmtMappingIdArr", required = true) String[] reportFmtMappingIdArr,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		reportOptionsPreferenceLogic.saveReportFormat(reportIdArr,
				reportFmtMappingIdArr, companyId);

	}

}
