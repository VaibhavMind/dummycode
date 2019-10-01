/**
 * @author abhisheksachdeva
 */
package com.payasia.web.controller.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.dto.MonthMasterDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.CalculatoryFieldFormResponse;
import com.payasia.common.form.PaySlipDynamicForm;
import com.payasia.common.util.FileUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.logic.PaySlipDynamicFormLogic;
import com.payasia.web.controller.PaySlipDynamicFormController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * The Class PaySlipDynamicFormControllerImpl.
 * 
 */
@Controller
@RequestMapping(value = "/admin/payslipDynamicForm")
public class PaySlipDynamicFormControllerImpl implements
		PaySlipDynamicFormController {

	/** The pay slip dynamic form logic. */
	@Resource
	PaySlipDynamicFormLogic paySlipDynamicFormLogic;
	private static final Logger LOGGER = Logger
			.getLogger(PaySlipDynamicFormControllerImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.PaySlipDynamicFormController#saveXML(java.
	 * lang.String, java.lang.String, long,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/saveXML.html", method = RequestMethod.POST)
	@ResponseBody public String saveXML(
			@RequestParam(value = "metaData", required = true) String metaData,
			@RequestParam(value = "tabName", required = true) String tabName,
			@RequestParam(value = "formId", required = true) long formId,
			@RequestParam(value = "year") int year,
			@RequestParam(value = "month") long month,
			@RequestParam(value = "part") int part,
			@RequestParam(value = "effectiveDateChanged") boolean effectiveDateChanged,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		String responseString = "";
		try {
			
			final String decodedMetaData = URLDecoder.decode(metaData, "UTF-8");
			
			responseString = paySlipDynamicFormLogic.saveXML(companyId,
					decodedMetaData, tabName, formId, year, month, part,
					effectiveDateChanged);

		} catch (PayAsiaSystemException payAsiaSystemException) {
			LOGGER.error(payAsiaSystemException.getMessage(),
					payAsiaSystemException);
			responseString = payAsiaSystemException.getKey();
		}catch( UnsupportedEncodingException e){
			LOGGER.error(e.getMessage(),e);
			responseString = e.getMessage();
		}
		return responseString;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.PaySlipDynamicFormController#getXML(long,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/getXML.html", method = RequestMethod.POST)
	@ResponseBody public String getXML(
			@RequestParam(value = "formId", required = true) long formId,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		PaySlipDynamicForm paySlipDynamicForm = paySlipDynamicFormLogic.getXML(
				companyId, formId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(paySlipDynamicForm,
				jsonConfig);
		return jsonObject.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.PaySlipDynamicFormController#getOptionsFromXL
	 * (com.payasia.common.form.PaySlipDynamicForm,
	 * org.springframework.validation.BindingResult,
	 * org.springframework.ui.ModelMap, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/getOptionsFromXL.html", method = RequestMethod.POST)
	@ResponseBody public String getOptionsFromXL(
			@ModelAttribute(value = "paySlipDynamicForm") PaySlipDynamicForm paySlipDynamicForm,
			BindingResult result, ModelMap model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		PaySlipDynamicForm paySlpDynamicForm = new PaySlipDynamicForm();

		if(paySlipDynamicForm.getAttachment()!=null){
			
			boolean isFileValid = FileUtils.isValidFile(paySlipDynamicForm.getAttachment(), paySlipDynamicForm.getAttachment().getOriginalFilename(), PayAsiaConstants.ALLOWED_STANDARD_UPLOAD_FILE_EXT, PayAsiaConstants.ALLOWED_STANDARD_UPLOAD_FILE_MINE_TYPE, PayAsiaConstants.FILE_SIZE);
		
			if(isFileValid){
				paySlpDynamicForm = paySlipDynamicFormLogic.getOptionsFromXL(paySlipDynamicForm);
			}
		}
		
		return paySlpDynamicForm.getOptions();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.PaySlipDynamicFormController#deleteForm(long,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/deleteForm.html", method = RequestMethod.POST)
	@ResponseBody public String deleteForm(long formId,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		String status = paySlipDynamicFormLogic.deleteTab(companyId, formId);
		return status;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.PaySlipDynamicFormController#saveDynamicXML
	 * (java.lang.String, java.lang.String,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/saveDynamicXML.html", method = RequestMethod.POST)
	@ResponseBody public String saveDynamicXML(
			@RequestParam(value = "metaData", required = true) String metaData,
			@RequestParam(value = "tabName", required = true) String tabName,
			@RequestParam(value = "year") int year,
			@RequestParam(value = "month") long month,
			@RequestParam(value = "part") int part, HttpServletRequest request,
			HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		
		PaySlipDynamicForm payslipDynamicForm = null;

		try{
	
		 final String decodedMetaData = URLDecoder.decode(metaData, "UTF-8");
	     payslipDynamicForm = paySlipDynamicFormLogic.saveDynamicXML(companyId, decodedMetaData, tabName, year, month, part);

		}catch(Exception exception){
			LOGGER.error(exception.getMessage(),exception);
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(payslipDynamicForm,
				jsonConfig);
		return jsonObject.toString();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.PaySlipDynamicFormController#getCalculatioryFields
	 * (javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/getCalculatioryFields.html", method = RequestMethod.POST)
	@ResponseBody public String getCalculatioryFields(
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		CalculatoryFieldFormResponse calculatoryFieldFormResponse = paySlipDynamicFormLogic
				.getCalculatioryFields(companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(
				calculatoryFieldFormResponse, jsonConfig);
		return jsonObject.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.PaySlipDynamicFormController#getDictionaryNames
	 * (java.lang.String[], javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/getDictionaryNames.html", method = RequestMethod.POST)
	@ResponseBody public String getDictionaryNames(
			@RequestParam(value = "dictionaryIds", required = true) String[] dictionaryIds,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Map<Long, String> dictionaryNames = paySlipDynamicFormLogic
				.getDictionaryLabel(companyId, dictionaryIds);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(dictionaryNames,
				jsonConfig);
		return jsonObject.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.PaySlipDynamicFormController#getTabList(javax
	 * .servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/getTabList.html", method = RequestMethod.POST)
	@ResponseBody public String getTabList(HttpServletRequest request,
			HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		List<PaySlipDynamicForm> tabList = paySlipDynamicFormLogic
				.getTabList(companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(tabList, jsonConfig);
		return jsonObject.toString();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.PaySlipDynamicFormController#checkFieldEdit
	 * (java.lang.Long, java.lang.String, boolean, java.lang.String,
	 * javax.servlet.http.HttpServletRequest)
	 */
	@Override
	@RequestMapping(value = "/checkFieldEdit.html", method = RequestMethod.POST)
	@ResponseBody public String checkFieldEdit(
			@RequestParam(value = "formId") Long formId,
			@RequestParam(value = "fieldName") String fieldName,
			@RequestParam(value = "isTable") boolean isTable,
			@RequestParam(value = "tablePosition") String tablePosition,
			HttpServletRequest request) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		String response = paySlipDynamicFormLogic.checkFieldEdit(formId,
				fieldName, isTable, companyId, tablePosition);
		return response;

	}

	@Override
	@RequestMapping(value = "/getMonthList.html", method = RequestMethod.POST)
	@ResponseBody public String getMonthList(HttpServletRequest request,
			HttpServletResponse response) {
		List<MonthMasterDTO> monthList = paySlipDynamicFormLogic.getMonthList();
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(monthList, jsonConfig);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/getPayslipReleasedStatus.html", method = RequestMethod.POST)
	@ResponseBody public String getPayslipReleasedStatus(Long month, int year,
			Integer part, HttpServletRequest request,
			HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		boolean isPayslipReleased = paySlipDynamicFormLogic
				.getPayslipReleasedStatus(companyId, month, year, part);
		return String.valueOf(isPayslipReleased);
	}
}
