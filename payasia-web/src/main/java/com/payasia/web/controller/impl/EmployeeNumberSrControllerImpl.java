package com.payasia.web.controller.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.form.EmployeeNumberSrForm;
import com.payasia.common.form.EmployeeNumberSrFormResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.logic.EmployeeNumberSrLogic;
import com.payasia.web.controller.EmployeeNumberSrController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * The Class EmployeeNumberSrControllerImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Controller
@RequestMapping(value = "/admin/employeeNumberSr")
public class EmployeeNumberSrControllerImpl implements
		EmployeeNumberSrController {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(EmployeeNumberSrControllerImpl.class);

	/** The message source. */
	@Autowired
	private MessageSource messageSource;

	/** The employee number sr logic. */
	@Resource
	EmployeeNumberSrLogic employeeNumberSrLogic;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.EmployeeNumberSrController#viewEmpNoSr(int,
	 * int, java.lang.String, java.lang.String,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/employeeNSr", method = RequestMethod.POST)
	@ResponseBody public String viewEmpNoSr(
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			HttpServletRequest request, HttpServletResponse response) {

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		EmployeeNumberSrFormResponse empNumSrFormResponse = employeeNumberSrLogic
				.viewEmpNoSr(companyId, pageDTO, sortDTO);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(empNumSrFormResponse,
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
	 * com.payasia.web.controller.EmployeeNumberSrController#saveNewSeries(com
	 * .payasia.common.form.EmployeeNumberSrForm,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, java.util.Locale)
	 */
	@Override
	@RequestMapping(value = "/addNewSeries", method = RequestMethod.POST)
	@ResponseBody public String saveNewSeries(
			@ModelAttribute("employeeNumberSrForm") EmployeeNumberSrForm employeeNumberSrForm,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		String saveStatus = employeeNumberSrLogic.saveNewSeries(
				employeeNumberSrForm, companyId);
		try {
			saveStatus = URLEncoder.encode(messageSource.getMessage(saveStatus,
					new Object[] {}, locale), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		} catch (NoSuchMessageException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return saveStatus;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.EmployeeNumberSrController#editSeries(com.
	 * payasia.common.form.EmployeeNumberSrForm,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/editSeries", method = RequestMethod.POST)
	@ResponseBody public String editSeries(
			@ModelAttribute("employeeNumberSrForm") EmployeeNumberSrForm employeeNumberSrForm,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		/*ID DECRYPT*/
		employeeNumberSrForm.setEmpNoSeriesId(FormatPreserveCryptoUtil.decrypt(employeeNumberSrForm.getEmpNoSeriesId()));
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		String saveStatus = employeeNumberSrLogic.editSeries(companyId,
				employeeNumberSrForm);

		try {
			saveStatus = URLEncoder.encode(messageSource.getMessage(saveStatus,
					new Object[] {}, locale), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		} catch (NoSuchMessageException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return saveStatus;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.EmployeeNumberSrController#deleteSeries(long)
	 */
	@Override
	@RequestMapping(value = "/deleteSeries", method = RequestMethod.POST)
	@ResponseBody public String deleteSeries(
			@RequestParam(value = "empNoSeriesId", required = true) long empNoSeriesId,
			Locale locale) {
		/*ID DECRYPT*/
		empNoSeriesId = FormatPreserveCryptoUtil.decrypt(empNoSeriesId);
		String saveStatus = employeeNumberSrLogic.deleteSeries(empNoSeriesId);

		try {
			saveStatus = URLEncoder.encode(messageSource.getMessage(saveStatus,
					new Object[] {}, locale), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		} catch (NoSuchMessageException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return saveStatus;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.EmployeeNumberSrController#getDataForSeries
	 * (long)
	 */
	@Override
	@RequestMapping(value = "/empNumSeriesDataForEdit", method = RequestMethod.POST)
	@ResponseBody public String getDataForSeries(
			@RequestParam(value = "empNoSeriesId", required = true) long empNoSeriesId) {
		/*ID DECRYPT*/
		empNoSeriesId = FormatPreserveCryptoUtil.decrypt(empNoSeriesId);
		EmployeeNumberSrForm employeeNumberSrForm = employeeNumberSrLogic
				.getDataForSeries(empNoSeriesId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(employeeNumberSrForm,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}

}