/**
 * @author abhisheksachdeva
 *
 */
package com.payasia.web.controller.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

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
import com.payasia.common.form.CompanyDynamicForm;
import com.payasia.common.util.FileUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.logic.CompanyDynamicFormLogic;
import com.payasia.web.controller.CompanyDynamicFormController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * The Class CompanyDynamicFormControllerImpl.
 * 
 */
@Controller
@RequestMapping(value = "/admin/companyDynamicForm")
public class CompanyDynamicFormControllerImpl implements
		CompanyDynamicFormController {
	private static final Logger LOGGER = Logger
			.getLogger(CompanyDynamicFormControllerImpl.class);
	/** The company dynamic form logic. */
	@Resource
	CompanyDynamicFormLogic companyDynamicFormLogic;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.CompanyDynamicFormController#saveXML(java.
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
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		String responseString = "";
		try {
			
			final String decodedMetaData = URLDecoder.decode(metaData, "UTF-8");
			
			responseString = companyDynamicFormLogic.saveXML(companyId,
					decodedMetaData, tabName, formId);

		} catch (UnsupportedEncodingException| PayAsiaSystemException payAsiaSystemException) {
			LOGGER.error(payAsiaSystemException.getMessage(),
					payAsiaSystemException);
			responseString = payAsiaSystemException.getMessage();
		}
		return responseString;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.CompanyDynamicFormController#getXML(long,
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
		CompanyDynamicForm companyDynamicForm = companyDynamicFormLogic.getXML(
				companyId, formId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(companyDynamicForm,
				jsonConfig);
		return jsonObject.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.CompanyDynamicFormController#getOptionsFromXL
	 * (com.payasia.common.form.CompanyDynamicForm,
	 * org.springframework.validation.BindingResult,
	 * org.springframework.ui.ModelMap, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/getOptionsFromXL.html", method = RequestMethod.POST)
	@ResponseBody public String getOptionsFromXL(
			@ModelAttribute(value = "companyDynamicForm") CompanyDynamicForm companyDynamicForm,
			BindingResult result, ModelMap model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		CompanyDynamicForm comnyDynamicForm = new CompanyDynamicForm();

		if(companyDynamicForm.getAttachment()!=null){
			
			boolean isFileValid = FileUtils.isValidFile(companyDynamicForm.getAttachment(), companyDynamicForm.getAttachment().getOriginalFilename(), PayAsiaConstants.ALLOWED_STANDARD_UPLOAD_FILE_EXT, PayAsiaConstants.ALLOWED_STANDARD_UPLOAD_FILE_MINE_TYPE, PayAsiaConstants.FILE_SIZE);
		
			if(isFileValid){
				comnyDynamicForm = companyDynamicFormLogic
						.getOptionsFromXL(companyDynamicForm);
			}
		}
		
		return comnyDynamicForm.getOptions();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.CompanyDynamicFormController#getCodeDescFromXL
	 * (com.payasia.common.form.CompanyDynamicForm,
	 * org.springframework.validation.BindingResult,
	 * org.springframework.ui.ModelMap, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/getCodeDescFromXL.html", method = RequestMethod.POST)
	@ResponseBody public String getCodeDescFromXL(
			@ModelAttribute(value = "companyDynamicForm") CompanyDynamicForm companyDynamicForm,
			BindingResult result, ModelMap model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		List<CodeDescDTO> codeDescList = new ArrayList<CodeDescDTO>();
		if(companyDynamicForm.getAttachment() != null){
			
			boolean isFileValid =  FileUtils.isValidFile(companyDynamicForm.getAttachment(), companyDynamicForm.getAttachment().getOriginalFilename(), PayAsiaConstants.ALLOWED_GENERAL_UPLOAD_FILE_EXT, PayAsiaConstants.ALLOWED_GENERAL_UPLOAD_FILE_MINE_TYPE, PayAsiaConstants.FILE_SIZE);
			if(isFileValid){
				codeDescList = companyDynamicFormLogic
						.getCodeDescFromXL(companyDynamicForm);
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
	 * com.payasia.web.controller.CompanyDynamicFormController#deleteForm(long,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/deleteForm.html", method = RequestMethod.POST)
	@ResponseBody public String deleteForm(long formId,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		String companyDynamicFormResponse = companyDynamicFormLogic.deleteTab(
				companyId, formId);
		return companyDynamicFormResponse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.CompanyDynamicFormController#saveDynamicXML
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
		
		CompanyDynamicForm companyDynamicForm =null;
		
		try {
	
			final String decodedMetaData = URLDecoder.decode(metaData, "UTF-8");
		    companyDynamicForm = companyDynamicFormLogic.saveDynamicXML(companyId, decodedMetaData, tabName);
		} catch (UnsupportedEncodingException e) {
				LOGGER.error(e.getMessage(), e);

		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(companyDynamicForm,
				jsonConfig);
		return jsonObject.toString();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.CompanyDynamicFormController#getTabList(javax
	 * .servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/getTabList.html", method = RequestMethod.POST)
	@ResponseBody public String getTabList(HttpServletRequest request,
			HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		List<CompanyDynamicForm> tabList = companyDynamicFormLogic
				.getTabList(companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(tabList, jsonConfig);
		return jsonObject.toString();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.CompanyDynamicFormController#checkFieldEdit
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
		String response = companyDynamicFormLogic.checkFieldEdit(formId,
				fieldName, isTable, companyId, tablePosition);
		return response;

	}

}
