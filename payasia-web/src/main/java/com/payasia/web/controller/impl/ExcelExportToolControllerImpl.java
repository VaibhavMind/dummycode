/**
 * @author abhisheksachdeva
 *
 */
package com.payasia.web.controller.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.form.ExcelExportToolForm;
import com.payasia.common.form.ExcelExportToolFormResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.logic.ExcelExportToolLogic;
import com.payasia.logic.MultilingualLogic;
import com.payasia.web.controller.ExcelExportToolController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * The Class ExcelExportToolControllerImpl.
 */
@Controller
@RequestMapping(value = "/admin/excelExportTool")
public class ExcelExportToolControllerImpl implements ExcelExportToolController {

	/** The excel export tool logic. */
	@Resource
	ExcelExportToolLogic excelExportToolLogic;

	/** The multilingual logic. */
	@Resource
	MultilingualLogic multilingualLogic;

	/** The message source. */
	@Autowired
	private MessageSource messageSource;

	private static final Logger LOGGER = Logger
			.getLogger(ExcelExportToolControllerImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.ExcelExportToolController#getExistMapping(
	 * long, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, java.util.Locale)
	 */
	@Override
	@RequestMapping(value = "/existExportTableMap.html", method = RequestMethod.POST)
	public @ResponseBody
	String getExistMapping(
			@RequestParam(value = "entityId", required = true) long entityId,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long languageId = multilingualLogic.getLanguageId(locale.getLanguage());
		ExcelExportToolForm excelExportToolForm = excelExportToolLogic
				.getExistMapping(companyId, entityId, languageId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(excelExportToolForm,
				jsonConfig);
		return jsonObject.toString();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.ExcelExportToolController#getExistMapping(
	 * long, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, java.util.Locale)
	 */
	@Override
	@RequestMapping(value = "/existExportTableMapGroup.html", method = RequestMethod.POST)
	public @ResponseBody
	String getExistMappingForGroup(
			@RequestParam(value = "entityId", required = true) long entityId,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long languageId = multilingualLogic.getLanguageId(locale.getLanguage());
		ExcelExportToolForm excelExportToolForm = excelExportToolLogic
				.getExistMappingForGroup(companyId, entityId, languageId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(excelExportToolForm,
				jsonConfig);
		return jsonObject.toString();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.ExcelExportToolController#getExistTableMapping
	 * (long, long, int, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, java.util.Locale)
	 */
	@Override
	@RequestMapping(value = "/getChildTableMap.html", method = RequestMethod.POST)
	public @ResponseBody
	String getExistTableMapping(
			@RequestParam(value = "entityId", required = true) long entityId,
			@RequestParam(value = "formId", required = true) long formId,
			@RequestParam(value = "tablePosition", required = true) int tablePosition,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long languageId = multilingualLogic.getLanguageId(locale.getLanguage());
		ExcelExportToolForm excelExportToolForm = excelExportToolLogic
				.getExistTableMapping(companyId, entityId, formId,
						tablePosition, languageId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(excelExportToolForm,
				jsonConfig);
		return jsonObject.toString();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.ExcelExportToolController#getExistImportTempDef
	 * (java.lang.String, java.lang.String, java.lang.Long, java.lang.String,
	 * java.lang.String, int, int, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/excelExportTemplateMDef.html", method = RequestMethod.POST)
	public @ResponseBody
	String getExistImportTempDef(
			@RequestParam(value = "searchCondition") String searchCondition,
			@RequestParam(value = "searchText") String searchText,
			@RequestParam(value = "category") Long entityId,
			@RequestParam(value = "scope") String scope,
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
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
		ExcelExportToolFormResponse formResponse = excelExportToolLogic
				.getExistImportTempDef(pageDTO, sortDTO, companyId,
						searchCondition, searchText, entityId, scope);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(formResponse, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException unsupportedEncodingException) {
			LOGGER.error(unsupportedEncodingException.getMessage(),
					unsupportedEncodingException);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.ExcelExportToolController#saveTemplate(java
	 * .lang.String, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, java.util.Locale)
	 */
	@Override
	@RequestMapping(value = "/addTemplate.html")
	public @ResponseBody
	String saveTemplate(
			@RequestParam(value = "metaData", required = true) String metaData,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		String responseMessage = null;
		try {
			String decodeMetaData = URLDecoder.decode(metaData, "UTF-8");
			Long companyId = (Long) request.getSession().getAttribute(
					PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
			Long languageId = multilingualLogic.getLanguageId(locale.getLanguage());
			
			responseMessage = excelExportToolLogic.saveTemplate(companyId,
					decodeMetaData, languageId);

			responseMessage = URLEncoder.encode(messageSource.getMessage(
					responseMessage, new Object[] {}, locale), "UTF-8");

		} catch (UnsupportedEncodingException unsupportedEncodingException) {
			LOGGER.error(unsupportedEncodingException.getMessage(),
					unsupportedEncodingException);
		} catch (NoSuchMessageException noSuchMessageException) {
			LOGGER.error(noSuchMessageException.getMessage(),
					noSuchMessageException);
		}
		return responseMessage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.ExcelExportToolController#deleteTemplate(long)
	 */
	@Override
	@RequestMapping(value = "/deleteTemplate.html", method = RequestMethod.POST)
	public void deleteTemplate(long templateId) {
		/*ID DECRYPT*/
		templateId = FormatPreserveCryptoUtil.decrypt(templateId);
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		boolean status =	excelExportToolLogic.isAdminAuthorizedForComTemplate(templateId, companyId);
		if(status)
		excelExportToolLogic.deleteTemplate(templateId);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.ExcelExportToolController#saveEditTemplate
	 * (long, java.lang.String, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, java.util.Locale)
	 */
	@Override
	@RequestMapping(value = "/saveEditTemplate.html")
	public @ResponseBody
	String saveEditTemplate(
			@RequestParam(value = "templateId", required = true) long templateId,
			@RequestParam(value = "metaData", required = true) String metaData,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {

		/*ID DECRYPT*/
		templateId = FormatPreserveCryptoUtil.decrypt(templateId);
		
		
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long languageId = multilingualLogic.getLanguageId(locale.getLanguage());
		String responseMessage = null;

		try {
			String decodedMetaData = URLDecoder.decode(metaData, "UTF-8");
			responseMessage = excelExportToolLogic.editTemplate(templateId,
					decodedMetaData, companyId, languageId);

			responseMessage = URLEncoder.encode(messageSource.getMessage(
					responseMessage, new Object[] {}, locale), "UTF-8");

		} catch (UnsupportedEncodingException unsupportedEncodingException) {
			LOGGER.error(unsupportedEncodingException.getMessage(),
					unsupportedEncodingException);
		} catch (NoSuchMessageException noSuchMessageException) {
			LOGGER.error(noSuchMessageException.getMessage(),
					noSuchMessageException);
		}

		return responseMessage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.ExcelExportToolController#getDataForTemplate
	 * (long)
	 */
	@Override
	@RequestMapping(value = "/getDataForTemplate.html", method = RequestMethod.POST)
	public @ResponseBody
	String getDataForTemplate(
			@RequestParam(value = "templateId", required = true) long templateId) {
		
		/*ID DECRYPT*/
		templateId = FormatPreserveCryptoUtil.decrypt(templateId);
		
		ExcelExportToolForm excelExportToolForm = excelExportToolLogic
				.getDataForTemplate(templateId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(excelExportToolForm,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException unsupportedEncodingException) {
			LOGGER.error(unsupportedEncodingException.getMessage(),
					unsupportedEncodingException);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.ExcelExportToolController#deleteFilter(long)
	 */
	@Override
	@RequestMapping(value = "/deleteFilter.html", method = RequestMethod.POST)
	public void deleteFilter(
			@RequestParam(value = "filterId", required = true) long filterId) {
		
		/*ID DECRYPT*/
		filterId = FormatPreserveCryptoUtil.decrypt(filterId);
		excelExportToolLogic.deleteFilter(filterId);

	}

}
