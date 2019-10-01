package com.payasia.web.controller.impl;

/**
 * @author vivekjain
 *
 */
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

import com.payasia.common.form.DynamicFormSectionForm;
import com.payasia.common.form.DynamicFormSectionFormResponse;
import com.payasia.common.form.EmployeeFilterListForm;
import com.payasia.common.form.ManageUserAddCompanyForm;
import com.payasia.common.form.ManageUserAddCompanyResponseForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.PrivilageResponseForm;
import com.payasia.common.form.SortCondition;
import com.payasia.common.form.UserResponseForm;
import com.payasia.common.form.UserRoleForm;
import com.payasia.common.form.UserRoleListForm;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.logic.GeneralLogic;
import com.payasia.logic.ManageUserLogic;
import com.payasia.web.controller.ManageUserController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * The Class ManageUserControllerImpl.
 */

@Controller
@RequestMapping(value = "/admin/manageRoles")
public class ManageUserControllerImpl implements ManageUserController {
	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(ManageUserControllerImpl.class);

	/** The manage user logic. */
	@Resource
	ManageUserLogic manageUserLogic;

	/** The message source. */
	@Autowired
	private MessageSource messageSource;
	@Resource
	GeneralLogic generalLogic;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.ManageUserController#viewAllRole(java.lang
	 * .String, java.lang.String, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/viewAllRole.html", method = RequestMethod.POST)
	@ResponseBody public String viewAllRole(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			HttpServletRequest request, HttpServletResponse response) {

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		UserRoleForm userRoleResponseForm = manageUserLogic.viewAllRole(
				companyId, sortDTO);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(userRoleResponseForm,
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
	 * com.payasia.web.controller.ManageUserController#viewPrivilage(java.lang
	 * .String, java.lang.String, java.lang.Long)
	 */
	@Override
	@RequestMapping(value = "/viewPrivilage.html", method = RequestMethod.POST)
	@ResponseBody public String viewPrivilage(
			@RequestParam(value = "searchCondition", required = false) String searchCondition,
			@RequestParam(value = "searchText", required = false) String searchText,
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "roleId", required = true) Long roleId) {

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		/*ID DYCRYPT*/
		roleId = FormatPreserveCryptoUtil.decrypt(roleId);
		PrivilageResponseForm response = manageUserLogic.viewPrivilage(roleId,
				searchCondition, searchText, sortDTO);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(response, jsonConfig);
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
	 * com.payasia.web.controller.ManageUserController#viewPrivilageUser(java
	 * .lang.String, java.lang.String, java.lang.Long,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/viewPrivilageUser.html", method = RequestMethod.POST)
	@ResponseBody public String viewPrivilageUser(
			@RequestParam(value = "searchCondition", required = false) String searchCondition,
			@RequestParam(value = "searchText", required = false) String searchText,
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "roleId", required = true) Long roleId,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			HttpServletRequest request, HttpServletResponse response) {

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		/*ID DYCRYPT */
		roleId = FormatPreserveCryptoUtil.decrypt(roleId);
		
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		UserResponseForm userResponseForm = manageUserLogic.viewPrivilageUser(
				companyId, employeeId, roleId, sortDTO, pageDTO,
				searchCondition, searchText);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(userResponseForm,
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
	 * com.payasia.web.controller.ManageUserController#saveUserRoleAndPrivilage
	 * (java.lang.String, java.lang.String[], java.lang.String,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, java.util.Locale)
	 */
	@Override
	@RequestMapping(value = "/saveUserRoleAndPrivilage.html", method = RequestMethod.POST)
	@ResponseBody public String saveUserRoleAndPrivilage(
			@RequestParam(value = "roleId", required = true) String roleId,
			@RequestParam(value = "privilageId", required = true) String[] privilageId,
			@RequestParam(value = "sectionIds", required = true) String[] sectionIds,
			@RequestParam(value = "userIdAndCompanyName", required = true) String userIdAndCompanyName,
			@RequestParam(value = "notSelectedUserIds", required = true) String notSelectedUserIds,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		String saveStatus = manageUserLogic.saveUserRoleAndPrivilage(companyId,
				roleId, privilageId, sectionIds, userIdAndCompanyName,
				notSelectedUserIds);

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
	 * com.payasia.web.controller.ManageUserController#saveRole(com.payasia.
	 * common.form.UserRoleListForm,
	 * org.springframework.validation.BindingResult,
	 * org.springframework.ui.ModelMap, javax.servlet.http.HttpServletRequest,
	 * java.util.Locale)
	 */
	@Override
	@RequestMapping(value = "/saveRole.html", method = RequestMethod.POST)
	@ResponseBody public String saveRole(
			@ModelAttribute("userRoleListForm") UserRoleListForm userRoleListForm,
			HttpServletRequest request, Locale locale) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		String addRoleStatus = manageUserLogic.saveRole(userRoleListForm,
				companyId);

		try {
			addRoleStatus = URLEncoder.encode(messageSource.getMessage(
					addRoleStatus, new Object[] {}, locale), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		} catch (NoSuchMessageException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return addRoleStatus;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.ManageUserController#copyRole(com.payasia.
	 * common.form.UserRoleListForm,
	 * org.springframework.validation.BindingResult,
	 * org.springframework.ui.ModelMap, javax.servlet.http.HttpServletRequest,
	 * java.util.Locale)
	 */
	@Override
	@RequestMapping(value = "/copyRole.html", method = RequestMethod.POST)
	@ResponseBody public String copyRole(
			@ModelAttribute("userRoleListForm") UserRoleListForm userRoleListForm,
			HttpServletRequest request, Locale locale) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		/*ID DYCRYPT */
		userRoleListForm.setCopyRoleId(FormatPreserveCryptoUtil.decrypt(userRoleListForm.getCopyRoleId()));
		String copyRoleStatus = manageUserLogic.copyRole(userRoleListForm,
				companyId);
		try {
			copyRoleStatus = URLEncoder.encode(messageSource.getMessage(
					copyRoleStatus, new Object[] {}, locale), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		} catch (NoSuchMessageException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return copyRoleStatus;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.ManageUserController#deleteRole(java.lang.
	 * Long, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, java.util.Locale)
	 */
	@Override
	@RequestMapping(value = "/deleteRole.html", method = RequestMethod.POST)
	@ResponseBody public String deleteRole(
			@RequestParam(value = "roleId", required = true) Long roleId,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		/*ID DYCRYPT */
		roleId = FormatPreserveCryptoUtil.decrypt(roleId);
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		String roleMappingStatus = manageUserLogic
				.deleteRole(companyId, roleId);
		try {
			roleMappingStatus = URLEncoder.encode(messageSource.getMessage(
					roleMappingStatus, new Object[] {}, locale), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		} catch (NoSuchMessageException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}

		return roleMappingStatus;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.ManageUserController#getCompanyIsPayAsia(javax
	 * .servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/getCompanyIsPayAsia.html", method = RequestMethod.POST)
	@ResponseBody public String getCompanyIsPayAsia(HttpServletRequest request,
			HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		String companyIsPayAsia = manageUserLogic
				.getCompanyIsPayAsia(companyId);
		return companyIsPayAsia;

	}

	@Override
	@RequestMapping(value = "/isPayAsiaUserAdmin.html", method = RequestMethod.POST)
	@ResponseBody public String isPayAsiaUserAdmin(HttpServletRequest request,
			HttpServletResponse response) {
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		 
		 
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		String isPayAsiaUserAdmin = manageUserLogic.isPayAsiaUserAdmin(
				employeeId, companyId);
		return isPayAsiaUserAdmin;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.ManageUserController#getCompanyList(java.lang
	 * .String, java.lang.String, java.lang.Boolean,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/getCompanyList.html", method = RequestMethod.POST)
	@ResponseBody public String getCompanyList(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "isCompanyPayasia", required = true) Boolean isCompanyPayasia,
			@RequestParam(value = "roleId", required = true) Long roleId,
			@RequestParam(value = "userId", required = true) Long userId,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		String companyName = (String) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_NAME);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		/*
		 * ID DYCRYPT
		 * */
		userId = FormatPreserveCryptoUtil.decrypt(userId);
		roleId = FormatPreserveCryptoUtil.decrypt(roleId);
		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		ManageUserAddCompanyResponseForm responseForm = manageUserLogic
				.getCompanyList(sortDTO, isCompanyPayasia, companyId,
						companyName, employeeId, roleId, userId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(responseForm, jsonConfig);
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
	 * com.payasia.web.controller.ManageUserController#getAssignedCompanyList
	 * (java.lang.Long, java.lang.Long, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, java.util.Locale)
	 */
	@Override
	@RequestMapping(value = "/getAssignedCompanyList.html", method = RequestMethod.POST)
	@ResponseBody public String getAssignedCompanyList(
			@RequestParam(value = "roleId", required = true) Long roleId,
			@RequestParam(value = "employeeId", required = true) Long employeeId,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		/*
		 * ID DYCRYPT
		 * */
		employeeId = FormatPreserveCryptoUtil.decrypt(employeeId);
		roleId = FormatPreserveCryptoUtil.decrypt(roleId);
		List<ManageUserAddCompanyForm> companyList = manageUserLogic
				.getAssignedCompanyList(companyId, roleId, employeeId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(companyList, jsonConfig);
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
	 * com.payasia.web.controller.ManageUserController#getEmployeeFilterList
	 * (java.lang.Long, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, java.util.Locale)
	 */
	@Override
	@RequestMapping(value = "/getEmployeeFilterList.html", method = RequestMethod.POST)
	@ResponseBody public String getEmployeeFilterList(
			@RequestParam(value = "companyId", required = true) Long companyId,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		
		List<EmployeeFilterListForm> filterList = manageUserLogic
				.getEmployeeFilterList(companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(filterList, jsonConfig);
		return jsonObject.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.ManageUserController#getEditEmployeeFilterList
	 * (java.lang.Long, java.lang.Long, java.lang.Long,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, java.util.Locale)
	 */
	@Override
	@RequestMapping(value = "/getEditEmployeeFilterList.html", method = RequestMethod.POST)
	@ResponseBody public String getEditEmployeeFilterList(
			@RequestParam(value = "employeeId", required = true) Long employeeId,
			@RequestParam(value = "roleId", required = true) Long roleId,
			@RequestParam(value = "companyId", required = true) Long companyId,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		
		/*
		 * ID DYCRYPT
		 * */
		employeeId = FormatPreserveCryptoUtil.decrypt(employeeId);
		roleId = FormatPreserveCryptoUtil.decrypt(roleId);
		
		List<EmployeeFilterListForm> filterList = manageUserLogic
				.getEditEmployeeFilterList(employeeId, roleId, companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(filterList, jsonConfig);
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
	 * com.payasia.web.controller.ManageUserController#deleteFilter(java.lang
	 * .Long)
	 */
	@Override
	@RequestMapping(value = "/deleteFilter.html", method = RequestMethod.POST)
	public void deleteFilter(
			@RequestParam(value = "filterId", required = true) Long filterId) {
		manageUserLogic.deleteFilter(filterId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.ManageUserController#saveEmployeeFilterList
	 * (java.lang.String, java.lang.Long, java.lang.Long, java.lang.Long,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, java.util.Locale)
	 */
	@Override
	@RequestMapping(value = "/saveEmployeeFilterList.html", method = RequestMethod.POST)
	@ResponseBody public String saveEmployeeFilterList(
			@RequestParam(value = "metaData", required = true) String metaData,
			@RequestParam(value = "employeeId", required = true) Long employeeId,
			@RequestParam(value = "roleId", required = true) Long roleId,
			@RequestParam(value = "companyId", required = true) Long companyId,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		
		String shortListStatus = null;
		/*
		 * ID DYCRYPT
		 * */
		employeeId = FormatPreserveCryptoUtil.decrypt(employeeId);
		roleId = FormatPreserveCryptoUtil.decrypt(roleId);
		
		try {
			
			final String decodeMetaData = URLDecoder.decode(metaData, "UTF-8");
			
			shortListStatus = manageUserLogic.saveEmployeeFilterList(
					decodeMetaData, employeeId, roleId, companyId);
			if (!shortListStatus.equals("0")) {
				shortListStatus = URLEncoder.encode(messageSource.getMessage(
						shortListStatus, new Object[] {}, locale), "UTF-8");
			}

		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		} catch (NoSuchMessageException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return shortListStatus;

	}

	@Override
	@RequestMapping(value = "/getAdvanceFilterComboHashmap.html", method = RequestMethod.POST)
	@ResponseBody public String getAdvanceFilterComboHashmap(
			@RequestParam(value = "companyId", required = true) Long companyId,
			HttpServletRequest request, HttpServletResponse response) {

		Map<String, String> advanceFilterCombosHashMap = generalLogic
				.getEmployeeFilterComboList(companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(
				advanceFilterCombosHashMap, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/viewSectionName.html", method = RequestMethod.POST)
	@ResponseBody public String viewSectionName(
			@RequestParam(value = "searchCondition", required = false ) String searchCondition,
			@RequestParam(value = "searchText", required = false) String searchText,
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "roleId", required = true) Long roleId,
			HttpServletRequest request) {

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		
		/*ID DYCRYPT*/
		roleId = FormatPreserveCryptoUtil.decrypt(Long.valueOf(roleId));

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		DynamicFormSectionFormResponse response = manageUserLogic
				.viewSectionName(roleId, companyId, sortDTO, searchCondition,
						searchText);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(response, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/viewSectionNameByCompany.html", method = RequestMethod.POST)
	@ResponseBody public String viewSectionNameByCompany(
			@RequestParam(value = "searchCondition", required = false) String searchCondition,
			@RequestParam(value = "searchText", required = false) String searchText,
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "roleId", required = true) Long roleId,
			@RequestParam(value = "companyId", required = true) Long companyId,
			HttpServletRequest request) {

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		DynamicFormSectionFormResponse response = manageUserLogic
				.viewSectionName(roleId, companyId, sortDTO, searchCondition,
						searchText);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(response, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/saveRolePrivileges.html", method = RequestMethod.POST)
	@ResponseBody public String saveRolePrivileges(
			@RequestParam(value = "roleId", required = true) String roleId,
			@RequestParam(value = "privilageIds", required = true) String[] privilageIds,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		
		/*ID DYCRYPT*/
		Long roleID = FormatPreserveCryptoUtil.decrypt(Long.valueOf(roleId));
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		String saveStatus = manageUserLogic.saveRolePrivileges(companyId,
				String.valueOf(roleID), privilageIds);

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

	@Override
	@RequestMapping(value = "/saveRoleSections.html", method = RequestMethod.POST)
	@ResponseBody public String saveRoleSections(
			@RequestParam(value = "roleId", required = true) String roleId,
			@RequestParam(value = "sectionIds", required = true) String[] sectionIds,
			@RequestParam(value = "overrideSection", required = true) boolean overrideSection,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		
		/*ID DYCRYPT*/
		Long roleID = FormatPreserveCryptoUtil.decrypt(Long.valueOf(roleId));
		
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		String saveStatus = manageUserLogic.saveRoleSections(companyId, String.valueOf(roleID),
				sectionIds, overrideSection);

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

	@Override
	@RequestMapping(value = "/saveEmployeeRoleWithDefaultCompany.html", method = RequestMethod.POST)
	@ResponseBody public String saveEmployeeRoleWithDefaultCompany(
			@RequestParam(value = "roleId", required = true) String roleId,
			@RequestParam(value = "userIds", required = true) String[] userIds,
			@RequestParam(value = "allUserIds", required = true) String[] allUserIds,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		
		/*ID DYCRYPT*/
		Long roleID = FormatPreserveCryptoUtil.decrypt(Long.valueOf(roleId));
		List<String> listOfUserID = new ArrayList<String>();
		for(String emplo : userIds) {
			listOfUserID.add(String.valueOf(FormatPreserveCryptoUtil.decrypt(Long.parseLong(emplo))));
		}
		
		List<String> listOfAllUserIds = new ArrayList<String>();
		for(String emplo : allUserIds) {
			listOfAllUserIds.add(String.valueOf(FormatPreserveCryptoUtil.decrypt(Long.parseLong(emplo))));
		}
		
		userIds = listOfUserID.toArray(new String[listOfUserID.size()]);
		allUserIds = listOfAllUserIds.toArray(new String[listOfAllUserIds.size()]);
	   /*ID DYRYPT END*/		
		
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		String saveStatus = manageUserLogic.saveEmployeeRoleWithDefaultCompany(
				companyId, String.valueOf(roleID), userIds, allUserIds);

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

	@Override
	@RequestMapping(value = "/saveEmployeeRoleWithAssignCompany.html", method = RequestMethod.POST)
	@ResponseBody public String saveEmployeeRoleWithAssignCompany(
			@RequestParam(value = "roleId", required = true) String roleId,
			@RequestParam(value = "userId", required = true) Long userId,
			@RequestParam(value = "companyIds", required = true) String[] companyIds,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		/*
		 * ID DYCRYPT
		 * */
		userId = FormatPreserveCryptoUtil.decrypt(userId);
		Long roleID = FormatPreserveCryptoUtil.decrypt(Long.valueOf(roleId));
		
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		String saveStatus = manageUserLogic.saveEmployeeRoleWithAssignCompany(
				companyId, String.valueOf(roleID), userId, companyIds);

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

	@Override
	@RequestMapping(value = "/saveCompanySection.html", method = RequestMethod.POST)
	@ResponseBody public String saveCompanySection(
			@RequestParam(value = "roleId", required = true) Long roleId,
			@RequestParam(value = "userId", required = true) Long userId,
			@RequestParam(value = "companyId", required = true) Long companyId,
			@RequestParam(value = "sectionIds", required = true) String[] sectionIds,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		/*
		 * ID DYCRYPT
		 * */
		userId = FormatPreserveCryptoUtil.decrypt(userId);
		roleId = FormatPreserveCryptoUtil.decrypt(roleId);
		
		String saveStatus = manageUserLogic.saveCompanySection(roleId,
				companyId, userId, sectionIds);

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

	@Override
	@RequestMapping(value = "/getAssignCompanySection.html", method = RequestMethod.POST)
	@ResponseBody public String getAssignCompanySection(
			@RequestParam(value = "roleId", required = true) Long roleId,
			@RequestParam(value = "userId", required = true) Long userId,
			@RequestParam(value = "companyId", required = true) Long companyId) {
		/*
		 * ID DYCRYPT
		 * */
		userId = FormatPreserveCryptoUtil.decrypt(userId);
		roleId = FormatPreserveCryptoUtil.decrypt(roleId);
		List<DynamicFormSectionForm> formIdList = manageUserLogic
				.getAssignCompanySection(roleId, userId, companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(formIdList, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/isSeletedCompanyAssignToUser.html", method = RequestMethod.POST)
	@ResponseBody public String isSeletedCompanyAssignToUser(
			@RequestParam(value = "roleId", required = true) Long roleId,
			@RequestParam(value = "userId", required = true) Long userId,
			@RequestParam(value = "companyId", required = true) Long companyId,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		/*
		 * ID DYCRYPT
		 * */
		userId = FormatPreserveCryptoUtil.decrypt(userId);
		roleId = FormatPreserveCryptoUtil.decrypt(roleId);
		String saveStatus = manageUserLogic.isSeletedCompanyAssignToUser(
				roleId, userId, companyId);

		return saveStatus;
	}
}
