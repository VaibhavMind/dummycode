package com.payasia.web.controller.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.form.HrisChangeRequestForm;
import com.payasia.common.form.HrisMyRequestFormResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.logic.HrisMyRequestLogic;
import com.payasia.logic.MultilingualLogic;
import com.payasia.web.controller.HrisMyRequestController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@Controller
@RequestMapping(value = { "/employee/hrisMyRequest", "/admin/hrisMyRequest" })
public class HrisMyRequestControllerImpl implements HrisMyRequestController {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(HrisMyRequestControllerImpl.class);

	/** The message source. */
	@Autowired
	private MessageSource messageSource;

	@Resource
	HrisMyRequestLogic hrisMyRequestLogic;
	/** The multilingual logic. */
	@Resource
	MultilingualLogic multilingualLogic;

	@Override
	@RequestMapping(value = "/getSubmittedRequest.html", method = RequestMethod.POST)
	@ResponseBody public String getSubmittedRequest(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "searchCondition", required = false) String searchCondition,
			@RequestParam(value = "searchText", required = false) String searchText,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session, Locale locale) {
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		Long languageId = multilingualLogic.getLanguageId(locale.toString());
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		String pageContextPath = request.getContextPath();

		HrisMyRequestFormResponse hrisMyRequestFormResponse = null;
		hrisMyRequestFormResponse = hrisMyRequestLogic.getSubmittedRequest(
				employeeId, pageDTO, sortDTO, pageContextPath, searchCondition,
				searchText, languageId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(
				hrisMyRequestFormResponse, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/getApprovedRequest.html", method = RequestMethod.POST)
	@ResponseBody public String getApprovedRequest(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "searchCondition", required = false) String searchCondition,
			@RequestParam(value = "searchText", required = false) String searchText,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session, Locale locale) {
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		Long languageId = multilingualLogic.getLanguageId(locale.toString());
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		String pageContextPath = request.getContextPath();

		HrisMyRequestFormResponse hrisMyRequestFormResponse = null;
		hrisMyRequestFormResponse = hrisMyRequestLogic.getApprovedRequest(
				employeeId, pageDTO, sortDTO, pageContextPath, searchCondition,
				searchText, languageId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(
				hrisMyRequestFormResponse, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/getRejectedRequest.html", method = RequestMethod.POST)
	@ResponseBody public String getRejectedRequest(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "searchCondition", required = false) String searchCondition,
			@RequestParam(value = "searchText", required = false) String searchText,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session, Locale locale) {
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		Long languageId = multilingualLogic.getLanguageId(locale.toString());
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		String pageContextPath = request.getContextPath();

		HrisMyRequestFormResponse hrisMyRequestFormResponse = null;
		hrisMyRequestFormResponse = hrisMyRequestLogic.getRejectedRequest(
				employeeId, pageDTO, sortDTO, pageContextPath, searchCondition,
				searchText, languageId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(
				hrisMyRequestFormResponse, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/getWithdrawnRequest.html", method = RequestMethod.POST)
	@ResponseBody public String getWithdrawnRequest(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "searchCondition", required = false) String searchCondition,
			@RequestParam(value = "searchText", required = false) String searchText,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session, Locale locale) {
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		Long languageId = multilingualLogic.getLanguageId(locale.toString());
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		String pageContextPath = request.getContextPath();

		HrisMyRequestFormResponse hrisMyRequestFormResponse = null;
		hrisMyRequestFormResponse = hrisMyRequestLogic.getWithdrawnRequest(
				employeeId, pageDTO, sortDTO, pageContextPath, searchCondition,
				searchText, languageId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(
				hrisMyRequestFormResponse, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/viewChangeRequest.html", method = RequestMethod.POST)
	@ResponseBody public String viewChangeRequest(
			@RequestParam(value = "hrisChangeRequestId", required = false) Long hrisChangeRequestId,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session, Locale locale) {
		
		/* ID DYCRYPT*/
		hrisChangeRequestId = FormatPreserveCryptoUtil.decrypt(hrisChangeRequestId);
		Long languageId = multilingualLogic.getLanguageId(locale.toString());
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		HrisChangeRequestForm hrisChangeRequestForm = hrisMyRequestLogic
				.viewChangeRequest(hrisChangeRequestId, employeeId, languageId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(hrisChangeRequestForm,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/withdrawHrisChangeRequest.html", method = RequestMethod.POST)
	@ResponseBody public String withdrawChangeRequest(
			@RequestParam(value = "hrisChangeRequestId", required = false) Long hrisChangeRequestId,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session, Locale locale) {
		
		/*ID DYCRYPT*/
		hrisChangeRequestId = FormatPreserveCryptoUtil.decrypt(hrisChangeRequestId);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		Long languageId = multilingualLogic.getLanguageId(locale.toString());
		HrisChangeRequestForm hrisChangeRequestForm = hrisMyRequestLogic
				.withdrawChangeRequest(hrisChangeRequestId, employeeId,
						languageId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(hrisChangeRequestForm,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}
}
