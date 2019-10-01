package com.payasia.web.controller.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.AdminEditViewResponse;
import com.payasia.common.form.DynamicFormTableDocumentDTO;
import com.payasia.common.form.EmployeeListForm;
import com.payasia.common.form.EmployeeListFormPage;
import com.payasia.common.form.EntityListViewFieldForm;
import com.payasia.common.form.EntityListViewForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.PasswordPolicyPreferenceForm;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FileUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.logic.EmployeeDetailLogic;
import com.payasia.logic.MultilingualLogic;
import com.payasia.web.controller.EmployeeDetailController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * The Class EmployeeDetailControllerImpl.
 * 
 * @author ragulapraveen
 */
@Controller
// @RequestMapping(value = { "/employee/employeeDetail", "/admin/employeeDetail"
// })
public class EmployeeDetailControllerImpl implements EmployeeDetailController {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger.getLogger(EmployeeDetailControllerImpl.class);

	/** The employee detail logic. */
	@Resource
	EmployeeDetailLogic employeeDetailLogic;

	/** The multilingual logic. */
	@Resource
	MultilingualLogic multilingualLogic;

	/** The message source. */
	@Autowired
	private MessageSource messageSource;

	/** The employee image width. */
	@Value("#{payasiaptProperties['payasia.employee.image.width']}")
	private Integer employeeImageWidth;

	/** The employee image height. */
	@Value("#{payasiaptProperties['payasia.employee.image.height']}")
	private Integer employeeImageHeight;
	

	@Autowired
	private ServletContext servletContext;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.EmployeeDetailController#filterEmployeeList
	 * (java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * int, int, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, javax.servlet.http.HttpSession,
	 * java.util.Locale)
	 */
	@Override
	@RequestMapping(value = "/admin/employeeDetail/searchEmployee.html", method = RequestMethod.POST)
	@ResponseBody
	public String filterEmployeeList(@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "searchCondition", required = true) String searchCondition,
			@RequestParam(value = "searchText", required = true) String searchText,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows, HttpServletRequest request,
			HttpServletResponse response, HttpSession session, Locale locale) {

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		Long languageId = multilingualLogic.getLanguageId(locale.toString());

		EmployeeListFormPage employeeListFormPageRes = null;
		try {
			employeeListFormPageRes = employeeDetailLogic.getEmployeeList(searchCondition, searchText, pageDTO, sortDTO,
					companyId, employeeId, languageId);
		} catch (UnsupportedEncodingException unsupportedEncodingException) {
			LOGGER.error(unsupportedEncodingException.getMessage(), unsupportedEncodingException);
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(employeeListFormPageRes, jsonConfig);
		return jsonObject.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.EmployeeDetailController#searchEmployeePwd
	 * (java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * int, int, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, javax.servlet.http.HttpSession)
	 */
	@Override
	@RequestMapping(value = "/admin/employeeDetail/searchEmployeePwd.html", method = RequestMethod.POST)
	@ResponseBody
	public String searchEmployeePwd(@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "fromDate", required = true) String fromDate,
			@RequestParam(value = "toDate", required = true) String toDate,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows, HttpServletRequest request,
			HttpServletResponse response, HttpSession session) {
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		EmployeeListFormPage employeeListFormPageRes = employeeDetailLogic.getEmployeeListPwd(companyId, fromDate,
				toDate, pageDTO, sortDTO);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(employeeListFormPageRes, jsonConfig);
		return jsonObject.toString();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.EmployeeDetailController#deleteEmployee(java
	 * .lang.Long)
	 */
	@Override
	@RequestMapping(value = "/admin/employeeDetail/deleteEmployee.html", method = RequestMethod.POST)
	@ResponseBody
	public String deleteEmployee(@RequestParam(value = "empID", required = true) Long empID, HttpServletRequest request,
			HttpServletResponse response) {

		/*ID DECRYPT*/

		empID = FormatPreserveCryptoUtil.decrypt(empID);
		EmployeeListFormPage employeeListFormPage = new EmployeeListFormPage();

		boolean employeeDeleted = false;

		try {
			Long userId = Long.parseLong(UserContext.getUserId());
			Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
			employeeDeleted = employeeDetailLogic.isAdminAuthorizedForEmployee(empID, companyId, userId);
			if (employeeDeleted) {

				employeeDeleted = employeeDetailLogic.deleteEmployee(empID);
			}
			employeeListFormPage.setEmployeeDeleted(String.valueOf(employeeDeleted));

		} catch (PayAsiaSystemException payAsiaSystemException) {
			LOGGER.error(payAsiaSystemException.getMessage(), payAsiaSystemException);
			employeeListFormPage.setEmployeeDeleted(payAsiaSystemException.getKey());
		}

		employeeListFormPage.setEmpId(String.valueOf(empID));

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(employeeListFormPage, jsonConfig);
		return jsonObject.toString();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.EmployeeDetailController#deleteRoleAndEmployee
	 * (java.lang.Long)
	 */
	@Override
	@RequestMapping(value = "/admin/employeeDetail/deleteRoleAndEmployee.html", method = RequestMethod.POST)
	@ResponseBody
	public String deleteRoleAndEmployee(@RequestParam(value = "empID", required = true) Long empID) {
		EmployeeListFormPage employeeListFormPage = new EmployeeListFormPage();
		try {
			Long userId = Long.parseLong(UserContext.getUserId());
			Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
			boolean employeeDeleted = employeeDetailLogic.isAdminAuthorizedForEmployee(empID, companyId, userId);
			employeeListFormPage.setEmployeeDeleted(String.valueOf(employeeDeleted));
			employeeListFormPage.setEmpId(String.valueOf(empID));
			if(employeeDeleted) {
			    employeeDetailLogic.deleteRoleAndEmployee(empID);
			}else{  
				LOGGER.error(PayAsiaConstants.PAYASIA_UNAUTHORIZED_ACCESS);
			}
		 }catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(employeeListFormPage, jsonConfig);
		return jsonObject.toString();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.EmployeeDetailController#editEmployee(com.
	 * payasia.common.form.EmployeeListForm,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, javax.servlet.http.HttpSession,
	 * java.util.Locale, java.lang.Long)
	 */
	@Override
	@RequestMapping(value = "/admin/employeeDetail/editEmployee.html", method = RequestMethod.POST)
	@ResponseBody
	public String editEmployee(

			@ModelAttribute("employeeListForm") EmployeeListForm employeeListForm,

			HttpServletRequest request, HttpServletResponse response, HttpSession session, Locale locale,
			@RequestParam(value = "empID", required = false) Long empID,
			@RequestParam(value = "mode", required = false) String mode) {

		/*ID DECRYPT*/
		empID = FormatPreserveCryptoUtil.decrypt(empID);
		Long languageId = multilingualLogic.getLanguageId(locale.toString());
		Long loggedInEmployeeId = (Long) request.getSession()
				.getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		EmployeeListFormPage employeeListFormPage = null;
		boolean employeeUpdated = false;
		Long userId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		employeeUpdated = employeeDetailLogic.isAdminAuthorizedForEmployee(empID, companyId, userId);
		if (employeeUpdated) {
			employeeListFormPage = employeeDetailLogic.getUpdatedXmls(loggedInEmployeeId, empID, companyId, languageId,
					mode);
			employeeListFormPage.setUniqueEmployeeId(empID);

			int maxFileSize = employeeDetailLogic.getEmployeeImageSize();
			employeeListFormPage.setMaxFileSize(maxFileSize);
		}
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(employeeListFormPage, jsonConfig);
		return jsonObject.toString();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.EmployeeDetailController#editEmployee(com.
	 * payasia.common.form.EmployeeListForm,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, javax.servlet.http.HttpSession,
	 * java.util.Locale, java.lang.Long)
	 */
	@Override
	@RequestMapping(value = "/admin/employeeDetail/viewProfile.html", method = RequestMethod.POST)
	@ResponseBody
	public String editHomePageEmployee(@ModelAttribute("employeeListForm") EmployeeListForm employeeListForm,
			HttpServletRequest request, HttpServletResponse response, HttpSession session, Locale locale,
			@RequestParam(value = "mode", required = false) String mode) {
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		Long languageId = multilingualLogic.getLanguageId(locale.toString());

		EmployeeListFormPage employeeListFormPage = employeeDetailLogic.getViewProfileXMLS(employeeId, companyId,
				languageId, mode);
		employeeListFormPage.setUniqueEmployeeId(employeeId);

		int maxFileSize = employeeDetailLogic.getEmployeeImageSize();
		employeeListFormPage.setMaxFileSize(maxFileSize);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(employeeListFormPage, jsonConfig);
		return jsonObject.toString();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.EmployeeDetailController#getXMLData(javax.
	 * servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse,
	 * javax.servlet.http.HttpSession, java.util.Locale)
	 */
	@Override
	@RequestMapping(value = "/admin/employeeDetail/addEmployeeXMLData.html", method = RequestMethod.POST)
	@ResponseBody
	public String getXMLData(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			Locale locale) {

		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		Long languageId = multilingualLogic.getLanguageId(locale.toString());

		EmployeeListFormPage employeeListFormPage = employeeDetailLogic.getUploadedDoc(companyId, languageId);
		int maxFileSize = employeeDetailLogic.getEmployeeImageSize();
		employeeListFormPage.setMaxFileSize(maxFileSize);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(employeeListFormPage, jsonConfig);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/admin/employeeDetail/saveAddEmployee.html", method = RequestMethod.POST)
	@ResponseBody
	public String saveAddEmployee(@ModelAttribute("employeeListForm") EmployeeListForm employeeListForm,
			@RequestParam(value = "xml", required = true) String xml,
			@RequestParam(value = "companyId", required = true) Long companyId,
			@RequestParam(value = "entityId", required = true) Long entityId,
			@RequestParam(value = "formId", required = true) Long formId,
			@RequestParam(value = "version", required = true) Integer version,
			@RequestParam(value = "tabNumber", required = true) Integer tabNumber,
			@RequestParam(value = "employeeID", required = true) String employeeID, Locale locale) {
		EmployeeListFormPage employeeListFormPage = new EmployeeListFormPage();
		Long empid = null;
		try {

			final String decodedXml = URLDecoder.decode(xml, "UTF-8");
			
			employeeListFormPage = employeeDetailLogic.checkEmployeeNumberEmail(employeeListForm.getEmployeeNumber(),
					companyId, employeeListForm.getEmail());
			if (tabNumber == 0) {

				if (employeeListFormPage.getEmployeeNumberStatus().equalsIgnoreCase(PayAsiaConstants.NOTAVAILABLE)) {
					empid = employeeDetailLogic.addEmployee(employeeListForm, companyId);

					employeeListFormPage = employeeDetailLogic.saveAddEmployee(decodedXml, companyId, entityId, formId,
							version, empid);
					employeeListFormPage.setUniqueEmployeeId(empid);

				} else {

					employeeListFormPage.setMessage(URLEncoder.encode(
							messageSource.getMessage(employeeListFormPage.getMessage(), new Object[] {}, locale),
							"UTF-8"));

				}

			} else {
				employeeListFormPage.setEmployeeNumberStatus(PayAsiaConstants.NOTAVAILABLE);
				empid = Long.parseLong(employeeID);
				employeeListFormPage = employeeDetailLogic.saveAddEmployee(decodedXml, companyId, entityId, formId, version,
						empid);
			}

		} catch (UnsupportedEncodingException unsupportedEncodingException) {
			LOGGER.error(unsupportedEncodingException.getMessage(), unsupportedEncodingException);
		} catch (NoSuchMessageException noSuchMessageException) {
			LOGGER.error(noSuchMessageException.getMessage(), noSuchMessageException);
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(employeeListFormPage, jsonConfig);
		return jsonObject.toString();

	}

	/*
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.EmployeeDetailController#
	 * saveEmployeeStaticAndDynamicData
	 * (com.payasia.common.form.EmployeeListForm, java.lang.String,
	 * java.lang.Long, java.lang.Long, java.lang.Long, java.lang.Integer,
	 * java.lang.Integer, java.lang.String, java.util.Locale)
	 */
	@Override
	// TODO unused method
	// @RequestMapping(value = "/saveEmployee.html", method =
	// RequestMethod.POST)
	@ResponseBody
	public String saveEmployeeStaticAndDynamicData(
			@ModelAttribute("employeeListForm") EmployeeListForm employeeListForm,
			@RequestParam(value = "xml", required = true) String xml,
			@RequestParam(value = "companyId", required = true) Long companyId,
			@RequestParam(value = "entityId", required = true) Long entityId,
			@RequestParam(value = "formId", required = true) Long formId,
			@RequestParam(value = "version", required = true) Integer version,
			@RequestParam(value = "tabNumber", required = true) Integer tabNumber,
			@RequestParam(value = "employeeID", required = true) String employeeID, Locale locale) {
		EmployeeListFormPage employeeListFormPage = new EmployeeListFormPage();
		Long empid = null;
		try {

			employeeListFormPage = employeeDetailLogic.checkEmployeeNumberEmail(employeeListForm.getEmployeeNumber(),
					companyId, employeeListForm.getEmail());
			if (tabNumber == 0) {

				if (employeeListFormPage.getEmployeeNumberStatus().equalsIgnoreCase(PayAsiaConstants.NOTAVAILABLE)) {
					empid = employeeDetailLogic.addEmployee(employeeListForm, companyId);

					employeeListFormPage = employeeDetailLogic.saveEmployee(xml, companyId, entityId, formId, version,
							empid);
					employeeListFormPage.setUniqueEmployeeId(empid);

				} else {

					employeeListFormPage.setMessage(URLEncoder.encode(
							messageSource.getMessage(employeeListFormPage.getMessage(), new Object[] {}, locale),
							"UTF-8"));

				}

			} else {
				employeeListFormPage.setEmployeeNumberStatus(PayAsiaConstants.NOTAVAILABLE);
				empid = Long.parseLong(employeeID);
				employeeListFormPage = employeeDetailLogic.saveEmployee(xml, companyId, entityId, formId, version,
						empid);
			}

		} catch (UnsupportedEncodingException unsupportedEncodingException) {
			LOGGER.error(unsupportedEncodingException.getMessage(), unsupportedEncodingException);
		} catch (NoSuchMessageException noSuchMessageException) {
			LOGGER.error(noSuchMessageException.getMessage(), noSuchMessageException);
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(employeeListFormPage, jsonConfig);
		return jsonObject.toString();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.EmployeeDetailController#
	 * saveEmployeeStaticData (com.payasia.common.form.EmployeeListForm,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, javax.servlet.http.HttpSession,
	 * java.util.Locale)
	 */
	@Override
	@RequestMapping(value = "/admin/employeeDetail/addEmployee.html", method = RequestMethod.POST)
	@ResponseBody
	public String saveEmployeeStaticData(@ModelAttribute("employeeListForm") EmployeeListForm employeeListForm,
			HttpServletRequest request, HttpServletResponse response, HttpSession session, Locale locale) {
		EmployeeListFormPage employeeListFormPage = new EmployeeListFormPage();
		try {

			Long empId = null;
			Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

			employeeListFormPage = employeeDetailLogic.checkEmployeeNumberEmail(employeeListForm.getEmployeeNumber(),
					companyId, employeeListForm.getEmail());

			if (employeeListFormPage.getEmployeeNumberStatus().equalsIgnoreCase(PayAsiaConstants.NOTAVAILABLE)) {
				empId = employeeDetailLogic.addEmployee(employeeListForm, companyId);
				employeeListFormPage.setUniqueEmployeeId(empId);
			} else {

				employeeListFormPage.setMessage(URLEncoder.encode(
						messageSource.getMessage(employeeListFormPage.getMessage(), new Object[] {}, locale), "UTF-8"));

			}
		} catch (UnsupportedEncodingException unsupportedEncodingException) {
			LOGGER.error(unsupportedEncodingException.getMessage(), unsupportedEncodingException);
		} catch (NoSuchMessageException noSuchMessageException) {
			LOGGER.error(noSuchMessageException.getMessage(), noSuchMessageException);
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(employeeListFormPage, jsonConfig);
		return jsonObject.toString();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.EmployeeDetailController#
	 * updateEmployeeStaticData (com.payasia.common.form.EmployeeListForm,
	 * java.lang.Long, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, javax.servlet.http.HttpSession,
	 * java.util.Locale)
	 */
	@Override
	@RequestMapping(value = { "/admin/employeeDetail/updateEmployeeStaticData.html",
			"/employee/employeeDetail/updateEmployeeStaticData.html" }, method = RequestMethod.POST)
	@ResponseBody
	public String updateEmployeeStaticData(@ModelAttribute("employeeListForm") EmployeeListForm employeeListForm,
			@RequestParam(value = "employeeID", required = true) Long employeeID, HttpServletRequest request,
			HttpServletResponse response, HttpSession session, Locale locale) {
		EmployeeListFormPage employeeListFormPage = new EmployeeListFormPage();
		try {

			Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

			employeeDetailLogic.updateEmployee(employeeListForm, employeeID, companyId);

		} catch (NoSuchMessageException noSuchMessageException) {
			LOGGER.error(noSuchMessageException.getMessage(), noSuchMessageException);
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(employeeListFormPage, jsonConfig);
		return jsonObject.toString();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.EmployeeDetailController#
	 * updateEmployeeStaticAndDynamicData
	 * (com.payasia.common.form.EmployeeListForm, java.lang.String,
	 * java.lang.Long, java.lang.Long, java.lang.Long, java.lang.Integer,
	 * java.lang.Integer, java.lang.Long, java.lang.Long, java.util.Locale)
	 */
	@Override
	@RequestMapping(value = "/admin/employeeDetail/updateEmployee.html", method = RequestMethod.POST)
	@ResponseBody
	public String updateEmployeeStaticAndDynamicData(
			@ModelAttribute("employeeListForm") EmployeeListForm employeeListForm,
			@RequestParam(value = "xml", required = true) String xml,
			@RequestParam(value = "companyId", required = true) Long companyId,
			@RequestParam(value = "entityId", required = true) Long entityId,
			@RequestParam(value = "formId", required = true) Long formId,
			@RequestParam(value = "version", required = true) Integer version,
			@RequestParam(value = "tabNumber", required = true) Integer tabNumber,
			@RequestParam(value = "employeeID", required = true) Long employeeID,
			@RequestParam(value = "tabID", required = true) Long tabID,

			Locale locale) {
		/*ID DECRYPT*/
		formId = FormatPreserveCryptoUtil.decrypt(formId);
		
		EmployeeListFormPage employeeListFormPage = new EmployeeListFormPage();
		boolean isemployeeUpdated = false;
		try {

			Long userId = Long.parseLong(UserContext.getUserId());
			Long companyID = Long.parseLong(UserContext.getWorkingCompanyId());
			isemployeeUpdated = employeeDetailLogic.isAdminAuthorizedForEmployee(employeeID, companyID, userId);
			
			final String decodedXml = URLDecoder.decode(xml, "UTF-8");
			
			if (isemployeeUpdated) {

				if (tabNumber == 0) {
                         
					
					employeeListFormPage = employeeDetailLogic.checkEmployeeNumberIsSame(employeeListForm.getEmployeeNumber(),
							companyId, employeeListForm.getEmail(),employeeID);

					if (employeeListFormPage.getEmployeeNumberStatus().equalsIgnoreCase(PayAsiaConstants.AVAILABLE)) {
						employeeDetailLogic.updateEmployee(employeeListForm, employeeID, companyId);
					} else {
						employeeListFormPage.setMessage(URLEncoder.encode(messageSource.getMessage(employeeListFormPage.getMessage(), new Object[] {}, locale), "UTF-8"));
						employeeListFormPage.setMode(PayAsiaConstants.PAYASIA_ERROR);
					}
				}
				if (tabID == 0) {
					employeeListFormPage = employeeDetailLogic.saveEmployee(decodedXml, companyId, entityId, formId, version,
							employeeID);
				} else {
					tabID = FormatPreserveCryptoUtil.decrypt(tabID);
					employeeDetailLogic.updateEmployeeDynamicFormRecord(decodedXml, companyId, entityId, formId, version,
							employeeID, tabID);
					employeeListFormPage.setDynamicFormRecordId(tabID);
					
				}

				employeeListFormPage.setStatus(PayAsiaConstants.PAYASIA_SUCCESS);
			} else {
				employeeListFormPage.setMode(PayAsiaConstants.PAYASIA_ERROR);
			}
		} catch (UnsupportedEncodingException | NoSuchMessageException noSuchMessageException) {
			LOGGER.error(noSuchMessageException.getMessage(), noSuchMessageException);
			employeeListFormPage.setMode(PayAsiaConstants.PAYASIA_ERROR);
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(employeeListFormPage, jsonConfig);
		return jsonObject.toString();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.EmployeeDetailController#loadgrid(javax.
	 * servlet .http.HttpServletRequest, javax.servlet.http.HttpServletResponse,
	 * java.lang.Long, int, java.lang.String[], java.lang.String[])
	 */
	@Override
	@RequestMapping(value = "/admin/employeeDetail/loadGrid.html", method = RequestMethod.POST)
	@ResponseBody
	public String loadgrid(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "tId", required = true) Long tid,
			@RequestParam(value = "columnCount", required = true) int columnCount,
			@RequestParam(value = "fieldNames", required = true) String[] fieldNames,
			@RequestParam(value = "fieldTypes", required = true) String[] fieldTypes,
			@RequestParam(value = "fieldDictIds", required = true) String[] fieldDictIds,
			@RequestParam(value = "tableType", required = true) String tableType,
			@RequestParam(value = "empID", required = false) Long empID,
			@RequestParam(value = "sortOrder", required = false) String sortOrder,
			@RequestParam(value = "sortBy", required = true) String sortBy, Locale locale) {
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long loggedInemployeeId = (Long) request.getSession()
				.getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		if (tid != null && tid != 0) {
			
			/*ID DECRYPT*/
			empID = FormatPreserveCryptoUtil.decrypt(empID);
			
			Long languageId = multilingualLogic.getLanguageId(locale.toString());
			EmployeeListFormPage employeeListFormPage = employeeDetailLogic.tableRecordList(tid, columnCount,
					fieldNames, fieldTypes, fieldDictIds, companyId, loggedInemployeeId, empID, tableType, sortOrder,
					sortBy, languageId);

			JsonConfig jsonConfig = new JsonConfig();
			JSONObject jsonObject = JSONObject.fromObject(employeeListFormPage, jsonConfig);

			try {

				return URLEncoder.encode(jsonObject.toString(), "UTF8");
			} catch (UnsupportedEncodingException unsupportedEncodingException) {
				LOGGER.error(unsupportedEncodingException.getMessage(), unsupportedEncodingException);
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
	 * com.payasia.web.controller.EmployeeDetailController#editViewGrid(java
	 * .lang.Long, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/admin/employeeDetail/editViewGrid.html", method = RequestMethod.POST)
	@ResponseBody
	public String editViewGrid(@RequestParam(value = "viewId", required = true) Long viewId, HttpServletRequest request,
			HttpServletResponse response) {
		AdminEditViewResponse adminEditViewResponse = new AdminEditViewResponse();

		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		List<EntityListViewFieldForm> list = employeeDetailLogic.editView(companyId, viewId);

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
		JSONObject jsonObject = JSONObject.fromObject(adminEditViewResponse, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException unsupportedEncodingException) {
			LOGGER.error(unsupportedEncodingException.getMessage(), unsupportedEncodingException);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.EmployeeDetailController#editView(java.lang
	 * .Long, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/admin/employeeDetail/editView.html", method = RequestMethod.POST)
	@ResponseBody
	public String editView(@RequestParam(value = "view", required = true) Long viewId, HttpServletRequest request,
			HttpServletResponse response, Locale locale) {
		AdminEditViewResponse adminEditViewResponse = new AdminEditViewResponse();
		UserContext.setLocale(locale);
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		List<EntityListViewFieldForm> list = employeeDetailLogic.listEditView(companyId, viewId);

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
		JSONObject jsonObject = JSONObject.fromObject(adminEditViewResponse, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException unsupportedEncodingException) {
			LOGGER.error(unsupportedEncodingException.getMessage(), unsupportedEncodingException);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.EmployeeDetailController#deleteView(java.lang
	 * .Long)
	 */
	@Override
	@RequestMapping(value = "/admin/employeeDetail/deleteViewGrid.html", method = RequestMethod.POST)
	@ResponseBody
	public String deleteView(@RequestParam(value = "viewId", required = true) Long viewId) {
		AdminEditViewResponse response = new AdminEditViewResponse();

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		boolean status = employeeDetailLogic.isAdminAuthorizedForViewGrid(viewId, companyId);
		if (status)
			employeeDetailLogic.deleteView(viewId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(response, jsonConfig);
		return jsonObject.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.EmployeeDetailController#saveCustomView(org
	 * .springframework.ui.ModelMap, java.lang.String, int, java.lang.String[],
	 * java.lang.String[], javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/admin/employeeDetail/saveCustomView.html", method = RequestMethod.POST)
	@ResponseBody
	public String saveCustomView(ModelMap model, @RequestParam(value = "viewName", required = true) String viewName,
			@RequestParam(value = "recordsPerPage", required = true) int recordsPerPage,
			@RequestParam(value = "dataDictionaryId", required = true) String[] dataDictionaryIdArr,
			@RequestParam(value = "rowIndexs", required = true) String[] rowIndexsArr, HttpServletRequest request,
			HttpServletResponse response) {

		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		EmployeeListFormPage employeeListFormPage = employeeDetailLogic.checkView(viewName, companyId);

		if (employeeListFormPage.getStatus().equalsIgnoreCase(PayAsiaConstants.NOTAVAILABLE)) {
			employeeDetailLogic.saveCustomView(companyId, viewName, recordsPerPage, dataDictionaryIdArr, rowIndexsArr);
		}

		JsonConfig jsonConfig = new JsonConfig();

		JSONObject jsonObject = JSONObject.fromObject(employeeListFormPage, jsonConfig);
		return jsonObject.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.EmployeeDetailController#updateCustomView(
	 * org.springframework.ui.ModelMap, java.lang.String, int,
	 * java.lang.String[], java.lang.String[], java.lang.Long,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/admin/employeeDetail/updateCustomView.html", method = RequestMethod.POST)
	@ResponseBody
	public String updateCustomView(ModelMap model, @RequestParam(value = "viewName", required = true) String viewName,
			@RequestParam(value = "recordsPerPage", required = true) int recordsPerPage,
			@RequestParam(value = "dataDictionaryId", required = true) String[] dataDictionaryIdArr,
			@RequestParam(value = "rowIndexs", required = true) String[] rowIndexsArr,
			@RequestParam(value = "viewId", required = true) Long viewId, HttpServletRequest request,
			HttpServletResponse response) {

		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		EmployeeListFormPage employeeListFormPage = employeeDetailLogic.checkViewUpdate(viewName, companyId, viewId);

		if (employeeListFormPage.getStatus().equalsIgnoreCase(PayAsiaConstants.NOTAVAILABLE)) {
			employeeDetailLogic.updateCustomView(companyId, viewName, recordsPerPage, dataDictionaryIdArr, rowIndexsArr,
					viewId);
		}

		JsonConfig jsonConfig = new JsonConfig();

		JSONObject jsonObject = JSONObject.fromObject(employeeListFormPage, jsonConfig);
		return jsonObject.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.EmployeeDetailController#getViewList(javax
	 * .servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/admin/employeeDetail/employeeGridViewList.html", method = RequestMethod.POST)
	@ResponseBody
	public String getViewList(HttpServletRequest request, HttpServletResponse response) {

		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		List<EntityListViewForm> viewNameList = employeeDetailLogic.getViewName(companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(viewNameList, jsonConfig);
		return jsonObject.toString();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.EmployeeDetailController#getCustomColumnName
	 * (java.lang.Long)
	 */
	@Override
	@RequestMapping(value = "/admin/employeeDetail/employeeCustomColumnNames.html", method = RequestMethod.POST)
	@ResponseBody
	public String getCustomColumnName(@RequestParam(value = "viewID", required = true) Long viewID) {

		List<EntityListViewFieldForm> entityListView = employeeDetailLogic.getCustomColumnName(viewID);

		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(entityListView, jsonConfig);
		return jsonObject.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.EmployeeDetailController#uploadEmployeeImage
	 * (com.payasia.common.form.EmployeeListForm, java.lang.Long,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = { "/admin/employeeDetail/uploadEmployeeImage.html",
			"/employee/employeeDetail/uploadEmployeeImage.html" }, method = RequestMethod.POST, produces = "text/html; charset=utf-8")
	@ResponseBody
	public String uploadEmployeeImage(@ModelAttribute("employeeListForm") EmployeeListForm employeeListForm,
			@RequestParam(value = "empID", required = false) Long empID, HttpServletRequest request,
			HttpServletResponse response) {
		EmployeeListFormPage employeeListFormPage = new EmployeeListFormPage();

		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		boolean isValidImg=false;
		
		if(employeeListForm.getEmployeeImage()!=null){
			isValidImg = FileUtils.isValidFile(employeeListForm.getEmployeeImage(), employeeListForm.getEmployeeImage().getOriginalFilename(), PayAsiaConstants.ALLOWED_UPLOAD_IMAGE_EXT, PayAsiaConstants.ALLOWED_UPLOAD_IMAGE_MINE_TYPE, PayAsiaConstants.FILE_SIZE);
		}
		
		if(isValidImg) {
			employeeDetailLogic.updateEmployeeImage(employeeListForm, companyId, empID);
			employeeListFormPage.setStatus(PayAsiaConstants.PAYASIA_SUCCESS);
		} else {
			employeeListFormPage.setStatus(PayAsiaConstants.PAYASIA_ERROR);
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(employeeListFormPage, jsonConfig);
		return jsonObject.toString();

	}
	
	

	@Override
	@RequestMapping(value = { "/admin/employeeDetail/employeeImageOfAdmin"
			 }, method = RequestMethod.GET)
	public @ResponseBody byte[] getAdminEmployeeImage(@RequestParam(value = "empID", required = false) long empID,
			HttpServletResponse response, HttpServletRequest request) throws IOException {
		/*ID DECRYPT*/
		empID = FormatPreserveCryptoUtil.decrypt(empID);
		// Auth Check
		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		if (!employeeDetailLogic.areEmployeesOfSameCompanyGroup(UserContext.getWorkingCompanyId(), empID, employeeId)) {
			throw new PayAsiaSystemException(PayAsiaConstants.PAYASIA_UNAUTHORIZED_ACCESS, "Unauthorized Access");
		}

		byte[] byteFile = null;
		String imagePath = servletContext.getRealPath("/resources/images/") + "/profile-default.png";

		byteFile = employeeDetailLogic.getEmployeeImage(empID, imagePath, employeeImageWidth, employeeImageHeight);
		String imageName = byteFile.getClass().getName();
		response.reset();
		String mimeType = URLConnection.guessContentTypeFromName(imageName);
		response.setContentType(mimeType);
		response.setContentLength(byteFile.length);

		response.setHeader("Content-Disposition", "inline;filename=" + imageName);
		return byteFile;

	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.EmployeeDetailController#getEmployeeImage(
	 * long, javax.servlet.http.HttpServletResponse,
	 * javax.servlet.http.HttpServletRequest)
	 */
	@Override
	@RequestMapping(value = { "/admin/employeeDetail/employeeImage",
			"/employee/employeeDetail/employeeImage" }, method = RequestMethod.GET)
	public @ResponseBody byte[] getEmployeeImage(@RequestParam(value = "empID", required = false) long empID,
			HttpServletResponse response, HttpServletRequest request) throws IOException {

		// Auth Check
		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		if (!employeeDetailLogic.areEmployeesOfSameCompanyGroup(UserContext.getWorkingCompanyId(), empID, employeeId)) {
			throw new PayAsiaSystemException(PayAsiaConstants.PAYASIA_UNAUTHORIZED_ACCESS, "Unauthorized Access");
		}

		byte[] byteFile = null;
		String imagePath = servletContext.getRealPath("/resources/images/") + "/profile-default.png";

		byteFile = employeeDetailLogic.getEmployeeImage(empID, imagePath, employeeImageWidth, employeeImageHeight);
		String imageName = byteFile.getClass().getName();
		response.reset();
		String mimeType = URLConnection.guessContentTypeFromName(imageName);
		response.setContentType(mimeType);
		response.setContentLength(byteFile.length);

		response.setHeader("Content-Disposition", "inline;filename=" + imageName);
		return byteFile;

	}

	@Override
	@RequestMapping(value = "/employee/employeeDetail/employeeViewProfileImage", method = RequestMethod.GET)
	public @ResponseBody byte[] employeeViewProfileImage(HttpServletResponse response, HttpServletRequest request)
			throws IOException {
		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		byte[] byteFile = null;
		String imagePath = servletContext.getRealPath("/resources/images/") + "/profile-default.png";

		byteFile = employeeDetailLogic.getEmployeeImage(employeeId, imagePath, employeeImageWidth, employeeImageHeight);
		String imageName = byteFile.getClass().getName();
		response.reset();
		String mimeType = URLConnection.guessContentTypeFromName(imageName);
		response.setContentType(mimeType);
		response.setContentLength(byteFile.length);

		response.setHeader("Content-Disposition", "inline;filename=" + imageName);
		return byteFile;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.EmployeeDetailController#getEmployeeImage(
	 * long, javax.servlet.http.HttpServletResponse,
	 * javax.servlet.http.HttpServletRequest)
	 */
	@Override
	@RequestMapping(value = "/employee/employeeDetail/getHomePageEmployeeImage", method = RequestMethod.GET)
	public @ResponseBody void getHomePageEmployeeImage(HttpServletResponse response, HttpServletRequest request) {

		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		ServletOutputStream outStream = null;
		byte[] byteFile = null;
		String imagePath = servletContext.getRealPath("/resources/images/") + "/profile-default.png";
		try {
			byteFile = employeeDetailLogic.getEmployeeImage(employeeId, imagePath, employeeImageWidth,
					employeeImageHeight);
			response.reset();
			response.setContentType("png");
			response.setContentLength(byteFile.length);
			String imageName = byteFile.getClass().getName();

			response.setHeader("Content-Disposition", "inline;filename=" + imageName);

			outStream = response.getOutputStream();
			outStream.write(byteFile);

		} catch (IOException iOException) {
			LOGGER.error(iOException.getMessage(), iOException);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.EmployeeDetailController#getEmployeeName(long,
	 * javax.servlet.http.HttpServletResponse,
	 * javax.servlet.http.HttpServletRequest)
	 */
	@Override
	@RequestMapping(value = { "/employee/employeeDetail/employeeName.html",
			"/admin/employeeDetail/employeeName.html" }, method = RequestMethod.POST)
	@ResponseBody
	public String getEmployeeName(HttpServletResponse response, HttpServletRequest request) {
		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		String empName = employeeDetailLogic.getEmployeeName(employeeId);
		try {
			return URLEncoder.encode(empName, "UTF-8");
		} catch (UnsupportedEncodingException unsupportedEncodingException) {
			LOGGER.error(unsupportedEncodingException.getMessage(), unsupportedEncodingException);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.EmployeeDetailController#
	 * getEmpPasswordAndSendMail (long, java.lang.String,
	 * javax.servlet.http.HttpServletResponse,
	 * javax.servlet.http.HttpServletRequest, java.util.Locale)
	 */
	@Override
	@RequestMapping(value = "/admin/employeeDetail/empPasswordAndSendMail.html", method = RequestMethod.POST)
	@ResponseBody
	public String getEmpPasswordAndSendMail(@RequestParam(value = "employeeId", required = false) long employeeId,
			@RequestParam(value = "password", required = false) String password, HttpServletResponse response,
			HttpServletRequest request, Locale locale) {

		boolean isempPasswordAndSendMail = false;
		String sendMailStatus = null;
		try {

			Long userId = Long.parseLong(UserContext.getUserId());
			Long companyID = Long.parseLong(UserContext.getWorkingCompanyId());
			isempPasswordAndSendMail = employeeDetailLogic.isAdminAuthorizedForEmployee(employeeId, companyID, userId);

			if (isempPasswordAndSendMail) {

				sendMailStatus = employeeDetailLogic.getEmpPasswordAndSendMail(employeeId, password);

				if (!sendMailStatus.equalsIgnoreCase(PayAsiaConstants.PAYASIA_ERROR)) {
					sendMailStatus = URLEncoder
							.encode(messageSource.getMessage(sendMailStatus, new Object[] {}, locale), "UTF-8");
				}

			} else {
				sendMailStatus = PayAsiaConstants.PAYASIA_ERROR;
			}

		} catch (UnsupportedEncodingException unsupportedEncodingException) {
			LOGGER.error(unsupportedEncodingException.getMessage(), unsupportedEncodingException);
		} catch (NoSuchMessageException noSuchMessageException) {
			LOGGER.error(noSuchMessageException.getMessage(), noSuchMessageException);
		}
		return sendMailStatus;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.EmployeeDetailController#getEmployeeImageSize
	 * (com.payasia.common.form.EmployeeListForm,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	// TODO unused method
	// @RequestMapping(value = "/employeeImageSize.html", method =
	// RequestMethod.POST)
	@ResponseBody
	public String getEmployeeImageSize(@ModelAttribute("employeeListForm") EmployeeListForm employeeListForm,
			HttpServletRequest request, HttpServletResponse response) {
		EmployeeListFormPage employeeListFormPage = new EmployeeListFormPage();
		int maxFileSize = employeeDetailLogic.getEmployeeImageSize();
		employeeListFormPage.setMaxFileSize(maxFileSize);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(employeeListFormPage, jsonConfig);
		return jsonObject.toString();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.EmployeeDetailController#getPasswordPolicy
	 * (javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/admin/employeeDetail/passwordPolicy.html", method = RequestMethod.POST)
	@ResponseBody
	public String getPasswordPolicy(HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		PasswordPolicyPreferenceForm passwordPolicy = employeeDetailLogic.getPasswordPolicy(companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(passwordPolicy, jsonConfig);
		return jsonObject.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.EmployeeDetailController#getResetPassword(
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/admin/employeeDetail/resetPassword.html", method = RequestMethod.POST)
	@ResponseBody
	public String getResetPassword(HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		String resetPwd = employeeDetailLogic.getResetPassword(companyId, employeeId);
		return resetPwd;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.EmployeeDetailController#editEmployee(com.
	 * payasia.common.form.EmployeeListForm,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, javax.servlet.http.HttpSession,
	 * java.util.Locale, java.lang.Long)
	 */
	@Override
	@RequestMapping(value = "/admin/employeeDetail/employeexXML.html", method = RequestMethod.POST)
	@ResponseBody
	public String getEmployeeXML(@RequestParam(value = "empID", required = false) Long empID,
			@RequestParam(value = "formId", required = false) Long formId, HttpServletRequest request,
			HttpServletResponse response, HttpSession session, Locale locale) {
		/*ID DECRYPT*/
		empID = FormatPreserveCryptoUtil.decrypt(empID);
		formId = FormatPreserveCryptoUtil.decrypt(formId);
		EmployeeListFormPage employeeListFormPage = null;
		Long languageId = multilingualLogic.getLanguageId(locale.toString());
		boolean employeeUpdated = false;
		Long userId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		employeeUpdated = employeeDetailLogic.isAdminAuthorizedForEmployee(empID, companyId, userId);
		if (employeeUpdated) {
			employeeListFormPage = employeeDetailLogic.getXML(empID, companyId, languageId, formId);
		}
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(employeeListFormPage, jsonConfig);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/admin/employeeDetail/saveTableRecord.html", method = RequestMethod.POST)
	@ResponseBody
	public String saveTableRecord(@RequestParam(value = "tableXML", required = true) String tableXML,
			@RequestParam(value = "tabId", required = true) Long tabId,
			@RequestParam(value = "seqNo", required = true) Integer seqNo,
			@RequestParam(value = "formId", required = true) Long formId,
			@RequestParam(value = "version", required = true) Integer version,
			@RequestParam(value = "entityKey", required = true) Long entityKey, HttpServletRequest request,
			HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		EmployeeListFormPage employeeListFormPage = new EmployeeListFormPage();
		try {
			/*ID DECRYPT*/
			formId = FormatPreserveCryptoUtil.decrypt(formId);
			if(tabId.toString().length()>14 ){
				   tabId = FormatPreserveCryptoUtil.decrypt(tabId);
				}
			final String decodedTableXML = URLDecoder.decode(tableXML, "UTF-8");
			
			employeeListFormPage = employeeDetailLogic.saveTableRecord(decodedTableXML, tabId, companyId, employeeId, formId,
					version, entityKey);
			employeeListFormPage.setStatus(PayAsiaConstants.PAYASIA_SUCCESS);

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			employeeListFormPage.setStatus(PayAsiaConstants.PAYASIA_ERROR);

		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(employeeListFormPage, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/employeeDetail/updateTableRecord.html", method = RequestMethod.POST)
	@ResponseBody
	public String updateTableRecord(@RequestParam(value = "tableXML", required = true) String tableXML,
			@RequestParam(value = "tabId", required = true) Long tabId,
			@RequestParam(value = "seqNo", required = true) Integer seqNo, HttpServletRequest request,
			HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		EmployeeListFormPage employeeListFormPage = new EmployeeListFormPage();
		try {
			
			final String decodedTableXML = URLDecoder.decode(tableXML, "UTF-8");
			/*ID DECRYPT*/
			tabId = FormatPreserveCryptoUtil.decrypt(tabId);
			employeeListFormPage = employeeDetailLogic.updateTableRecord(decodedTableXML, tabId, companyId, employeeId, seqNo);

			employeeListFormPage.setStatus(PayAsiaConstants.PAYASIA_SUCCESS);

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			employeeListFormPage.setStatus(PayAsiaConstants.PAYASIA_ERROR);

		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(employeeListFormPage, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/employee/employeeDetail/deleteEmpTableRecord.html", method = RequestMethod.POST)
	@ResponseBody
	public String deleteEmpTableRecord(@RequestParam(value = "tableId", required = true) Long tableId,
			@RequestParam(value = "seqNo", required = true) Integer seqNo,
			@RequestParam(value = "tableType", required = true) String tableType,
			@RequestParam(value = "fieldDictIds", required = true) String[] fieldDictIds,
			@RequestParam(value = "fieldLabel", required = true) String fieldLabel,
			@RequestParam(value = "formId", required = true) String formId, HttpServletRequest request,
			HttpServletResponse response) {

		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		EmployeeListFormPage employeeListFormPage = new EmployeeListFormPage();
		try {

			if (PayAsiaConstants.TRUE.equalsIgnoreCase(employeeDetailLogic.isEnableEmployeeChangeWorkflow(companyId))) {
				Long formIdValue=Long.parseLong(formId);
				
				/*ID DECRYPT*/
				formIdValue= FormatPreserveCryptoUtil.decrypt(formIdValue);
				employeeListFormPage = employeeDetailLogic.deleteTableRecord(tableId, companyId, seqNo, tableType,
						employeeId, fieldDictIds, fieldLabel,formIdValue,false);
			}
			else{
				employeeListFormPage.setStatus(PayAsiaConstants.PAYASIA_ERROR);
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			employeeListFormPage.setStatus(PayAsiaConstants.PAYASIA_ERROR);

		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(employeeListFormPage, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/employeeDetail/deleteTableRecord.html", method = RequestMethod.POST)
	@ResponseBody
	public String deleteTableRecord(@RequestParam(value = "tableId", required = true) Long tableId,
			@RequestParam(value = "seqNo", required = true) Integer seqNo,
			@RequestParam(value = "tableType", required = true) String tableType,
			@RequestParam(value = "entityKey", required = true) Long entityKey,
			@RequestParam(value = "fieldDictIds", required = true) String[] fieldDictIds,
			@RequestParam(value = "fieldLabel", required = true) String fieldLabel, HttpServletRequest request,
			HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		EmployeeListFormPage employeeListFormPage = new EmployeeListFormPage();
		try {
			Long userId = Long.parseLong(UserContext.getUserId());
			Long companyID = Long.parseLong(UserContext.getWorkingCompanyId());
			/*ID DECRYPT*/
			entityKey = FormatPreserveCryptoUtil.decrypt(entityKey);
			boolean employeeDeleted = employeeDetailLogic.isAdminAuthorizedForEmployee(entityKey, companyID, userId);
			if (employeeDeleted) {

				employeeListFormPage = employeeDetailLogic.deleteTableRecord(tableId, companyId, seqNo, tableType,
						entityKey, fieldDictIds, fieldLabel,null,true);
			} else {
				employeeListFormPage.setStatus("ERROR");
			}

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			employeeListFormPage.setStatus(PayAsiaConstants.PAYASIA_ERROR);

		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(employeeListFormPage, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = { "/admin/employeeDetail/deleteEmployeeImage.html",
			"/employee/employeeDetail/deleteEmployeeImage.html" }, method = RequestMethod.POST)
	@ResponseBody
	public String deleteEmployeeImage(@RequestParam(value = "empID", required = false) Long empID,
			HttpServletRequest request, HttpServletResponse response) {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		Long loggedInEmployeeId = (Long) request.getSession()
				.getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		String status = PayAsiaConstants.PAYASIA_ERROR;
		if (loggedInEmployeeId.equals(empID))

			status = employeeDetailLogic.deleteEmployeeImage(companyId, empID);
		else {
			Long userId = Long.parseLong(UserContext.getUserId());

			boolean employeeDeleted = employeeDetailLogic.isAdminAuthorizedForEmployee(empID, companyId, userId);
			if (employeeDeleted) {
				status = employeeDetailLogic.deleteEmployeeImage(companyId, empID);
			}

		}
		return status;
	}

	@Override
	@RequestMapping(value = "/employee/employeeDetail/updateEmployeeViewProfile.html", method = RequestMethod.POST)
	@ResponseBody
	public String updateEmpStaticAndDynamicViewProfileData(
			@ModelAttribute("employeeListForm") EmployeeListForm employeeListForm,
			@RequestParam(value = "xml", required = true) String xml,
			@RequestParam(value = "companyId", required = true) Long companyId,
			@RequestParam(value = "entityId", required = true) Long entityId,
			@RequestParam(value = "formId", required = true) Long formId,
			@RequestParam(value = "version", required = true) Integer version,
			@RequestParam(value = "tabNumber", required = true) Integer tabNumber,
			@RequestParam(value = "employeeID", required = true) Long employeeID,
			@RequestParam(value = "tabID", required = true) Long tabID, Locale locale) {
		EmployeeListFormPage employeeListFormPage = new EmployeeListFormPage();
		Long languageId = multilingualLogic.getLanguageId(locale.toString());
		try {
			
			final String decodedXml = URLDecoder.decode(xml, "UTF-8");

            /*ID DECRYPT*/
			formId = FormatPreserveCryptoUtil.decrypt(formId);
			
			if (tabID == 0) {
				employeeListFormPage = employeeDetailLogic.saveEmployeeViewProfile(decodedXml, companyId, entityId, formId,
					version, employeeID, languageId);
			} else {
				/*ID DECRYPT*/
				tabID = FormatPreserveCryptoUtil.decrypt(tabID);
				employeeListFormPage = employeeDetailLogic.updateEmplViewProfileDynamicFormRecord(decodedXml, companyId,
						entityId, formId, version, employeeID, tabID, languageId);
				employeeListFormPage.setDynamicFormRecordId(tabID);
			}
			employeeListFormPage.setStatus(PayAsiaConstants.PAYASIA_SUCCESS);
		} catch (UnsupportedEncodingException | NoSuchMessageException noSuchMessageException) {
			LOGGER.error(noSuchMessageException.getMessage(), noSuchMessageException);
			employeeListFormPage.setStatus(PayAsiaConstants.PAYASIA_ERROR);
		}
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(employeeListFormPage, jsonConfig);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/employee/employeeDetail/updateViewProfileTableRecord.html", method = RequestMethod.POST)
	@ResponseBody
	public String updateViewProfileTableRecord(@RequestParam(value = "tableXML", required = true) String tableXML,
			@RequestParam(value = "tabId", required = true) Long tabId,
			@RequestParam(value = "seqNo", required = true) Integer seqNo, HttpServletRequest request,
			HttpServletResponse response, Locale locale) {
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		Long languageId = multilingualLogic.getLanguageId(locale.toString());
		EmployeeListFormPage employeeListFormPage = new EmployeeListFormPage();
		try {
			employeeListFormPage = employeeDetailLogic.updateViewProfileTableRecord(tableXML, tabId, companyId,
					employeeId, seqNo, languageId);

			employeeListFormPage.setStatus(PayAsiaConstants.PAYASIA_SUCCESS);

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			employeeListFormPage.setStatus(PayAsiaConstants.PAYASIA_ERROR);

		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(employeeListFormPage, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/employee/employeeDetail/saveViewProfileTableRecord.html", method = RequestMethod.POST)
	@ResponseBody
	public String saveViewProfileTableRecord(@RequestParam(value = "tableXML", required = true) String tableXML,
			@RequestParam(value = "tabId", required = true) Long tabId,
			@RequestParam(value = "seqNo", required = true) Integer seqNo,
			@RequestParam(value = "formId", required = true) Long formId,
			@RequestParam(value = "version", required = true) Integer version, HttpServletRequest request,
			HttpServletResponse response, Locale locale) {
		
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		Long languageId = multilingualLogic.getLanguageId(locale.toString());
		
		EmployeeListFormPage employeeListFormPage = new EmployeeListFormPage();
		try {
			
			final String decodedTableXML = URLDecoder.decode(tableXML, "UTF-8"); 
			
			
			if (PayAsiaConstants.TRUE.equalsIgnoreCase(employeeDetailLogic.isEnableEmployeeChangeWorkflow(companyId))) {
				/*ID DECRYPT*/
				formId = FormatPreserveCryptoUtil.decrypt(formId);	
				tabId = FormatPreserveCryptoUtil.decrypt(tabId);
				
				employeeListFormPage = employeeDetailLogic.saveViewProfileTableRecord(decodedTableXML, tabId, companyId,
					employeeId, formId, version, employeeId, languageId);
			employeeListFormPage.setStatus(PayAsiaConstants.PAYASIA_SUCCESS);
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			employeeListFormPage.setStatus(PayAsiaConstants.PAYASIA_ERROR);

		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(employeeListFormPage, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/employee/employeeDetail/editEmployeeViewProfile.html", method = RequestMethod.POST)
	@ResponseBody
	public String editEmployeeViewProfile(@ModelAttribute("employeeListForm") EmployeeListForm employeeListForm,
			HttpServletRequest request, HttpServletResponse response, HttpSession session, Locale locale,
			@RequestParam(value = "mode", required = false) String mode) {
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long languageId = multilingualLogic.getLanguageId(locale.toString());
		Long loggedInEmployeeId = (Long) request.getSession()
				.getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		EmployeeListFormPage employeeListFormPage = employeeDetailLogic.getViewProfileUpdatedXmls(loggedInEmployeeId,
				loggedInEmployeeId, companyId, languageId, mode);
		employeeListFormPage.setUniqueEmployeeId(loggedInEmployeeId);

		int maxFileSize = employeeDetailLogic.getEmployeeImageSize();
		employeeListFormPage.setMaxFileSize(maxFileSize);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(employeeListFormPage, jsonConfig);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/employee/employeeDetail/employeeViewProfileXML.html", method = RequestMethod.POST)
	@ResponseBody
	public String getViewProfileEmployeeXML(@RequestParam(value = "formId", required = false) Long formId,
			HttpServletRequest request, HttpServletResponse response, HttpSession session, Locale locale) {
		
		/*ID DECRYPT*/
		formId= FormatPreserveCryptoUtil.decrypt(formId);
		
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long languageId = multilingualLogic.getLanguageId(locale.toString());
		Long loggedInEmployeeId = (Long) request.getSession()
				.getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		EmployeeListFormPage employeeListFormPage = employeeDetailLogic.getViewProfileXML(loggedInEmployeeId, companyId,
				languageId, formId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(employeeListFormPage, jsonConfig);
		return jsonObject.toString();

	}	

	@Override
	@RequestMapping(value = { "/employee/employeeDetail/empChangeRequestData.html",
			"/admin/employeeDetail/empChangeRequestData.html" }, method = RequestMethod.POST)
	@ResponseBody
	public String getEmpChangeRequestData(
			@RequestParam(value = "HRISChangeRequestId", required = false) Long HRISChangeRequestId,
			HttpServletRequest request, HttpServletResponse response, HttpSession session, Locale locale) {
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long loggedInEmployeeId = (Long) request.getSession()
				.getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		Long languageId = multilingualLogic.getLanguageId(locale.toString());
		EmployeeListFormPage employeeListFormPage = employeeDetailLogic.getEmpChangeRequestData(loggedInEmployeeId,
				companyId, HRISChangeRequestId, languageId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(employeeListFormPage, jsonConfig);

		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;

	}

	@Override
	@RequestMapping(value = "/employee/employeeDetail/empViewProfileTableChangeRequestData.html", method = RequestMethod.POST)
	@ResponseBody
	public String getViewProfileTableEmpChangeRequestData(
			@RequestParam(value = "tableFieldDictionaryId", required = false) Long tableFieldDictionaryId,
			@RequestParam(value = "seqNum", required = false) int seqNum, HttpServletRequest request,
			HttpServletResponse response, HttpSession session, Locale locale) {
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long loggedInEmployeeId = (Long) request.getSession()
				.getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		Long languageId = multilingualLogic.getLanguageId(locale.toString());
		EmployeeListFormPage employeeListFormPage = employeeDetailLogic.getTableEmpChangeRequestData(loggedInEmployeeId,
				companyId, loggedInEmployeeId, tableFieldDictionaryId, seqNum, languageId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(employeeListFormPage, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;

	}

	@Override
	@RequestMapping(value = "/admin/employeeDetail/empTableChangeRequestData.html", method = RequestMethod.POST)
	@ResponseBody
	public String getTableEmpChangeRequestData(
			@RequestParam(value = "tableFieldDictionaryId", required = false) Long tableFieldDictionaryId,
			@RequestParam(value = "seqNum", required = false) int seqNum,
			@RequestParam(value = "empID", required = false) Long empID, HttpServletRequest request,
			HttpServletResponse response, HttpSession session, Locale locale) {
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long loggedInEmployeeId = (Long) request.getSession()
				.getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		Long languageId = multilingualLogic.getLanguageId(locale.toString());
		EmployeeListFormPage employeeListFormPage = employeeDetailLogic.getTableEmpChangeRequestData(loggedInEmployeeId,
				companyId, empID, tableFieldDictionaryId, seqNum, languageId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(employeeListFormPage, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;

	}

	@Override
	@RequestMapping(value = "/employee/employeeDetail/saveEmpDocTableRecord.html", method = RequestMethod.POST, produces = "text/html; charset=utf-8")
	@ResponseBody
	public String saveEmpDocTableRecord(
			@ModelAttribute("addEmpDynDocumentForm") DynamicFormTableDocumentDTO dynamicFormDocumentDTO,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long loggedInEmployeeId = (Long) request.getSession()
				.getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		
		/*ID DECRYPT*/	
		dynamicFormDocumentDTO.setFormId(FormatPreserveCryptoUtil.decrypt(dynamicFormDocumentDTO.getFormId()));
		dynamicFormDocumentDTO.setTabId(FormatPreserveCryptoUtil.decrypt(dynamicFormDocumentDTO.getTabId()));
		
		dynamicFormDocumentDTO.setEntityKey(String.valueOf(loggedInEmployeeId));
		
		EmployeeListFormPage employeeListFormPage = new EmployeeListFormPage();
		try {
			boolean isFileValid = false;
			if(dynamicFormDocumentDTO!=null && dynamicFormDocumentDTO.getUploadFile()!=null){
			  isFileValid = FileUtils.isValidFile(dynamicFormDocumentDTO.getUploadFile(), dynamicFormDocumentDTO.getUploadFile().getOriginalFilename(), PayAsiaConstants.ALLOWED_GENERAL_UPLOAD_FILE_EXT, PayAsiaConstants.ALLOWED_GENERAL_UPLOAD_FILE_MINE_TYPE, PayAsiaConstants.FILE_SIZE);
			}else{
				isFileValid = true;
			}
			
			if(isFileValid){
				employeeListFormPage = employeeDetailLogic.saveDocTableRecord(dynamicFormDocumentDTO, companyId,loggedInEmployeeId);
				employeeListFormPage.setStatus(PayAsiaConstants.PAYASIA_SUCCESS);
	
			}else{
				
				employeeListFormPage.setStatus(PayAsiaConstants.PAYASIA_ERROR);
			}
			
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			employeeListFormPage.setStatus(PayAsiaConstants.PAYASIA_ERROR);

		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(employeeListFormPage, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/employeeDetail/saveDocTableRecord.html", method = RequestMethod.POST, produces = "text/html; charset=utf-8")
	@ResponseBody
	public String saveTableDocRecord(
			@ModelAttribute("addEmpDynDocumentForm") DynamicFormTableDocumentDTO dynamicFormDocumentDTO,
			HttpServletRequest request, HttpServletResponse response) {
		
		/*ID DECRYPT*/	
		dynamicFormDocumentDTO.setFormId(FormatPreserveCryptoUtil.decrypt(dynamicFormDocumentDTO.getFormId()));
		dynamicFormDocumentDTO.setEntityKey(String.valueOf(FormatPreserveCryptoUtil.decrypt(Long.valueOf(dynamicFormDocumentDTO.getEntityKey()))));
		if(dynamicFormDocumentDTO.getTabId().toString().length()>15 ){
			  dynamicFormDocumentDTO.setTabId(FormatPreserveCryptoUtil.decrypt(dynamicFormDocumentDTO.getTabId()));
			}
		
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long loggedInEmployeeId = (Long) request.getSession()
				.getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		EmployeeListFormPage employeeListFormPage = new EmployeeListFormPage();
		try {
			
			 boolean isFileValid = false;
			if(dynamicFormDocumentDTO!=null && dynamicFormDocumentDTO.getUploadFile()!=null){
			  isFileValid = FileUtils.isValidFile(dynamicFormDocumentDTO.getUploadFile(), dynamicFormDocumentDTO.getUploadFile().getOriginalFilename(), PayAsiaConstants.ALLOWED_GENERAL_UPLOAD_FILE_EXT, PayAsiaConstants.ALLOWED_GENERAL_UPLOAD_FILE_MINE_TYPE, PayAsiaConstants.FILE_SIZE);
			}else{
				isFileValid = true;
			}
			
		   if(isFileValid){
				employeeListFormPage = employeeDetailLogic.saveDocTableRecord(dynamicFormDocumentDTO, companyId,loggedInEmployeeId);
				employeeListFormPage.setStatus(PayAsiaConstants.PAYASIA_SUCCESS);
			}else{
				employeeListFormPage.setStatus(PayAsiaConstants.PAYASIA_ERROR);
			}

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			employeeListFormPage.setStatus(PayAsiaConstants.PAYASIA_ERROR);

		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(employeeListFormPage, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/employeeDetail/updateDocTableRecord.html", method = RequestMethod.POST, produces = "text/html; charset=utf-8")
	@ResponseBody
	public String updateDocTableRecord(
			@ModelAttribute("addEmpDynDocumentForm") DynamicFormTableDocumentDTO dynamicFormDocumentDTO,
			HttpServletRequest request, HttpServletResponse response) {
		
		/*ID DECRYPT*/	
		dynamicFormDocumentDTO.setFormId(FormatPreserveCryptoUtil.decrypt(dynamicFormDocumentDTO.getFormId()));
		dynamicFormDocumentDTO.setTabId(FormatPreserveCryptoUtil.decrypt(dynamicFormDocumentDTO.getTabId()));
		dynamicFormDocumentDTO.setEntityKey(String.valueOf(FormatPreserveCryptoUtil.decrypt(Long.valueOf(dynamicFormDocumentDTO.getEntityKey()))));
		
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long loggedInEmployeeId = (Long) request.getSession()
				.getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		EmployeeListFormPage employeeListFormPage = new EmployeeListFormPage();
		try {
			employeeListFormPage = employeeDetailLogic.updateDocTableRecord(dynamicFormDocumentDTO, companyId,
					loggedInEmployeeId);
			employeeListFormPage.setStatus(PayAsiaConstants.PAYASIA_SUCCESS);

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			employeeListFormPage.setStatus(PayAsiaConstants.PAYASIA_ERROR);

		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(employeeListFormPage, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/employee/employeeDetail/updateEmpDocTableRecord.html", method = RequestMethod.POST, produces = "text/html; charset=utf-8")
	@ResponseBody
	public String updateEmpDocTableRecord(
			@ModelAttribute("addEmpDynDocumentForm") DynamicFormTableDocumentDTO dynamicFormDocumentDTO,
			HttpServletRequest request, HttpServletResponse response) {
		
		
		/*ID DECRYPT*/	
		dynamicFormDocumentDTO.setFormId(FormatPreserveCryptoUtil.decrypt(dynamicFormDocumentDTO.getFormId()));
		dynamicFormDocumentDTO.setTabId(FormatPreserveCryptoUtil.decrypt(dynamicFormDocumentDTO.getTabId()));
		
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long loggedInEmployeeId = (Long) request.getSession()
				.getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		dynamicFormDocumentDTO.setEntityKey(String.valueOf(loggedInEmployeeId));
		EmployeeListFormPage employeeListFormPage = new EmployeeListFormPage();
		try {
			employeeListFormPage = employeeDetailLogic.updateDocTableRecord(dynamicFormDocumentDTO, companyId,
					loggedInEmployeeId);
			employeeListFormPage.setStatus(PayAsiaConstants.PAYASIA_SUCCESS);

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			employeeListFormPage.setStatus(PayAsiaConstants.PAYASIA_ERROR);

		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(employeeListFormPage, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/employeeDetail/downloadEmpDoc.html", method = RequestMethod.GET)
	public @ResponseBody byte[] downloadEmpDoc(@RequestParam(value = "attachmentId", required = true) Long recordId,
			@RequestParam(value = "seqNo", required = true) int seqNo,
			@RequestParam(value = "entityKey", required = true) Long entityKey, HttpServletRequest request,
			HttpServletResponse response) {

		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		DynamicFormTableDocumentDTO attachment = employeeDetailLogic.downloadEmpDoc(recordId, seqNo, companyId,
				entityKey);

		byte[] byteFile = attachment.getAttachmentBytes();

		response.reset();
		String mimeType = URLConnection.guessContentTypeFromName(attachment.getFileName());
		response.setContentType("application/" + mimeType);
		response.setContentLength(byteFile.length);
		String filename = attachment.getFileName();

		response.setHeader("Content-Disposition", "attachment;filename=" + filename);

		return byteFile;

	}

	@Override
	@RequestMapping(value = "/employee/employeeDetail/downloadViewProfileEmpDoc.html", method = RequestMethod.GET)
	public @ResponseBody byte[] downloadViewProfileEmpDoc(
			@RequestParam(value = "attachmentId", required = true) Long recordId,
			@RequestParam(value = "seqNo", required = true) int seqNo, HttpServletRequest request,
			HttpServletResponse response) {
		Long loggedInEmployeeId = (Long) request.getSession()
				.getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		DynamicFormTableDocumentDTO attachment = employeeDetailLogic.downloadEmpDoc(recordId, seqNo, companyId,
				loggedInEmployeeId);

		byte[] byteFile = attachment.getAttachmentBytes();

		response.reset();
		String mimeType = URLConnection.guessContentTypeFromName(attachment.getFileName());
		response.setContentType("application/" + mimeType);
		response.setContentLength(byteFile.length);
		String filename = attachment.getFileName();

		response.setHeader("Content-Disposition", "attachment;filename=" + filename);

		return byteFile;

	}

	@Override
	@RequestMapping(value = "/admin/employeeDetail/empTakeOwnership.html", method = RequestMethod.POST)
	@ResponseBody
	public String empTakeOwnership(@RequestParam(value = "tableId", required = true) Long tableId,
			@RequestParam(value = "seqNo", required = true) Integer seqNo,
			@RequestParam(value = "ownership", required = true) String ownership, HttpServletRequest request,
			HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		employeeDetailLogic.empTakeOwnership(tableId, companyId, seqNo, ownership);

		return "";
	}

	@Override
	@RequestMapping(value = "/employee/employeeDetail/empLoadGrid.html", method = RequestMethod.POST)
	@ResponseBody
	public String empLoadGrid(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "tId", required = true) Long tid,
			@RequestParam(value = "formId", required = true) Long formId,
			@RequestParam(value = "columnCount", required = true) int columnCount,
			@RequestParam(value = "fieldNames", required = true) String[] fieldNames,
			@RequestParam(value = "fieldTypes", required = true) String[] fieldTypes,
			@RequestParam(value = "fieldDictIds", required = true) String[] fieldDictIds,
			@RequestParam(value = "tableType", required = true) String tableType,
			@RequestParam(value = "sortOrder", required = true) String sortOrder,
			@RequestParam(value = "sortBy", required = true) String sortBy, Locale locale) {
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long loggedInEmployeeId = (Long) request.getSession()
				.getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		Long languageId = multilingualLogic.getLanguageId(locale.toString());
		if (tid != null && tid != 0) {
			/*ID DECRYPT*/
			formId = FormatPreserveCryptoUtil.decrypt(formId);
			EmployeeListFormPage employeeListFormPage = employeeDetailLogic.empTableRecordList(tid, formId, columnCount,
					fieldNames, fieldTypes, fieldDictIds, companyId, loggedInEmployeeId, tableType, sortOrder, sortBy,
					languageId);
			JsonConfig jsonConfig = new JsonConfig();
			JSONObject jsonObject = JSONObject.fromObject(employeeListFormPage, jsonConfig);

			try {

				return URLEncoder.encode(jsonObject.toString(), "UTF8");
			} catch (UnsupportedEncodingException unsupportedEncodingException) {
				LOGGER.error(unsupportedEncodingException.getMessage(), unsupportedEncodingException);
			}
			return null;

		} else {
			return null;
		}

	}

	@Override
	@RequestMapping(value ="/employee/employeeDetail/showHistory.html", method = RequestMethod.POST)
	@ResponseBody public String showHistory(
			@RequestParam(value = "tableId", required = true) Long tableId,
			@RequestParam(value = "seqNo", required = true) Integer seqNo,
			@RequestParam(value = "tableType", required = true) String tableType,
			@RequestParam(value = "formId", required = true) String formId,
			HttpServletRequest request, HttpServletResponse response) {
		EmployeeListFormPage importHistory =null;
		
		/*ID DECRYPT*/
		Long formID = FormatPreserveCryptoUtil.decrypt(Long.valueOf(formId));
		
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long empId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		if(PayAsiaConstants.TRUE.equalsIgnoreCase(employeeDetailLogic
				.isEnableEmployeeChangeWorkflow(companyId)))
		{
		 importHistory = employeeDetailLogic
				.getEmployeeDocHistory(companyId,empId, tableId, seqNo, tableType,formID,false);
		}
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject
				.fromObject(importHistory, jsonConfig);
		try {

			return URLEncoder.encode(jsonObject.toString(), "UTF8");
		} catch (UnsupportedEncodingException unsupportedEncodingException) {
			LOGGER.error(unsupportedEncodingException.getMessage(),
					unsupportedEncodingException);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/employee/employeeDetail/isEnableEmployeeChangeWorkflow.html", method = RequestMethod.POST)
	@ResponseBody
	public String isEnableEmployeeChangeWorkflow(HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		String isEnableEmployeeChangeWorkflow = employeeDetailLogic.isEnableEmployeeChangeWorkflow(companyId);
		return isEnableEmployeeChangeWorkflow;

	}

	@Override
	@RequestMapping(value = "/employee/employeeDetail/isAllowEmployeetouploaddocument.html", method = RequestMethod.POST)
	@ResponseBody
	public String isAllowEmployeetouploaddocument(HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		String isAllowEmployeetouploaddocument = employeeDetailLogic.isAllowEmployeetouploaddocument(companyId);
		return isAllowEmployeetouploaddocument;

	}

	@Override
	@RequestMapping(value = "/admin/employeeDetail/empGetPassword.html", method = RequestMethod.POST)
	@ResponseBody
	public String empGetPassword(@RequestParam(value = "empID", required = false) Long empID,
			HttpServletRequest request, HttpServletResponse response) {
		Long loggedInEmployeeId = (Long) request.getSession()
				.getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		String empPassword = null;

		/*ID DECRYPT*/
		empID = FormatPreserveCryptoUtil.decrypt(empID);
		
		Long userId = Long.parseLong(UserContext.getUserId());

		boolean employeePwd = employeeDetailLogic.isAdminAuthorizedForEmployee(empID, companyId, userId);

		if (employeePwd) {

			empPassword = employeeDetailLogic.empGetPassword(companyId, loggedInEmployeeId, empID);
			try {
				empPassword = URLEncoder.encode(empPassword, "UTF8");
			} catch (UnsupportedEncodingException uee) {
				LOGGER.error(uee.getMessage(), uee);
			}
		}
		return empPassword;
	}

	@Override
	@RequestMapping(value = "/admin/employeeDetail/isHideGetPassword.html", method = RequestMethod.POST)
	@ResponseBody
	public String isHideGetPassword(HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		String isHidePassword = employeeDetailLogic.isHideGetPassword(companyId);
		return isHidePassword;

	}

	@Override
	@RequestMapping(value = "/admin/employeeDetail/getClientAdminEditDeleteEmpStatus.html", method = RequestMethod.POST)
	@ResponseBody
	public String getClientAdminEditDeleteEmpStatus(HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		String clientAdminEditDeleteEmpStatus = employeeDetailLogic.getClientAdminEditDeleteEmpStatus(companyId);
		return clientAdminEditDeleteEmpStatus;

	}
	@Override
	@RequestMapping(value = "/admin/employeeDetail/showHistory.html", method = RequestMethod.POST)
	@ResponseBody public String showHistory(
			@RequestParam(value = "tableId", required = true) Long tableId,
			@RequestParam(value = "seqNo", required = true) Integer seqNo,
			@RequestParam(value = "tableType", required = true) String tableType,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long empId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		EmployeeListFormPage importHistory = employeeDetailLogic
				.getEmployeeDocHistory(companyId,empId, tableId, seqNo, tableType,null,true);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject
				.fromObject(importHistory, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF8");
		} catch (UnsupportedEncodingException unsupportedEncodingException) {
			LOGGER.error(unsupportedEncodingException.getMessage(),
					unsupportedEncodingException);
		}
		return null;
	}
	
	
	
	
	

}
