/**
 * @author abhisheksachdeva
 *
 */
package com.payasia.web.controller.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Locale;

import javax.annotation.Resource;
import javax.mail.internet.MimeUtility;
import javax.servlet.ServletOutputStream;
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

import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.ExcelImportToolForm;
import com.payasia.common.form.ExcelImportToolFormResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.logic.ExcelImportToolLogic;
import com.payasia.logic.MultilingualLogic;
import com.payasia.web.controller.ExcelImportToolController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * The Class ExcelImportToolControllerImpl.
 */
@Controller
@RequestMapping(value = "/admin/excelImportTool")
public class ExcelImportToolControllerImpl implements ExcelImportToolController {

	/** The excel import tool logic. */
	@Resource
	ExcelImportToolLogic excelImportToolLogic;

	/** The multilingual logic. */
	@Resource
	MultilingualLogic multilingualLogic;

	/** The message source. */
	@Autowired
	private MessageSource messageSource;

	private static final Logger LOGGER = Logger
			.getLogger(ExcelImportToolControllerImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.ExcelImportToolController#getExistMapping(
	 * long, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, java.util.Locale)
	 */
	@Override
	@RequestMapping(value = "/existTableMap.html", method = RequestMethod.POST)
	@ResponseBody public String getExistMapping(
			@RequestParam(value = "entityId", required = true) long entityId,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long languageId = multilingualLogic.getLanguageId(locale.getLanguage());
		ExcelImportToolForm excelImportToolForm = excelImportToolLogic
				.getExistMapping(companyId, entityId, languageId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(excelImportToolForm,
				jsonConfig);
		return jsonObject.toString();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.ExcelImportToolController#getExistTableMapping
	 * (long, long, int, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, java.util.Locale)
	 */
	@Override
	@RequestMapping(value = "/getChildTableMap.html", method = RequestMethod.POST)
	@ResponseBody public String getExistTableMapping(
			@RequestParam(value = "entityId", required = true) long entityId,
			@RequestParam(value = "formId", required = true) long formId,
			@RequestParam(value = "tablePosition", required = true) int tablePosition,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long languageId = multilingualLogic.getLanguageId(locale.getLanguage());
		ExcelImportToolForm excelImportToolForm = excelImportToolLogic
				.getExistTableMapping(companyId, entityId, formId,
						tablePosition, languageId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(excelImportToolForm,
				jsonConfig);
		return jsonObject.toString();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.ExcelImportToolController#getExistImportTempDef
	 * (java.lang.String, java.lang.String, java.lang.Long, java.lang.String,
	 * java.lang.String, int, int, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/excelImportTemplateMDef.html", method = RequestMethod.POST)
	@ResponseBody public String getExistImportTempDef(
			@RequestParam(value = "searchCondition") String searchCondition,
			@RequestParam(value = "searchText") String searchText,
			@RequestParam(value = "category") Long entityId,
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
		ExcelImportToolFormResponse formResponse = excelImportToolLogic
				.getExistImportTempDef(pageDTO, sortDTO, companyId,
						searchCondition, searchText, entityId);

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
	 * com.payasia.web.controller.ExcelImportToolController#saveTemplate(java
	 * .lang.String, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, java.util.Locale)
	 */
	@Override
	@RequestMapping(value = "/addTemplateMDef.html", method = RequestMethod.POST)
	@ResponseBody public String saveTemplate(
			@RequestParam(value = "metaData", required = true) String metaData,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		String responseString = null;
		try {
			
			String decodeMetaData = URLDecoder.decode(metaData, "UTF-8");
			Long companyId = (Long) request.getSession().getAttribute(
					PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
			Long languageId = multilingualLogic.getLanguageId(locale.getLanguage());
			

			responseString = excelImportToolLogic.saveTemplate(companyId,
					decodeMetaData, languageId);

			responseString = URLEncoder.encode(messageSource.getMessage(
					responseString, new Object[] {}, locale), "UTF-8");

		} catch (UnsupportedEncodingException unsupportedEncodingException) {
			LOGGER.error(unsupportedEncodingException.getMessage(),
					unsupportedEncodingException);
		} catch (NoSuchMessageException noSuchMessageException) {
			LOGGER.error(noSuchMessageException.getMessage(),
					noSuchMessageException);
		}

		return responseString;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.ExcelImportToolController#deleteTemplate(long)
	 */
	@Override
	@RequestMapping(value = "/deleteTemplateMDef.html", method = RequestMethod.POST)
	public void deleteTemplate(
			@RequestParam(value = "templateId", required = true) long templateId) {

		/*ID DECRYPT*/
		templateId = FormatPreserveCryptoUtil.decrypt(templateId);
		
		excelImportToolLogic.deleteTemplate(templateId);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.ExcelImportToolController#editTemplate(java
	 * .lang.String, long, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, java.util.Locale)
	 */
	@Override
	@RequestMapping(value = "/editSaveTemplateMDef.html")
	@ResponseBody public String editTemplate(
			@RequestParam(value = "metaData", required = true) String metaData,
			@RequestParam(value = "templateId", required = true) long templateId,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		
		/*ID DECRYPT*/
		templateId = FormatPreserveCryptoUtil.decrypt(templateId);
		
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long languageId = multilingualLogic.getLanguageId(locale.getLanguage());

		String responseString = null;

		try {
            String decodemetadata=URLDecoder.decode(metaData, "UTF-8");
			responseString = excelImportToolLogic.editTemplate(companyId,
					decodemetadata, templateId, languageId);

			responseString = URLEncoder.encode(messageSource.getMessage(
					responseString, new Object[] {}, locale), "UTF-8");

		} catch (UnsupportedEncodingException unsupportedEncodingException) {
			LOGGER.error(unsupportedEncodingException.getMessage(),
					unsupportedEncodingException);
		} catch (NoSuchMessageException noSuchMessageException) {
			LOGGER.error(noSuchMessageException.getMessage(),
					noSuchMessageException);
		}

		return responseString;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.ExcelImportToolController#getDataForTemplate
	 * (long)
	 */
	@Override
	@RequestMapping(value = "/getDataForTemplate.html", method = RequestMethod.POST)
	@ResponseBody public String getDataForTemplate(
			@RequestParam(value = "templateId", required = true) long templateId) {

		/*ID DECRYPT*/
		templateId = FormatPreserveCryptoUtil.decrypt(templateId);
		
		ExcelImportToolForm excelImportToolForm = excelImportToolLogic
				.getDataForTemplate(templateId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(excelImportToolForm,
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
	 * com.payasia.web.controller.ExcelImportToolController#generateExcel(long,
	 * javax.servlet.http.HttpServletResponse,
	 * javax.servlet.http.HttpServletRequest)
	 */
	@Override
	@RequestMapping(value = "/generateExcel", method = RequestMethod.GET)
	public void generateExcel(long templateId, HttpServletResponse response,
			HttpServletRequest request) throws IOException {

		ServletOutputStream outputStream = null;
		try {
			
			/*ID DECRYPT*/
			templateId = FormatPreserveCryptoUtil.decrypt(templateId);

			ExcelImportToolForm importToolForm = excelImportToolLogic
					.generateExcel(templateId);
			String fileName = importToolForm.getTemplateName();
			outputStream = response.getOutputStream();
			response.setContentType("application/octet-stream");
			response.setHeader("Cache-Control",
					"must-revalidate, post-check=0, pre-check=0");

			fileName = fileName + ".xls";
			String user_agent = request.getHeader("user-agent");
			boolean isInternetExplorer = (user_agent.indexOf("MSIE") > -1);
			if (isInternetExplorer) {
				response.setHeader(
						"Content-disposition",
						"attachment; filename=\""
								+ URLEncoder.encode(fileName, "utf-8")
										.replaceAll("\\+", " ") + "\"");
			} else {
				response.setHeader(
						"Content-disposition",
						"attachment; filename=\""
								+ MimeUtility
										.encodeWord(fileName, "utf-8", "Q")
								+ "\"");
			}

			outputStream = response.getOutputStream();
			importToolForm.getWorkbook().write(outputStream);
		} catch (IOException ioException) {
			LOGGER.error(ioException.getMessage(), ioException);
			throw new PayAsiaSystemException(ioException.getMessage(),
					ioException);

		} finally {
			try {

				if (outputStream != null) {
					outputStream.flush();
					outputStream.close();
				}

			} catch (IOException ioException) {
				LOGGER.error(ioException.getMessage(), ioException);
				throw new PayAsiaSystemException(ioException.getMessage(),
						ioException);
			}
		}

	}
}
