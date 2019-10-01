package com.payasia.web.controller.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.EmployeeFilterListForm;
import com.payasia.common.form.LeaveSchemeForm;
import com.payasia.common.form.LeaveSchemeResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.LeaveSchemeDAO;
import com.payasia.dao.LeaveSchemeTypeDAO;
import com.payasia.dao.bean.LeaveScheme;
import com.payasia.dao.bean.LeaveSchemeType;
import com.payasia.logic.LeaveSchemeLogic;
import com.payasia.web.controller.LeaveSchemeController;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@Controller
public class LeaveSchemeControllerImpl implements LeaveSchemeController {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(LeaveSchemeControllerImpl.class);

	@Resource
	LeaveSchemeLogic leaveSchemeLogic;

	@Resource
	MessageSource messageSource;
	
	@Resource
	LeaveSchemeDAO leaveSchemeDAO;
	
	@Resource
	LeaveSchemeTypeDAO leaveSchemeTypeDAO;

	@RequestMapping(value = "/admin/leaveScheme/viewLeaveScheme.html", method = RequestMethod.POST)
	@Override
	@ResponseBody
	public String viewLeaveScheme(
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "searchCondition", required = true) String searchCondition,
			@RequestParam(value = "searchText", required = true) String searchText) {

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		LeaveSchemeResponse leaveSchemeResponse = leaveSchemeLogic
				.viewLeaveScheme(companyId, searchCondition, searchText,
						pageDTO, sortDTO);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveSchemeResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/admin/leaveScheme/addLeaveScheme.html", method = RequestMethod.POST)
	@ResponseBody
	public String addLeaveScheme(
			@ModelAttribute(value = "leaveSchemeForm") LeaveSchemeForm leaveSchemeForm) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		String Status = leaveSchemeLogic.addLeaveScheme(companyId,
				leaveSchemeForm);
		return Status;
	}

	@Override
	@RequestMapping(value = "/admin/leaveScheme/deleteLeaveScheme.html", method = RequestMethod.POST)
	@ResponseBody
	public String deleteLeaveScheme(
			@RequestParam(value = "leaveSchemeId", required = true) Long leaveSchemeId) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		/* ID DECRYPT */
		leaveSchemeId= FormatPreserveCryptoUtil.decrypt(leaveSchemeId);
		String status;
		try {

			leaveSchemeLogic.deleteLeaveScheme(companyId, leaveSchemeId);
			status = "false";
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			status = "true";
		}
		return status;

	}

	@Override
	@RequestMapping(value = "/admin/leaveScheme/getLeaveScheme.html", method = RequestMethod.POST)
	@ResponseBody
	public String getLeaveScheme(
			@RequestParam(value = "leaveSchemeId", required = true) Long leaveSchemeId) {
		leaveSchemeId= FormatPreserveCryptoUtil.decrypt(leaveSchemeId);
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		LeaveSchemeForm leaveSchemeForm = leaveSchemeLogic.getLeaveScheme(
				companyId, leaveSchemeId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveSchemeForm,
				jsonConfig);

		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/admin/leaveScheme/getLeaveTypeLeaveScheme.html", method = RequestMethod.POST)
	@ResponseBody
	public String getLeaveTypeLeaveScheme(
			@RequestParam(value = "leaveSchemeId", required = true) Long leaveSchemeId,
			@RequestParam(value = "leaveTypeId", required = true) Long leaveTypeId) {
		/* ID DECRYPT */
		leaveSchemeId= FormatPreserveCryptoUtil.decrypt(leaveSchemeId);
		leaveTypeId= FormatPreserveCryptoUtil.decrypt(leaveTypeId);
		
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		LeaveSchemeForm leaveSchemeForm = leaveSchemeLogic
				.getLeaveTypeLeaveScheme(companyId, leaveSchemeId, leaveTypeId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveSchemeForm,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/admin/leaveScheme/configureLeaveScheme.html", method = RequestMethod.POST)
	@ResponseBody
	public String configureLeaveScheme(
			@ModelAttribute(value = "leaveSchemeForm") LeaveSchemeForm leaveSchemeForm) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		String Status = leaveSchemeLogic.configureLeaveScheme(leaveSchemeForm,
				companyId);
		return Status;
	}

	@Override
	@RequestMapping(value = "/admin/leaveScheme/configureLeaveSchemeTypeWorkFlow.html", method = RequestMethod.POST)
	@ResponseBody
	public String configureLeaveSchemeTypeWorkFlow(
			@ModelAttribute(value = "leaveSchemeForm") LeaveSchemeForm leaveSchemeForm) {
		
		leaveSchemeForm.setLeaveSchemeId(FormatPreserveCryptoUtil.decrypt(leaveSchemeForm.getLeaveSchemeId()));
		leaveSchemeForm.setLeaveSchemeTypeId(FormatPreserveCryptoUtil.decrypt(leaveSchemeForm.getLeaveSchemeTypeId()));
		
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		String Status = leaveSchemeLogic.configureLeaveSchemeTypeWorkFlow(
				leaveSchemeForm, companyId);
		return Status;
	}

	@Override
	@RequestMapping(value = "/admin/leaveScheme/leaveSchemeTypeForEdit.html", method = RequestMethod.POST)
	@ResponseBody
	public String getLeaveSchemeTypeForEdit(
			@RequestParam(value = "leaveSchemeTypeId", required = true) Long leaveSchemeTypeId) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		LeaveSchemeForm leaveSchemeForm = leaveSchemeLogic
				.getLeaveSchemeTypeForEdit(companyId, leaveSchemeTypeId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveSchemeForm,
				jsonConfig);

		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/leaveScheme/configureLeaveSchemeType.html", method = RequestMethod.POST)
	@ResponseBody
	public String configureLeaveSchemeType(
			@ModelAttribute(value = "leaveSchemeForm") LeaveSchemeForm leaveSchemeForm) {
		
		//leaveSchemeForm.setLeaveSchemeTypeId(FormatPreserveCryptoUtil.decrypt(leaveSchemeForm.getLeaveSchemeTypeId()));
		
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		LeaveScheme leaveScheme = leaveSchemeDAO.findSchemeByCompanyID(leaveSchemeForm.getLeaveSchemeId(), companyId);
		if(leaveScheme == null)
		{
			throw new PayAsiaSystemException("Authentication Exception");
		}
		LeaveSchemeResponse leaveSchemeResponse = leaveSchemeLogic
				.saveLeaveSchemeDetailTypes(leaveSchemeForm);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveSchemeResponse,
				jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/leaveScheme/getLeaveType.html", method = RequestMethod.POST)
	@ResponseBody
	public String getLeaveType(
			@RequestParam(value = "leaveSchemeId", required = true) Long leaveSchemeId) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		/* ID DECRYPT */
		leaveSchemeId = FormatPreserveCryptoUtil.decrypt(leaveSchemeId);
		LeaveSchemeResponse leaveSchemeResponse = new LeaveSchemeResponse();
		Set<LeaveSchemeForm> leaveTypeList = leaveSchemeLogic.getLeaveTypeList(
				companyId, leaveSchemeId);
		leaveSchemeResponse.setLeaveSchemeSet(leaveTypeList);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveSchemeResponse,
				jsonConfig);

		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/admin/leaveScheme/getLeaveTypeForCombo.html", method = RequestMethod.POST)
	@ResponseBody
	public String getLeaveTypeForCombo(
			@RequestParam(value = "leaveSchemeId", required = true) Long leaveSchemeId,
			@RequestParam(value = "leaveSchemeTypeId", required = true) Long leaveSchemeTypeId) {
		
		/* ID DECRYPT */
		leaveSchemeTypeId= FormatPreserveCryptoUtil.decrypt(leaveSchemeTypeId);
		leaveSchemeId= FormatPreserveCryptoUtil.decrypt(leaveSchemeId);
		
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		Set<LeaveSchemeForm> leaveTypeList = leaveSchemeLogic
				.getLeaveTypeListForCombo(companyId, leaveSchemeId,
						leaveSchemeTypeId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(leaveTypeList, jsonConfig);
		return jsonObject.toString();
	}

	@RequestMapping(value = "/admin/leaveScheme/viewLeaveType.html", method = RequestMethod.POST)
	@Override
	@ResponseBody
	public String viewLeaveType(
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "leaveSchemeId", required = true) Long leaveSchemeId) {
		
		/* ID DECRYPT */
		leaveSchemeId= FormatPreserveCryptoUtil.decrypt(leaveSchemeId);
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		LeaveSchemeResponse leaveSchemeResponse = leaveSchemeLogic
				.viewLeaveType(leaveSchemeId, companyId, pageDTO, sortDTO);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveSchemeResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@RequestMapping(value = "/admin/leaveScheme/viewAssignedEmployees.html", method = RequestMethod.POST)
	@Override
	@ResponseBody
	public String viewAssignedEmployees(
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "leaveSchemeId", required = true) Long leaveSchemeId) {
		/* ID DECRYPT */
		leaveSchemeId= FormatPreserveCryptoUtil.decrypt(leaveSchemeId);
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		LeaveSchemeResponse leaveSchemeResponse = leaveSchemeLogic
				.viewAssignedEmployees(leaveSchemeId, companyId, pageDTO,
						sortDTO);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveSchemeResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/admin/leaveScheme/editLeaveType.html", method = RequestMethod.POST)
	public @ResponseBody void editLeaveType(
			@RequestParam(value = "leaveSchemeTypeId", required = true) Long leaveSchemeTypeId,
			@ModelAttribute(value = "leaveSchemeForm") LeaveSchemeForm leaveSchemeForm) {
		/* ID DECRYPT */
		leaveSchemeTypeId= FormatPreserveCryptoUtil.decrypt(leaveSchemeTypeId);
        Long companyId= Long.parseLong(UserContext.getWorkingCompanyId());
        LeaveSchemeType leaveSchemeType = leaveSchemeTypeDAO.findLeaveSchemeTypeByCompId(leaveSchemeTypeId, companyId);
        if(leaveSchemeType == null)
        {
        	throw new PayAsiaSystemException("Authentication Exception");
        }
		leaveSchemeLogic.editLeaveType(leaveSchemeTypeId, leaveSchemeForm);
	}

	@Override
	@RequestMapping(value = "/admin/leaveScheme/getLeaveTypeForEdit.html", method = RequestMethod.POST)
	@ResponseBody
	public String getLeaveTypeForEdit(
			@RequestParam(value = "leaveSchemeTypeId", required = true) Long leaveSchemeTypeId) {
		/* ID DECRYPT */
		leaveSchemeTypeId = FormatPreserveCryptoUtil.decrypt(leaveSchemeTypeId);
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		 LeaveSchemeType leaveSchemeType = leaveSchemeTypeDAO.findLeaveSchemeTypeByCompId(leaveSchemeTypeId, companyId);
	        if(leaveSchemeType == null)
	        {
	        	throw new PayAsiaSystemException("Authentication Exception");
	        }
		LeaveSchemeForm leaveSchemeForm = leaveSchemeLogic
				.getLeaveTypeForEdit(leaveSchemeTypeId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveSchemeForm,
				jsonConfig);

		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/admin/leaveScheme/addLeaveType.html", method = RequestMethod.POST)
	@ResponseBody
	public String addLeaveType(
			@RequestParam(value = "leaveTypeId", required = true) String[] leaveTypeId,
			@RequestParam(value = "leaveSchemeId", required = true) Long leaveSchemeId,
			Locale locale) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		LeaveSchemeResponse leaveSchemeResponse = leaveSchemeLogic
				.addLeaveType(leaveTypeId, leaveSchemeId, companyId);
		try {
			leaveSchemeResponse.setMessageKey(URLEncoder.encode(messageSource
					.getMessage(leaveSchemeResponse.getMessageKey(),
							new Object[] {}, locale), "UTF-8"));

			JsonConfig jsonConfig = new JsonConfig();
			JSONObject jsonObject = JSONObject.fromObject(leaveSchemeResponse,
					jsonConfig);

			return jsonObject.toString();
		} catch (UnsupportedEncodingException unsupportedEncodingException) {

			LOGGER.error(unsupportedEncodingException.getMessage(),
					unsupportedEncodingException);
			throw new PayAsiaSystemException(
					unsupportedEncodingException.getMessage(),
					unsupportedEncodingException);
		}

	}

	@Override
	@RequestMapping(value = "/admin/leaveScheme/deleteLeaveType.html", method = RequestMethod.POST)
	@ResponseBody
	public String deleteLeaveType(
			@RequestParam(value = "leaveSchemeTypeId", required = true) Long leaveSchemeTypeId,
			@RequestParam(value = "leaveSchemeId", required = true) Long leaveSchemeId,
			Locale locale) {

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = null;
		LeaveSchemeResponse leaveSchemeResponse = new LeaveSchemeResponse();
		try {
			Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
			leaveSchemeResponse = leaveSchemeLogic.deleteLeaveType(
					leaveSchemeTypeId, leaveSchemeId, companyId);

			leaveSchemeResponse.setMessageKey(URLEncoder.encode(messageSource
					.getMessage(leaveSchemeResponse.getMessageKey(),
							new Object[] {}, locale), "UTF-8"));
			jsonObject = JSONObject.fromObject(leaveSchemeResponse, jsonConfig);
			return jsonObject.toString();
		} catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
			try {
				leaveSchemeResponse.setMessageKey(URLEncoder.encode(
						messageSource.getMessage(
								"payasia.leave.type.cannot.be.deleted",
								new Object[] {}, locale), "UTF-8"));
			} catch (UnsupportedEncodingException e) {

				LOGGER.error(e.getMessage(), e);
			} catch (NoSuchMessageException e) {

				LOGGER.error(e.getMessage(), e);
			}
			jsonObject = JSONObject.fromObject(leaveSchemeResponse, jsonConfig);
			return jsonObject.toString();
		}

	}

	@Override
	@RequestMapping(value = "/admin/leaveScheme/getEmployeeFilterList.html", method = RequestMethod.POST)
	@ResponseBody
	public String getEmployeeFilterList() {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		long employeeId = Long.parseLong(UserContext.getUserId());
		boolean isAdmin = false;
		List<EmployeeFilterListForm> filterList = leaveSchemeLogic
				.getEmployeeFilterList(companyId, employeeId, isAdmin);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(filterList, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/leaveScheme/getEmployeeFilterListForAdmin.html", method = RequestMethod.POST)
	@ResponseBody
	public String getEmployeeFilterListForAdmin() {
		long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		long employeeId = Long.parseLong(UserContext.getUserId());
		boolean isAdmin = true;
		List<EmployeeFilterListForm> filterList = leaveSchemeLogic
				.getEmployeeFilterList(companyId, employeeId, isAdmin);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(filterList, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/leaveScheme/saveEmployeeFilterList.html", method = RequestMethod.POST)
	@ResponseBody
	public String saveEmployeeFilterList(
			@RequestParam(value = "metaData", required = true) String metaData,
			@RequestParam(value = "leaveSchemeTypeId", required = true) Long leaveSchemeTypeId) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		//leaveSchemeTypeId=FormatPreserveCryptoUtil.decrypt(leaveSchemeTypeId);
		LeaveSchemeType leaveSchemeType = leaveSchemeTypeDAO.findLeaveSchemeTypeByCompId(leaveSchemeTypeId, companyId);
		if(leaveSchemeType == null)
		{
			throw new PayAsiaSystemException("Authentication Exception");
		}
		String shortListStatus = null;
		try{
			final String decodedMetaData = URLDecoder.decode(metaData, "UTF-8");
			shortListStatus = leaveSchemeLogic.saveEmployeeFilterList(decodedMetaData, leaveSchemeTypeId, companyId);
		}catch(Exception ex){
			LOGGER.error(PayAsiaConstants.PAYASIA_ERROR);
		}
		return shortListStatus;
	}

	@Override
	@RequestMapping(value = "/admin/leaveScheme/getEditEmployeeFilterList.html", method = RequestMethod.POST)
	@ResponseBody
	public String getEditEmployeeFilterList(
			@RequestParam(value = "leaveSchemeTypeId", required = true) Long leaveSchemeTypeId) {
		//leaveSchemeTypeId = FormatPreserveCryptoUtil.decrypt(leaveSchemeTypeId);
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		List<EmployeeFilterListForm> filterList = leaveSchemeLogic
				.getEditEmployeeFilterList(leaveSchemeTypeId, companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(filterList, jsonConfig);
		return jsonObject.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.ManageUserController#deleteFilter(java.lang
	 * .Long)
	 */
	@Override
	@RequestMapping(value = "/admin/leaveScheme/deleteFilter.html", method = RequestMethod.POST)
	public void deleteFilter(
			@RequestParam(value = "filterId", required = true) Long filterId,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		leaveSchemeLogic.deleteFilter(filterId, companyId);
	}
	
	
	
	
	

	@Override
	@RequestMapping(value = "/admin/leaveScheme/getAppCodeListForProration.html", method = RequestMethod.POST)
	@ResponseBody
	public String getAppCodeListForProration(
			@RequestParam(value = "distMethodName", required = true) String distMethodName) {
		List<LeaveSchemeForm> leaveSchemeFormList = null;
		if ("Earned".equalsIgnoreCase(distMethodName)) {
			leaveSchemeFormList = leaveSchemeLogic
					.getAppcodeListForEarnedProrationMeth();
		} else {
			leaveSchemeFormList = leaveSchemeLogic
					.getAppcodeListForProrationMeth();
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(leaveSchemeFormList,
				jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/leaveScheme/callRedistributeProc.html", method = RequestMethod.POST)
	@ResponseBody
	public String callRedistributeProc(
			@RequestParam(value = "leaveSchemeTypeId", required = true) Long leaveSchemeTypeId,
			Locale locale) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		LeaveSchemeType leaveSchemeType = leaveSchemeTypeDAO.findLeaveSchemeTypeByCompId(leaveSchemeTypeId, companyId);
		if(leaveSchemeType == null)
		{
			throw new PayAsiaSystemException("Authentication Exception");
		}
		LeaveSchemeResponse leaveSchemeResponse = leaveSchemeLogic
				.callRedistributeProc(leaveSchemeTypeId);

		if (!leaveSchemeResponse.getLeaveSchemeProc().getStatus()) {
			if (leaveSchemeResponse.getLeaveSchemeProc().getErrorMsg() != null) {
				leaveSchemeResponse.getLeaveSchemeProc().setErrorMsg(
						messageSource.getMessage(leaveSchemeResponse
								.getLeaveSchemeProc().getErrorMsg(),
								new Object[] {}, locale));
			}

		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveSchemeResponse,
				jsonConfig);

		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/employee/leaveScheme/getEmployeeFilterList.html", method = RequestMethod.POST)
	@ResponseBody
	public String getEmployeeFilterListEmp() {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		Long employeeId = Long.parseLong(UserContext.getUserId());
		boolean isAdmin = false;
		List<EmployeeFilterListForm> filterList = leaveSchemeLogic
				.getEmployeeFilterList(companyId, employeeId, isAdmin);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(filterList, jsonConfig);
		return jsonObject.toString();
	}
	
	@Override
	@RequestMapping(value = "/employee/leaveScheme/getEmployeeFilterListForAdmin.html", method = RequestMethod.POST)
	@ResponseBody
	public String getEmployeeFilterListForAdminEmp() {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		Long employeeId = Long.parseLong(UserContext.getUserId());
		boolean isAdmin = true;
		List<EmployeeFilterListForm> filterList = leaveSchemeLogic
				.getEmployeeFilterList(companyId, employeeId, isAdmin);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(filterList, jsonConfig);
		return jsonObject.toString();
	}
		
}
