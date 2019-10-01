/**

 * @author abhisheksachdeva
 * 
 */
package com.payasia.web.controller.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;

import javax.annotation.Resource;
import javax.mail.internet.MimeUtility;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.form.DataExportForm;
import com.payasia.common.form.DataExportFormResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.logic.DataExportLogic;
import com.payasia.logic.MultilingualLogic;
import com.payasia.web.controller.DataExportController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * The Class DataExportControllerImpl.
 */
@Controller
@RequestMapping(value = "/admin/dataExport")
public class DataExportControllerImpl implements DataExportController {

	/** The data export logic. */
	@Resource
	DataExportLogic dataExportLogic;

	/** The multilingual logic. */
	@Resource
	MultilingualLogic multilingualLogic;

	private static final Logger LOGGER = Logger
			.getLogger(DataExportControllerImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.DataExportController#getExportTemplates(java
	 * .lang.String, java.lang.String, java.lang.Long, java.lang.String,
	 * java.lang.String, int, int, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/getExportTemplates.html", method = RequestMethod.POST)
	@ResponseBody public String getExportTemplates(
			@RequestParam(value = "searchCondition", required = true) String searchCondition,
			@RequestParam(value = "searchText", required = true) String searchText,
			@RequestParam(value = "category", required = true) Long entityId,
			@RequestParam(value = "scope", required = true) String scope,
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
		DataExportFormResponse formResponse = dataExportLogic
				.getExportTemplates(pageDTO, sortDTO, companyId,
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
	 * com.payasia.web.controller.DataExportController#generateExcel(com.payasia
	 * .common.form.DataExportForm, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, java.util.Locale)
	 */
	@Override
	@RequestMapping(value = "/generateExcel", method = RequestMethod.POST)
	public void generateExcel(
			@ModelAttribute(value = "dataExportForm") DataExportForm dataExportForm,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) throws IOException {

		Long languageId = multilingualLogic.getLanguageId(locale.getLanguage());
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		DataExportForm exportForm;
		String scope = dataExportForm.getScope();
		
		/*ID DECRYPT*/
		dataExportForm.setTemplateId(FormatPreserveCryptoUtil.decrypt(dataExportForm.getTemplateId()));
		
		if (scope.equalsIgnoreCase(PayAsiaConstants.PAYASIA_SCOPE_GROUP
				.toLowerCase())) {

			exportForm = dataExportLogic.generateExcelGroup(
					dataExportForm.getTemplateId(), companyId, employeeId,
					dataExportForm.getExcelExportFilterList(), languageId,
					dataExportForm.getSelectedComapnyIds());

		} else {
			exportForm = dataExportLogic.generateExcel(
					dataExportForm.getTemplateId(), companyId, employeeId,
					dataExportForm.getExcelExportFilterList(), languageId);

		}

		Workbook excelFile = exportForm.getWorkbook();

		String fileName = exportForm.getFinalFileName() + ".xls";
		ServletOutputStream outputStream = response.getOutputStream();
		response.setContentType("application/octet-stream");
		response.setHeader("Cache-Control",
				"must-revalidate, post-check=0, pre-check=0");

		String user_agent = request.getHeader("user-agent");
		boolean isInternetExplorer = (user_agent.indexOf("MSIE") > -1);
		if (isInternetExplorer) {
			response.setHeader(
					"Content-disposition",
					"attachment; filename=\""
							+ URLEncoder.encode(fileName, "utf-8").replaceAll(
									"\\+", " ") + "\"");
		} else {
			response.setHeader("Content-disposition", "attachment; filename=\""
					+ MimeUtility.encodeWord(fileName, "utf-8", "Q") + "\"");
		}

		excelFile.write(outputStream);
		outputStream.flush();
		outputStream.close();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.DataExportController#generateExcel(com.payasia
	 * .common.form.DataExportForm, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, java.util.Locale)
	 */
	@Override
	@RequestMapping(value = "/generateExcelGroup", method = RequestMethod.POST)
	public void generateExcelGroup(
			@ModelAttribute(value = "dataExportForm") DataExportForm dataExportForm,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) throws IOException {

		Long languageId = multilingualLogic.getLanguageId(locale.getLanguage());
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		
		/*ID DECRYPT*/
		DataExportForm exportForm = dataExportLogic.generateExcelGroup(
				FormatPreserveCryptoUtil.decrypt(dataExportForm.getTemplateId()), companyId, employeeId,
				dataExportForm.getExcelExportFilterList(), languageId,
				dataExportForm.getSelectedComapnyIds());
		Workbook excelFile = exportForm.getWorkbook();

		String fileName = exportForm.getFinalFileName() + ".xls";
		ServletOutputStream outputStream = response.getOutputStream();
		response.setContentType("application/octet-stream");
		response.setHeader("Cache-Control",
				"must-revalidate, post-check=0, pre-check=0");

		String user_agent = request.getHeader("user-agent");
		boolean isInternetExplorer = (user_agent.indexOf("MSIE") > -1);
		if (isInternetExplorer) {
			response.setHeader(
					"Content-disposition",
					"attachment; filename=\""
							+ URLEncoder.encode(fileName, "utf-8").replaceAll(
									"\\+", " ") + "\"");
		} else {
			response.setHeader("Content-disposition", "attachment; filename=\""
					+ MimeUtility.encodeWord(fileName, "utf-8", "Q") + "\"");
		}

		excelFile.write(outputStream);
		outputStream.flush();
		outputStream.close();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.DataExportController#getFiltersForTemplate
	 * (long, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, org.springframework.ui.ModelMap,
	 * java.util.Locale)
	 */
	@Override
	@RequestMapping(value = "/getFiltersForTemplate.html", method = RequestMethod.POST)
	@ResponseBody public String getFiltersForTemplate(long templateId,
			HttpServletRequest request, HttpServletResponse response,
			ModelMap model, Locale locale) {
		
		/*ID DECRYPT*/
		templateId = FormatPreserveCryptoUtil.decrypt(templateId);
		
		DataExportForm dataExportForm =null;
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
	    boolean status =	dataExportLogic.isAdminAuthorizedForComTemplate(templateId, companyId);
		if(status)
		{
		Long languageId = multilingualLogic.getLanguageId(locale.getLanguage());
		 dataExportForm = dataExportLogic.getDataForTemplate(
				templateId, languageId, employeeId, "edit");
		}
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(
				dataExportForm.getExcelExportFilterList(), jsonConfig);
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
	 * com.payasia.web.controller.DataExportController#getFiltersForTemplate
	 * (long, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, org.springframework.ui.ModelMap,
	 * java.util.Locale)
	 */
	@Override
	@RequestMapping(value = "/getFiltersForTemplateGroup.html", method = RequestMethod.POST)
	@ResponseBody public String getFiltersForTemplateGroup(long templateId,
			HttpServletRequest request, HttpServletResponse response,
			ModelMap model, Locale locale) {
		
		/*ID DECRYPT*/
		templateId = FormatPreserveCryptoUtil.decrypt(templateId);
		
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		Long languageId = multilingualLogic.getLanguageId(locale.getLanguage());
		DataExportForm dataExportForm = dataExportLogic
				.getDataForTemplateGroup(templateId, languageId, null,
						employeeId, "edit");

		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(
				dataExportForm.getExcelExportFilterList(), jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException unsupportedEncodingException) {
			LOGGER.error(unsupportedEncodingException.getMessage(),
					unsupportedEncodingException);
		}
		return null;
	}

}
