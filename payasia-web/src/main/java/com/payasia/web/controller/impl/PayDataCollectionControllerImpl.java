package com.payasia.web.controller.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
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

import com.payasia.common.form.PageRequest;
import com.payasia.common.form.PayCodeDataForm;
import com.payasia.common.form.PayCodeDataFormResponse;
import com.payasia.common.form.PayDataCollectionForm;
import com.payasia.common.form.PayDataCollectionResponseForm;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FileUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.logic.PayCodeDataLogic;
import com.payasia.logic.PayDataCollectionLogic;
import com.payasia.web.controller.PayCodeDataController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * The Class PayDataCollectionControllerImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Controller
@RequestMapping(value = "/admin/payCode")
public class PayDataCollectionControllerImpl implements PayCodeDataController {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(PayDataCollectionControllerImpl.class);

	/** The pay code data logic. */
	@Resource
	PayCodeDataLogic payCodeDataLogic;

	/** The pay data collection logic. */
	@Resource
	PayDataCollectionLogic payDataCollectionLogic;

	/** The message source. */
	@Autowired
	private MessageSource messageSource;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.PayCodeDataController#payCodeDetails(int,
	 * int, java.lang.String, java.lang.String,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/payCodeDetails.html", method = RequestMethod.POST)
	public @ResponseBody
	String payCodeDetails(
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
		PayCodeDataFormResponse payCodeDataFormResponse = payCodeDataLogic
				.getPayCodedetails(companyId, pageDTO, sortDTO);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(payCodeDataFormResponse,
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
	 * com.payasia.web.controller.PayCodeDataController#addPayCode(java.lang
	 * .String, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/addPayCode.html", method = RequestMethod.POST)
	public @ResponseBody
	void addPayCode(
			@RequestParam(value = "payCode", required = true) String payCode,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		payCodeDataLogic.addPayCode(companyId, payCode);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.PayCodeDataController#getPayCodeData(long)
	 */
	@Override
	@RequestMapping(value = "/getDataForPayCode.html", method = RequestMethod.POST)
	public @ResponseBody
	String getPayCodeData(
			@RequestParam(value = "payCodeId", required = true) long payCodeId) {

		PayCodeDataForm payCodeDataForm = payCodeDataLogic
				.getPayCodeData(payCodeId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(payCodeDataForm,
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
	 * com.payasia.web.controller.PayCodeDataController#updatePaycodeData(com
	 * .payasia.common.form.PayCodeDataForm,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/updatePayCode.html", method = RequestMethod.POST)
	public @ResponseBody
	void updatePaycodeData(
			@ModelAttribute("payCodeDataForm") PayCodeDataForm payCodeDataForm,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		payCodeDataLogic.updatePaycodeData(payCodeDataForm, companyId);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.PayCodeDataController#deletePaycode(long,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, java.util.Locale)
	 */
	@Override
	@RequestMapping(value = "/deletePayCode.html", method = RequestMethod.POST)
	public @ResponseBody
	String deletePaycode(
			@RequestParam(value = "payCodeId", required = true) long payCodeId,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		String statusMsg = payCodeDataLogic.deletePaycode(companyId, payCodeId);
		try {
			statusMsg = URLEncoder.encode(messageSource.getMessage(statusMsg,
					new Object[] {}, locale), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		} catch (NoSuchMessageException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return statusMsg;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.PayCodeDataController#getPayCodeFromXL(com
	 * .payasia.common.form.PayCodeDataForm,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/getPayCodeFromXL.html", method = RequestMethod.POST)
	public void getPayCodeFromXL(
			@ModelAttribute("payCodeDataForm") PayCodeDataForm payCodeDataForm,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		if(payCodeDataForm.getFileUpload() != null){
			boolean isFileValid = FileUtils.isValidFile(payCodeDataForm.getFileUpload(), payCodeDataForm.getFileUpload().getOriginalFilename(), PayAsiaConstants.ALLOWED_STANDARD_UPLOAD_FILE_EXT, PayAsiaConstants.ALLOWED_STANDARD_UPLOAD_FILE_MINE_TYPE, PayAsiaConstants.FILE_SIZE);
		
			if(isFileValid){
				
				payCodeDataLogic.getPayCodeFromXL(payCodeDataForm, companyId);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.PayCodeDataController#filterEmployeeList(java
	 * .lang.String, java.lang.String, java.lang.String, java.lang.String, int,
	 * int, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/searchEmployee.html", method = RequestMethod.POST)
	public @ResponseBody
	String filterEmployeeList(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "searchCondition", required = true) String searchCondition,
			@RequestParam(value = "searchText", required = true) String searchText,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			HttpServletRequest request, HttpServletResponse response) {

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		PayDataCollectionResponseForm payDataCollectionResponseForm = payDataCollectionLogic
				.getEmployeeList(companyId, searchCondition, searchText,
						pageDTO, sortDTO, employeeId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(
				payDataCollectionResponseForm, jsonConfig);
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
	 * @see com.payasia.web.controller.PayCodeDataController#
	 * getPayDataCollectionForEmployee(java.lang.Long,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/getPayDataCollectionForEmployee.html", method = RequestMethod.POST)
	public @ResponseBody
	String getPayDataCollectionForEmployee(
			@RequestParam(value = "payDataCollectionId", required = true) Long payDataCollectionId,
			HttpServletRequest request, HttpServletResponse response) {
		
		/*ID DECRYPT*/
		payDataCollectionId = FormatPreserveCryptoUtil.decrypt(payDataCollectionId);
		
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		PayDataCollectionForm payDataCollectionForm = payDataCollectionLogic
				.getPayDataCollectionForEdit(companyId, payDataCollectionId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(payDataCollectionForm,
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
	 * com.payasia.web.controller.PayCodeDataController#addPayDataCollection
	 * (java.lang.String, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, java.util.Locale)
	 */
	@Override
	@RequestMapping(value = "/addPayDataCollection.html", method = RequestMethod.POST)
	public @ResponseBody
	String addPayDataCollection(
			@RequestParam(value = "metaData", required = true) String metaData,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		String status = null;
		try {
			
			String decodeMetaData = URLDecoder.decode(metaData,"UTF-8");
			Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		    status = payDataCollectionLogic.addPayDataCollection(companyId,decodeMetaData);
			status = URLEncoder.encode(messageSource.getMessage(status, new Object[] {}, locale),"UTF-8");
		}catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		} catch (NoSuchMessageException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return status;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.PayCodeDataController#updatePayDataCollection
	 * (com.payasia.common.form.PayDataCollectionForm,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/updatePayDataCollection.html", method = RequestMethod.POST)
	public void updatePayDataCollection(
			@ModelAttribute("payDataCollectionForm") PayDataCollectionForm payDataCollectionForm,
			HttpServletRequest request, HttpServletResponse response) {
		
		/*ID DECRYPT*/
		payDataCollectionForm.setPayDataCollectionId(FormatPreserveCryptoUtil.decrypt(payDataCollectionForm
				.getPayDataCollectionId()));
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		payDataCollectionLogic.updatePayDataCollection(payDataCollectionForm,
				companyId);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.PayCodeDataController#deletePayDataCollection
	 * (java.lang.Long)
	 */
	@Override
	@RequestMapping(value = "/deletePayDataCollection.html", method = RequestMethod.POST)
	public void deletePayDataCollection(
			@RequestParam(value = "payDataCollectionId", required = true) Long payDataCollectionId) {
		
		/*ID DECRYPT*/
		payDataCollectionId = FormatPreserveCryptoUtil.decrypt(payDataCollectionId);
		
		payDataCollectionLogic.deletePayDataCollection(payDataCollectionId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.PayCodeDataController#getEmployeeId(java.lang
	 * .String, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/getEmployeeId.html", method = RequestMethod.POST)
	public @ResponseBody
	String getEmployeeId(
			@RequestParam(value = "searchString", required = true) String searchString,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		List<PayDataCollectionForm> payDataCollectionFormList = payDataCollectionLogic
				.getEmployeeId(companyId, searchString, employeeId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(payDataCollectionFormList,
				jsonConfig);
		return jsonObject.toString();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.PayCodeDataController#getPayCode(java.lang
	 * .String, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/getPayCode.html", method = RequestMethod.POST)
	public @ResponseBody
	String getPayCode(
			@RequestParam(value = "searchString", required = true) String searchString,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		List<PayDataCollectionForm> payDataCollectionFormList = payDataCollectionLogic
				.getPayCode(companyId, searchString);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(payDataCollectionFormList,
				jsonConfig);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/getPayCodeList.html", method = RequestMethod.POST)
	public @ResponseBody
	String getPayCodeList(HttpServletRequest request,
			HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		List<PayDataCollectionForm> payDataCollectionFormList = payDataCollectionLogic
				.getAllPayCode(companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(payDataCollectionFormList,
				jsonConfig);
		return jsonObject.toString();

	}
}
