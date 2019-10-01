/**
 * @author ragulapraveen
 * 
 */
package com.payasia.web.controller.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.form.AdminEditViewResponse;
import com.payasia.common.form.CompanyCopyForm;
import com.payasia.common.form.CompanyForm;
import com.payasia.common.form.CompanyFormResponse;
import com.payasia.common.form.EntityListViewFieldForm;
import com.payasia.common.form.EntityListViewForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.logic.CompanyInformationLogic;
import com.payasia.logic.MultilingualLogic;
import com.payasia.web.controller.CompanyInformationController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * The Class CompanyInformationControllerImpl.
 */

@Controller
@RequestMapping(value = "/admin/company")
public class CompanyInformationControllerImpl implements
		CompanyInformationController {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(CompanyInformationControllerImpl.class);

	/** The company information logic. */
	@Resource
	CompanyInformationLogic companyInformationLogic;

	/** The multilingual logic. */
	@Resource
	MultilingualLogic multilingualLogic;

	/** The message source. */
	@Autowired
	private MessageSource messageSource;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.CompanyInformationController#searchCompany
	 * (java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * int, int, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, javax.servlet.http.HttpSession)
	 */
	@Override
	@RequestMapping(value = "/companydetail.html", method = RequestMethod.POST)
	@ResponseBody public String searchCompany(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "searchCondition", required = true) String searchCondition,
			@RequestParam(value = "searchText", required = true) String searchText,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session)

	{
		Long empId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		CompanyFormResponse companyFormResponse = null;
		try {
			companyFormResponse = companyInformationLogic.getCompanies(
					searchCondition, searchText, empId, pageDTO, sortDTO);
		} catch (UnsupportedEncodingException unsupportedEncodingException) {

			LOGGER.error(unsupportedEncodingException.getMessage(),
					unsupportedEncodingException);
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(companyFormResponse,
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
	 * com.payasia.web.controller.CompanyInformationController#getCustomColumnName
	 * (java.lang.Long)
	 */
	@Override
	@RequestMapping(value = "/companyCustomColumnNames.html", method = RequestMethod.POST)
	@ResponseBody public String getCustomColumnName(Long viewID) {

		List<EntityListViewFieldForm> entityListView = companyInformationLogic
				.getCustomColumnName(viewID);

		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(entityListView, jsonConfig);
		return jsonObject.toString();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.CompanyInformationController#saveCompanyStaticData
	 * (com.payasia.common.form.CompanyForm,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, javax.servlet.http.HttpSession,
	 * java.util.Locale)
	 */
	@Override
	@RequestMapping(value = "/saveCompanyStaticData.html", method = RequestMethod.POST)
	@ResponseBody public String saveCompanyStaticData(
			@ModelAttribute("companyForm") CompanyForm companyForm,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session, Locale locale) {
		CompanyForm cmpForm = companyInformationLogic.checkCompany(companyForm);
		Long companyIdVO = null;

		if (cmpForm.getCompanyStatus().equalsIgnoreCase(
				PayAsiaConstants.NOTAVAILABLE)) {

			cmpForm.setCompanyStatus(PayAsiaConstants.NOTAVAILABLE);
			companyIdVO = companyInformationLogic.addCompany(companyForm);
			cmpForm.setEntityValue(companyIdVO);

		} else {

			cmpForm.setMessage(messageSource.getMessage(cmpForm.getMessage(),
					new Object[] {}, locale));

		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(cmpForm, jsonConfig);

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
	 * com.payasia.web.controller.CompanyInformationController#editCompany(com
	 * .payasia.common.form.CompanyForm, javax.servlet.http.HttpServletResponse,
	 * java.util.Locale, java.lang.Long)
	 */
	@Override
	@RequestMapping(value = "/editCompany.html", method = RequestMethod.POST)
	@ResponseBody public String editCompany(
			@ModelAttribute("companyForm") CompanyForm companyForm,
			HttpServletResponse response,
			Locale locale,
			@RequestParam(value = "editCompanyId", required = false) Long entityKey) {
		/*ID DECRYPT*/
		entityKey = FormatPreserveCryptoUtil.decrypt(entityKey);
		Long companyId = entityKey;
		Long languageId = multilingualLogic.getLanguageId(locale.getLanguage());
		CompanyForm companyResponse = companyInformationLogic
				.getCompanyUpdatedXmls(companyId, entityKey, languageId);
		response.setHeader("Content-Type", "text/html; charset=utf-8");

		JsonConfig jsonConfig = new JsonConfig();
		response.setCharacterEncoding("utf-8");
		JSONObject jsonObject = JSONObject.fromObject(companyResponse,
				jsonConfig);

		try {

			return URLEncoder.encode(jsonObject.toString(), "UTF8");
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
	 * com.payasia.web.controller.CompanyInformationController#loadgrid(javax
	 * .servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse,
	 * java.lang.Long, int, java.lang.String[], java.lang.String[],
	 * java.lang.String)
	 */
	@Override
	@RequestMapping(value = "/loadGrid.html", method = RequestMethod.POST)
	@ResponseBody public String loadgrid(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value = "tId", required = true) Long gridId,
			@RequestParam(value = "columnCount", required = true) int columnCount,
			@RequestParam(value = "fieldNames", required = true) String[] fieldNames,
			@RequestParam(value = "fieldTypes", required = true) String[] fieldTypes,
			@RequestParam(value = "editCompanyJqueryDateFormat", required = true) String editCompanyJqueryDateFormat) {

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		if (gridId != 0) {

			CompanyForm companyResponse = companyInformationLogic
					.tableRecordList(gridId, columnCount, fieldNames,
							fieldTypes, companyId, editCompanyJqueryDateFormat);
			JsonConfig jsonConfig = new JsonConfig();
			JSONObject jsonObject = JSONObject.fromObject(companyResponse,
					jsonConfig);
			try {
				return URLEncoder.encode(jsonObject.toString(), "UTF-8");
			} catch (UnsupportedEncodingException unsupportedEncodingException) {
				LOGGER.error(unsupportedEncodingException.getMessage(),
						unsupportedEncodingException);
			}
			return null;
		} else {
			return null;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.CompanyInformationController#updateCompany
	 * (com.payasia.common.form.CompanyForm, java.lang.String, java.lang.Long,
	 * java.lang.Long, java.lang.Integer, java.lang.Integer, java.lang.Long,
	 * java.lang.Long, java.lang.String, java.lang.String,
	 * javax.servlet.http.HttpSession, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, java.util.Locale)
	 */
	@Override
	@RequestMapping(value = "/updateCompany.html", method = RequestMethod.POST)
	@ResponseBody public String updateCompany(
			@ModelAttribute("companyForm") CompanyForm companyForm,
			@RequestParam(value = "xml", required = true) String xml,
			@RequestParam(value = "entityId", required = true) Long entityId,
			@RequestParam(value = "formId", required = true) Long formId,
			@RequestParam(value = "version", required = true) Integer version,
			@RequestParam(value = "tabNumber", required = true) Integer tabNumber,
			@RequestParam(value = "entityKey", required = true) Long entityKey,
			@RequestParam(value = "tabID", required = true) Long tabID,
			@RequestParam(value = "existingCompanyDateFormat", required = true) String existingCompanyDateFormat,
			@RequestParam(value = "companyDateFormat", required = true) String companyDateFormat,
			HttpSession session, HttpServletRequest request,
			HttpServletResponse response, Locale locale) {
		/*ID DECRYPT*/
		entityKey = FormatPreserveCryptoUtil.decrypt(entityKey);
		CompanyForm companyResponse = companyInformationLogic.checkCompany(
				companyForm, entityKey);

		try {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		if (companyResponse.getCompanyStatus().equalsIgnoreCase(
				PayAsiaConstants.NOTAVAILABLE)) {
			final String decodedXML = URLDecoder.decode(xml, "UTF-8"); 
			
			if (tabNumber == 0) {

				companyInformationLogic.updateCompany(companyForm, entityKey);
			}

			if (tabID == 0) {
				companyInformationLogic.saveCompany(decodedXML, companyId, entityId,
						formId, version, entityKey, companyDateFormat,
						existingCompanyDateFormat);
			} else {
				companyInformationLogic.updateCompanyDynamicFormRecord(decodedXML,
						companyId, entityId, formId, version, entityKey, tabID,
						companyDateFormat, existingCompanyDateFormat);
			}
		} else {
			companyResponse.setMessage(messageSource.getMessage(
					companyResponse.getMessage(), new Object[] {}, locale));

		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(companyResponse,
				jsonConfig);
	
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
	 * com.payasia.web.controller.CompanyInformationController#deleteCompany
	 * (java.lang.Long, javax.servlet.http.HttpServletRequest, java.util.Locale)
	 */
	@Override
	@RequestMapping(value = "/deleteCompany.html", method = RequestMethod.POST)
	@ResponseBody public String deleteCompany(
			@RequestParam(value = "cmpId", required = true) Long cmpId,
			HttpServletRequest request, Locale locale) {
		/*ID DECRYPT*/
		cmpId = FormatPreserveCryptoUtil.decrypt(cmpId);
		CompanyForm companyResponse = new CompanyForm();

		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		try {
			companyInformationLogic.deleteCompany(cmpId, employeeId);
			companyResponse.setDeleteMsg(messageSource.getMessage(
					"payasia.company.company.deleted.successfully",
					new Object[] {}, locale));
		} catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
			companyResponse.setDeleteMsg(messageSource.getMessage(
					"payasia.company.company.cannot.be.deleted",
					new Object[] {}, locale));
		}
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(companyResponse,
				jsonConfig);
		return jsonObject.toString();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.CompanyInformationController#copyCompany(com
	 * .payasia.common.form.CompanyCopyForm, int)
	 */
	@Override
	@RequestMapping(value = "/copyCompany.html", method = {RequestMethod.POST,RequestMethod.GET})
	@ResponseBody public String copyCompany(CompanyCopyForm companyCopyForm,
			@RequestParam(value = "cmpId", required = true) Long cmpId, HttpServletRequest request, Locale locale) {
		
		/*ID DECRYPT*/
		cmpId = FormatPreserveCryptoUtil.decrypt(cmpId);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		CompanyForm companyForm = new CompanyForm();
		companyForm.setCompanyCode(companyCopyForm.getCopyCompanyCode());
		companyForm.setCompanyName(companyCopyForm.getCopyCompanyName());

		CompanyForm cmpFormResponse = companyInformationLogic
				.checkCompany(companyForm);

		if (cmpFormResponse.getCompanyStatus().equalsIgnoreCase(
				PayAsiaConstants.NOTAVAILABLE)) {

			cmpFormResponse.setCompanyStatus(PayAsiaConstants.NOTAVAILABLE);
			companyInformationLogic.copyCompany(companyCopyForm, cmpId.intValue(),
					employeeId);

		} else {

			cmpFormResponse.setMessage(messageSource.getMessage(
					cmpFormResponse.getMessage(), new Object[] {}, locale));

		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(cmpFormResponse,
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
	 * com.payasia.web.controller.CompanyInformationController#companyGridView
	 * (java.lang.Long, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/companyGirdView.html", method = RequestMethod.POST)
	@ResponseBody public String companyGridView(
			@RequestParam(value = "viewId", required = true) Long viewId,
			HttpServletRequest request, HttpServletResponse res) {
		AdminEditViewResponse response = new AdminEditViewResponse();
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		List<EntityListViewFieldForm> list = companyInformationLogic.editView(
				companyId, viewId);

		int totalRecords;
		totalRecords = list.size();
		response.setRows(list);

		int recordSize = totalRecords;
		int pageSize = 10;
		int totalPages = recordSize / pageSize;
		if (recordSize % pageSize != 0) {
			totalPages = totalPages + 1;
		}
		if (recordSize == 0) {
			totalPages = 0;
		}

		response.setRecords(String.valueOf(recordSize));
		response.setPage("1");
		response.setTotal(String.valueOf(totalPages));

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(response, jsonConfig);
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
	 * @see com.payasia.web.controller.CompanyInformationController#
	 * selectedCompanyGridView(java.lang.Long,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/selectedCompanyGridView.html", method = RequestMethod.POST)
	@ResponseBody public String selectedCompanyGridView(
			@RequestParam(value = "view", required = true) Long viewId,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		UserContext.setLocale(locale);
		AdminEditViewResponse adminEditViewResponse = new AdminEditViewResponse();
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		List<EntityListViewFieldForm> list = companyInformationLogic
				.listEditView(companyId, viewId);

		int totalRecords;
		totalRecords = list.size();
		adminEditViewResponse.setRows(list);

		int recordSize = totalRecords;
		int pageSize = 10;
		int totalPages = recordSize / pageSize;
		if (recordSize % pageSize != 0) {
			totalPages = totalPages + 1;
		}
		if (recordSize == 0) {
			totalPages = 0;
		}

		adminEditViewResponse.setRecords(String.valueOf(recordSize));
		adminEditViewResponse.setPage("1");
		adminEditViewResponse.setTotal(String.valueOf(totalPages));

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(adminEditViewResponse,
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
	 * com.payasia.web.controller.CompanyInformationController#saveCompanyGridView
	 * (java.lang.String, int, java.lang.String[], java.lang.String[],
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/saveCompanyGridView.html", method = RequestMethod.POST)
	@ResponseBody public String saveCompanyGridView(
			@RequestParam(value = "viewName", required = true) String viewName,
			@RequestParam(value = "recordsPerPage", required = true) int recordsPerPage,
			@RequestParam(value = "dataDictionaryId", required = true) String[] dataDictionaryIdArr,
			@RequestParam(value = "rowIndexs", required = true) String[] rowIndexsArr,
			HttpServletRequest request, HttpServletResponse response) {

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		CompanyForm companyResponse = companyInformationLogic.checkView(
				viewName, companyId);

		if (companyResponse.getStatus().equalsIgnoreCase(
				PayAsiaConstants.NOTAVAILABLE)) {

			companyInformationLogic.saveCustomView(companyId, viewName,
					recordsPerPage, dataDictionaryIdArr, rowIndexsArr);
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(companyResponse,
				jsonConfig);
		return jsonObject.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.CompanyInformationController#updateCompanyGridView
	 * (java.lang.String, int, java.lang.String[], java.lang.String[],
	 * java.lang.Long, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/updateCompanyGridView.html", method = RequestMethod.POST)
	@ResponseBody public String updateCompanyGridView(
			@RequestParam(value = "viewName", required = true) String viewName,
			@RequestParam(value = "recordsPerPage", required = true) int recordsPerPage,
			@RequestParam(value = "dataDictionaryId", required = true) String[] dataDictionaryIdArr,
			@RequestParam(value = "rowIndexs", required = true) String[] rowIndexsArr,
			@RequestParam(value = "viewId", required = true) Long viewId,
			HttpServletRequest request, HttpServletResponse response) {

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		CompanyForm companyResponse = companyInformationLogic.checkViewUpdate(
				viewName, companyId, viewId);

		if (companyResponse.getStatus().equalsIgnoreCase(
				PayAsiaConstants.NOTAVAILABLE)) {

			companyInformationLogic.updateCustomView(companyId, viewName,
					recordsPerPage, dataDictionaryIdArr, rowIndexsArr, viewId);
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(companyResponse,
				jsonConfig);
		return jsonObject.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.CompanyInformationController#
	 * getCompanyGridViewList(javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/companyGridViewList.html", method = RequestMethod.POST)
	@ResponseBody public String getCompanyGridViewList(
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		List<EntityListViewForm> viewNameList = companyInformationLogic
				.getViewName(companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(viewNameList, jsonConfig);
		return jsonObject.toString();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.CompanyInformationController#deleteCompanyGridView
	 * (java.lang.Long)
	 */
	@Override
	@RequestMapping(value = "/deleteCompanyGridView.html", method = RequestMethod.POST)
	@ResponseBody public String deleteCompanyGridView(
			@RequestParam(value = "viewId", required = true) Long viewId) {
		AdminEditViewResponse response = new AdminEditViewResponse();
		companyInformationLogic.deleteView(viewId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(response, jsonConfig);
		return jsonObject.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.CompanyInformationController#
	 * updateCompanyStaticData(com.payasia.common.form.CompanyForm,
	 * java.lang.Long, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, javax.servlet.http.HttpSession,
	 * java.util.Locale)
	 */
	@Override
	@RequestMapping(value = "/updateCompanyStaticData.html", method = RequestMethod.POST)
	@ResponseBody public String updateCompanyStaticData(
			@ModelAttribute("companyForm") CompanyForm companyForm,
			@RequestParam(value = "entityKey", required = true) Long entityKey,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session, Locale locale) {
		CompanyForm companyResponse = companyInformationLogic.checkCompany(
				companyForm, entityKey);

		if (companyResponse.getCompanyStatus().equalsIgnoreCase(
				PayAsiaConstants.NOTAVAILABLE)) {
			companyInformationLogic.updateCompany(companyForm, entityKey);

		} else {
			companyResponse.setMessage(messageSource.getMessage(
					companyResponse.getMessage(), new Object[] {}, locale));

		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(companyResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException unsupportedEncodingException) {
			LOGGER.error(unsupportedEncodingException.getMessage(),
					unsupportedEncodingException);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/saveCompanyTableRecord.html", method = RequestMethod.POST)
	@ResponseBody public String saveCompanyTableRecord(
			@RequestParam(value = "tableXML", required = true) String tableXML,
			@RequestParam(value = "tabId", required = true) Long tabId,
			@RequestParam(value = "formId", required = true) Long formId,
			@RequestParam(value = "version", required = true) Integer version,
			@RequestParam(value = "entityKey", required = true) Long entityKey,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		CompanyForm companyForm = new CompanyForm();
		try {
			companyForm = companyInformationLogic.saveCompanyTableRecord(
					tableXML, tabId, companyId, formId, version, entityKey);
			companyForm.setStatus(PayAsiaConstants.PAYASIA_SUCCESS);

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			companyForm.setStatus(PayAsiaConstants.PAYASIA_ERROR);

		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(companyForm, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/updateCompanyTableRecord.html", method = RequestMethod.POST)
	@ResponseBody public String updateCompanyTableRecord(
			@RequestParam(value = "tableXML", required = true) String tableXML,
			@RequestParam(value = "tabId", required = true) Long tabId,
			@RequestParam(value = "seqNo", required = true) Integer seqNo,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		CompanyForm companyForm = new CompanyForm();
		try {
			companyForm = companyInformationLogic.updateCompanyTableRecord(
					tableXML, tabId, companyId, seqNo);

			companyForm.setStatus(PayAsiaConstants.PAYASIA_SUCCESS);

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			companyForm.setStatus(PayAsiaConstants.PAYASIA_ERROR);

		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(companyForm, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/deleteCompanyTableRecord.html", method = RequestMethod.POST)
	@ResponseBody public String deleteCompanyTableRecord(
			@RequestParam(value = "tableId", required = true) Long tableId,
			@RequestParam(value = "seqNo", required = true) Integer seqNo,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		CompanyForm companyForm = new CompanyForm();
		try {
			companyForm = companyInformationLogic.deleteCompanyTableRecord(
					tableId, companyId, seqNo);

			companyForm.setStatus(PayAsiaConstants.PAYASIA_SUCCESS);

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			companyForm.setStatus(PayAsiaConstants.PAYASIA_ERROR);

		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(companyForm, jsonConfig);
		return jsonObject.toString();
	}

}
