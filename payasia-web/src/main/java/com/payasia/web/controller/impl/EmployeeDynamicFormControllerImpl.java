/**
 * @author abhisheksachdeva
 *
 */
package com.payasia.web.controller.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
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

import com.payasia.common.dto.CodeDescDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.CalculatoryFieldFormResponse;
import com.payasia.common.form.EmployeeDynamicForm;
import com.payasia.common.util.FileUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.logic.EmployeeDynamicFormLogic;
import com.payasia.web.controller.EmployeeDynamicFormController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * The Class EmployeeDynamicFormControllerImpl.
 */
@Controller
@RequestMapping(value = "/admin/employeeDynamicForm")
public class EmployeeDynamicFormControllerImpl implements
		EmployeeDynamicFormController {
	private static final Logger LOGGER = Logger
			.getLogger(EmployeeDynamicFormControllerImpl.class);
	/** The employee dynamic form logic. */
	@Resource
	EmployeeDynamicFormLogic employeeDynamicFormLogic;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.EmployeeDynamicFormController#saveXML(java
	 * .lang.String, java.lang.String, long,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/saveXML.html", method = RequestMethod.POST)
	@ResponseBody public String saveXML(
			@RequestParam(value = "metaData", required = true) String metaData,
			@RequestParam(value = "tabName", required = true) String tabName,
			@RequestParam(value = "formId", required = true) long formId,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		String responseString = "";
		try {

			final String decodedMetaData = URLDecoder.decode(metaData, "UTF-8");
			responseString = employeeDynamicFormLogic.saveXML(companyId,
					decodedMetaData, tabName, formId);

		} catch (PayAsiaSystemException e) {
			LOGGER.error(e.getMessage(), e);
			responseString = e.getKey();
		}catch(UnsupportedEncodingException ex){
			LOGGER.error(ex.getMessage(), ex);
			responseString = ex.getMessage();
		}
		return responseString;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.EmployeeDynamicFormController#getXML(long,
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
		EmployeeDynamicForm employeeDynamicForm = employeeDynamicFormLogic
				.getXML(companyId, formId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(employeeDynamicForm,
				jsonConfig);
		return jsonObject.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.EmployeeDynamicFormController#getOptionsFromXL
	 * (com.payasia.common.form.EmployeeDynamicForm,
	 * org.springframework.validation.BindingResult,
	 * org.springframework.ui.ModelMap, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/getOptionsFromXL.html", method = RequestMethod.POST)
	@ResponseBody public String getOptionsFromXL(
			@ModelAttribute(value = "employeeDynamicForm") EmployeeDynamicForm employeeDynamicForm,
			BindingResult result, ModelMap model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		EmployeeDynamicForm empDynamicForm = new EmployeeDynamicForm();

		if(employeeDynamicForm.getAttachment()!=null){
			
			boolean isFileValid = FileUtils.isValidFile(employeeDynamicForm.getAttachment(), employeeDynamicForm.getAttachment().getOriginalFilename(), PayAsiaConstants.ALLOWED_STANDARD_UPLOAD_FILE_EXT, PayAsiaConstants.ALLOWED_STANDARD_UPLOAD_FILE_MINE_TYPE, PayAsiaConstants.FILE_SIZE);
		
			if(isFileValid){
				empDynamicForm = employeeDynamicFormLogic
				.getOptionsFromXL(employeeDynamicForm);
			}
		}
		
		
		return empDynamicForm.getOptions();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.EmployeeDynamicFormController#getCodeDescFromXL
	 * (com.payasia.common.form.EmployeeDynamicForm,
	 * org.springframework.validation.BindingResult,
	 * org.springframework.ui.ModelMap, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/getCodeDescFromXL.html", method = RequestMethod.POST)
	@ResponseBody public String getCodeDescFromXL(
			@ModelAttribute(value = "employeeDynamicForm") EmployeeDynamicForm employeeDynamicForm,
			BindingResult result, ModelMap model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		List<CodeDescDTO> codeDescList = new ArrayList<CodeDescDTO>();
		if(employeeDynamicForm.getAttachment()!=null){
			boolean isFileValid = FileUtils.isValidFile(employeeDynamicForm.getAttachment(), employeeDynamicForm.getAttachment().getOriginalFilename(), PayAsiaConstants.ALLOWED_GENERAL_UPLOAD_FILE_EXT, PayAsiaConstants.ALLOWED_GENERAL_UPLOAD_FILE_MINE_TYPE, PayAsiaConstants.FILE_SIZE);
		
			if(isFileValid){
				codeDescList = employeeDynamicFormLogic
						.getCodeDescFromXL(employeeDynamicForm);
			}
		}
		
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(codeDescList, jsonConfig);
		return jsonObject.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.EmployeeDynamicFormController#deleteForm(long,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/deleteForm.html", method = RequestMethod.POST)
	@ResponseBody public String deleteForm(long formId,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		String employeeDynamicFormResponse = employeeDynamicFormLogic
				.deleteTab(companyId, formId);
		return employeeDynamicFormResponse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.EmployeeDynamicFormController#saveDynamicXML
	 * (java.lang.String, java.lang.String,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/saveDynamicXML.html", method = RequestMethod.POST)
	@ResponseBody public String saveDynamicXML(
			@RequestParam(value = "metaData", required = true) String metaData,
			@RequestParam(value = "tabName", required = true) String tabName,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		
		EmployeeDynamicForm employeeDynamicForm = null;

		try{
			 final String decodedMetaData = URLDecoder.decode(metaData, "UTF-8");
			employeeDynamicForm = employeeDynamicFormLogic.saveDynamicXML(companyId, decodedMetaData, tabName);

		}catch(Exception exception){
			LOGGER.error(exception.getMessage(),exception);
		}
		
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(employeeDynamicForm,
				jsonConfig);
		return jsonObject.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.EmployeeDynamicFormController#getTabList(javax
	 * .servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/getTabList.html", method = RequestMethod.POST)
	@ResponseBody public String getTabList(HttpServletRequest request,
			HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		List<EmployeeDynamicForm> tabList = employeeDynamicFormLogic
				.getTabList(companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(tabList, jsonConfig);
		return jsonObject.toString();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.EmployeeDynamicFormController#checkFieldEdit
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
		String response = employeeDynamicFormLogic.checkFieldEdit(formId,
				fieldName, isTable, companyId, tablePosition);
		return response;

	}

	@Override
	@RequestMapping(value = "/getCodeDescList.html", method = RequestMethod.POST)
	@ResponseBody public String getCodeDescList(
			@RequestParam(value = "dataDictionaryId") Long dataDictionaryId,
			HttpServletRequest request) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		List<CodeDescDTO> codeDescList = employeeDynamicFormLogic
				.getDynCodeDescList(dataDictionaryId, companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(codeDescList, jsonConfig);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/saveCodeDesc.html", method = RequestMethod.POST)
	@ResponseBody public String saveCodeDesc(
			@RequestParam(value = "metaData", required = true) String metaData,
			@RequestParam(value = "tabName", required = true) String tabName,
			@RequestParam(value = "formId", required = true) long formId,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		String responseString = "";
		try {
			responseString = employeeDynamicFormLogic.saveDynCodeDesc(
					companyId, metaData, tabName, formId);

		} catch (PayAsiaSystemException e) {
			LOGGER.error(e.getMessage(), e);
			responseString = e.getKey();
		}

		return responseString;

	}

	@Override
	@RequestMapping(value = "/getNumericDataDictionaryFields.html", method = RequestMethod.POST)
	@ResponseBody public String getNumericDataDictionaryFields(
			@RequestParam(value = "isTableField", required = false) boolean isTableField,
			@RequestParam(value = "tableDicId", required = false) long tableDicId,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		CalculatoryFieldFormResponse calculatoryFieldFormResponse = employeeDynamicFormLogic
				.getNumericOrDateTypeDataDictionaryFields(companyId,
						isTableField, tableDicId,
						PayAsiaConstants.FIELD_TYPE_NUMERIC);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(
				calculatoryFieldFormResponse, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/getStringDataDictionaryFields.html", method = RequestMethod.POST)
	@ResponseBody public String getStringDataDictionaryFields(
			@RequestParam(value = "isTableField", required = false) boolean isTableField,
			@RequestParam(value = "tableDicId", required = false) long tableDicId,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		CalculatoryFieldFormResponse calculatoryFieldFormResponse = employeeDynamicFormLogic
				.getStringTypeDataDictionaryFields(companyId, isTableField,
						tableDicId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(
				calculatoryFieldFormResponse, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/getDateDataDictionaryFields.html", method = RequestMethod.POST)
	@ResponseBody public String getDateDataDictionaryFields(
			@RequestParam(value = "isTableField", required = false) boolean isTableField,
			@RequestParam(value = "tableDicId", required = false) long tableDicId,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		CalculatoryFieldFormResponse calculatoryFieldFormResponse = employeeDynamicFormLogic
				.getNumericOrDateTypeDataDictionaryFields(companyId,
						isTableField, tableDicId,
						PayAsiaConstants.FIELD_TYPE_DATE);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(
				calculatoryFieldFormResponse, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/getReferenceDataDictionaryFields.html", method = RequestMethod.POST)
	@ResponseBody public String getReferenceDataDictionaryFields(
			@RequestParam(value = "isTableField", required = false) boolean isTableField,
			@RequestParam(value = "tableDicId", required = false) long tableDicId,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		CalculatoryFieldFormResponse calculatoryFieldFormResponse = employeeDynamicFormLogic
				.getReferenceDataDictionaryFields(companyId, isTableField,
						tableDicId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(
				calculatoryFieldFormResponse, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/getDictionaryNames.html", method = RequestMethod.POST)
	@ResponseBody public String getDictionaryNames(
			@RequestParam(value = "dictionaryIds", required = true) String[] dictionaryIds,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Map<Long, String> dictionaryNames = employeeDynamicFormLogic
				.getDictionaryLabel(companyId, dictionaryIds);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(dictionaryNames,
				jsonConfig);
		return jsonObject.toString();
	}

}
