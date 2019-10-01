package com.payasia.web.controller.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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

import com.payasia.common.dto.CompanyModuleDTO;
import com.payasia.common.form.EmployeeListForm;
import com.payasia.common.form.LeaveReviewerResponseForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.form.WorkFlowDelegateForm;
import com.payasia.common.form.WorkFlowDelegateResponse;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.logic.WorkFlowDelegateLogic;
import com.payasia.web.controller.WorkFlowDelegateController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * The Class WorkFlowDelegateControllerImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Controller
/*@RequestMapping(value = { "/employee/workFlowDelegate",
		"/admin/workFlowDelegate" })*/
public class WorkFlowDelegateControllerImpl implements
		WorkFlowDelegateController {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(WorkFlowDelegateControllerImpl.class);

	/** The work flow delegate logic. */
	@Resource
	WorkFlowDelegateLogic workFlowDelegateLogic;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.WorkFlowDelegateController#saveWorkFlowDelegate
	 * (com.payasia.common.form.WorkFlowDelegateForm,
	 * org.springframework.validation.BindingResult,
	 * org.springframework.ui.ModelMap, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	@RequestMapping(value ={ "/employee/workFlowDelegate/addWorkFlowDelegate.html",
	"/admin/workFlowDelegate/addWorkFlowDelegate.html" } , method = RequestMethod.POST)
	public void saveWorkFlowDelegate(
			@ModelAttribute(value = "workFlowDelegateForm") WorkFlowDelegateForm workFlowDelegateForm,
			BindingResult result, ModelMap model, HttpServletRequest request) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		  boolean flag=delegateIdCheck(workFlowDelegateForm.getUserId(),workFlowDelegateForm.getDelegateToId());
          if(flag) {
		       workFlowDelegateLogic.saveWorkFlowDelegate(workFlowDelegateForm,companyId);
          }
          else {
        	  LOGGER.error("Work Flow Delegate cant't be same !!! ");
          }

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.WorkFlowDelegateController#deleteWorkFlowDelegate
	 * (long)
	 */
	@Override
	@RequestMapping(value = { "/employee/workFlowDelegate/deleteWorkFlowDelegate.html",
	"/admin/workFlowDelegate/deleteWorkFlowDelegate.html" } , method = RequestMethod.POST)
	public void deleteWorkFlowDelegate(
			@RequestParam(value = "workflowDelegateId", required = true) long workflowDelegateId,
			HttpServletRequest request) {
		/* ID DYCRYPT * */
		workflowDelegateId = FormatPreserveCryptoUtil.decrypt(workflowDelegateId);
		
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		workFlowDelegateLogic.deleteWorkFlowDelegate(workflowDelegateId,
				companyId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.WorkFlowDelegateController#getWorkFlowDelegateData
	 * (long, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	@RequestMapping(value =  { "/employee/workFlowDelegate/getWorkFlowDelegateData.html",
	"/admin/workFlowDelegate/getWorkFlowDelegateData.html" }, method = RequestMethod.POST)
	public @ResponseBody
	String getWorkFlowDelegateData(
			@RequestParam(value = "workflowDelegateId", required = true) long workflowDelegateId,
			HttpServletRequest request) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		/*  ID DYCRYPT */
		workflowDelegateId = FormatPreserveCryptoUtil.decrypt(workflowDelegateId);
		
		WorkFlowDelegateForm workFlowDelegateForm = workFlowDelegateLogic
				.getWorkFlowDelegateData(workflowDelegateId, companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(workFlowDelegateForm,
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
	 * com.payasia.web.controller.WorkFlowDelegateController#updateWorkFlowDelegate
	 * (com.payasia.common.form.WorkFlowDelegateForm, long,
	 * javax.servlet.http.HttpServletRequest)
	 */
	@Override
	@RequestMapping(value = { "/employee/workFlowDelegate/editWorkFlowDelegate.html",
	"/admin/workFlowDelegate/editWorkFlowDelegate.html" } , method = RequestMethod.POST)
	public void updateWorkFlowDelegate(
			@ModelAttribute(value = "workFlowDelegateForm") WorkFlowDelegateForm workFlowDelegateForm,
			@RequestParam(value = "workflowDelegateId", required = true) long workflowDelegateId,
			HttpServletRequest request) {
		
		 /* ID DYCRYPT */
		workflowDelegateId = FormatPreserveCryptoUtil.decrypt(workflowDelegateId);
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		  boolean flag=delegateIdCheck(workFlowDelegateForm.getUserId(),workFlowDelegateForm.getDelegateToId());
          if(flag) {
		       workFlowDelegateLogic.updateWorkFlowDelegate(workFlowDelegateForm,workflowDelegateId, companyId);
          }else{
        	  
        	  LOGGER.error("Work Flow Delegate cant't be same !!! ");
          }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.WorkFlowDelegateController#viewWorkFlowDelegate
	 * (java.lang.String, java.lang.String, int, int, java.lang.String,
	 * java.lang.String, java.lang.String,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value =  { "/employee/workFlowDelegate/viewWorkFlowDelegate.html",
	"/admin/workFlowDelegate/viewWorkFlowDelegate.html" }, method = RequestMethod.POST)
	public @ResponseBody
	String viewWorkFlowDelegate(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "criteria", required = true) String criteria,
			@RequestParam(value = "keyword", required = true) String keyword,
			@RequestParam(value = "workFlowType", required = true) String workFlowType,
			HttpServletRequest request, HttpServletResponse response) {

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		CompanyModuleDTO companyModuleDTO = new CompanyModuleDTO();
		companyModuleDTO
				.setHasClaimModule((boolean) request.getSession().getAttribute(
						PayAsiaSessionAttributes.COMPANY_HAS_CLAIM_MODULE));
		companyModuleDTO
				.setHasHrisModule((boolean) request.getSession().getAttribute(
						PayAsiaSessionAttributes.COMPANY_HAS_HRIS_MODULE));
		companyModuleDTO
				.setHasLeaveModule((boolean) request.getSession().getAttribute(
						PayAsiaSessionAttributes.COMPANY_HAS_LEAVE_MODULE));

		companyModuleDTO
				.setHasLundinTimesheetModule((boolean) request
						.getSession()
						.getAttribute(
								PayAsiaSessionAttributes.COMPANY_HAS_LUNDIN_TIMESHEET_MODULE));
		companyModuleDTO
				.setHasLionTimesheetModule((boolean) request
						.getSession()
						.getAttribute(
								PayAsiaSessionAttributes.COMPANY_HAS_LION_TIMESHEET_MODULE));
		companyModuleDTO
				.setHasCoherentTimesheetModule((boolean) request
						.getSession()
						.getAttribute(
								PayAsiaSessionAttributes.COMPANY_HAS_COHERENT_TIMESHEET_MODULE));

		WorkFlowDelegateResponse workFlowresponse = workFlowDelegateLogic
				.getWorkFlowDelegateList(pageDTO, sortDTO, criteria, keyword,
						workFlowType, companyId, companyModuleDTO);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(workFlowresponse,
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
	 * com.payasia.web.controller.WorkFlowDelegateController#viewWorkFlowDelegate
	 * (java.lang.String, java.lang.String, int, int, java.lang.String,
	 * java.lang.String, java.lang.String,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/employee/workFlowDelegate/viewEmployeeWorkFlowDelegate.html", method = RequestMethod.POST)
	public @ResponseBody
	String viewEmployeeWorkFlowDelegate(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "criteria", required = true) String criteria,
			@RequestParam(value = "keyword", required = true) String keyword,
			@RequestParam(value = "workFlowType", required = true) String workFlowType,
			HttpServletRequest request, HttpServletResponse response) {

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		CompanyModuleDTO companyModuleDTO = new CompanyModuleDTO();
		companyModuleDTO
				.setHasClaimModule((boolean) request.getSession().getAttribute(
						PayAsiaSessionAttributes.COMPANY_HAS_CLAIM_MODULE));
		companyModuleDTO
				.setHasHrisModule((boolean) request.getSession().getAttribute(
						PayAsiaSessionAttributes.COMPANY_HAS_HRIS_MODULE));
		companyModuleDTO
				.setHasLeaveModule((boolean) request.getSession().getAttribute(
						PayAsiaSessionAttributes.COMPANY_HAS_LEAVE_MODULE));
		companyModuleDTO
				.setHasLundinTimesheetModule((boolean) request
						.getSession()
						.getAttribute(
								PayAsiaSessionAttributes.COMPANY_HAS_LUNDIN_TIMESHEET_MODULE));
		companyModuleDTO
				.setHasLionTimesheetModule((boolean) request
						.getSession()
						.getAttribute(
								PayAsiaSessionAttributes.COMPANY_HAS_LION_TIMESHEET_MODULE));
		companyModuleDTO
				.setHasCoherentTimesheetModule((boolean) request
						.getSession()
						.getAttribute(
								PayAsiaSessionAttributes.COMPANY_HAS_COHERENT_TIMESHEET_MODULE));

		WorkFlowDelegateResponse workFlowresponse = workFlowDelegateLogic
				.viewEmployeeWorkFlowDelegate(pageDTO, sortDTO, criteria,
						keyword, workFlowType, companyId, employeeId,
						companyModuleDTO);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(workFlowresponse,
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
	 * com.payasia.web.controller.WorkFlowDelegateController#searchEmployee(
	 * java.lang.String, java.lang.String, int, int, java.lang.String,
	 * java.lang.String, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	
	//TODO not used remove
	@Override
	@RequestMapping(value = { "/employee/workFlowDelegate/searchEmployee.html",
			"/admin/workFlowDelegate/searchEmployee.html" }, method = RequestMethod.POST)
	public @ResponseBody
	String searchEmployee(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "empName", required = true) String empName,
			@RequestParam(value = "empNumber", required = true) String empNumber,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		WorkFlowDelegateResponse workFlowResponse = workFlowDelegateLogic
				.searchEmployee(pageDTO, sortDTO, employeeId, empName,
						empNumber, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(workFlowResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}

	@Override
	@RequestMapping(value =  { "/employee/workFlowDelegate/searchGroupEmployee.html",
	"/admin/workFlowDelegate/searchGroupEmployee.html" }, method = RequestMethod.POST)
	public @ResponseBody
	String searchGroupEmployee(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "empName", required = false) String empName,
			@RequestParam(value = "empNumber", required = false) String empNumber,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		LeaveReviewerResponseForm leaveReviewerResponse = workFlowDelegateLogic
				.searchGroupEmployee(pageDTO, sortDTO, empName, empNumber,
						companyId, employeeId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveReviewerResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}

	@Override
	@RequestMapping(value ={ "/employee/workFlowDelegate/getCompanyGroupEmployeeId.html",
	"/admin/workFlowDelegate/getCompanyGroupEmployeeId.html" }, method = RequestMethod.POST)
	public @ResponseBody
	String getCompanyGroupEmployeeId(
			@RequestParam(value = "searchString", required = true) String searchString,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		List<EmployeeListForm> employeeListFormList = workFlowDelegateLogic
				.getCompanyGroupEmployeeId(companyId, searchString, employeeId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(employeeListFormList,
				jsonConfig);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value =	"/admin/workFlowDelegate/getEmployeeId.html" , method = RequestMethod.POST)
	public @ResponseBody
	String getEmployeeId(
			@RequestParam(value = "searchString", required = true) String searchString,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		List<EmployeeListForm> employeeListFormList = workFlowDelegateLogic
				.getEmployeeId(companyId, searchString, employeeId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(employeeListFormList,
				jsonConfig);
		return jsonObject.toString();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.WorkFlowDelegateController#searchEmployee(
	 * java.lang.String, java.lang.String, int, int, java.lang.String,
	 * java.lang.Sring, tjavax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/admin/workFlowDelegate/searchWorkflowEmployee.html", method = RequestMethod.POST)
	public @ResponseBody
	String searchWorkflowEmployee(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "empName", required = true) String empName,
			@RequestParam(value = "empNumber", required = true) String empNumber,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		LeaveReviewerResponseForm leaveReviewerResponse = workFlowDelegateLogic
				.searchWorkflowEmployee(pageDTO, sortDTO, empName, empNumber,
						companyId, employeeId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveReviewerResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}

	/**
	 * @param : Long workflowID, Long delegateID
	 * @param : This method used to check delegate are same .
	 * */
	private boolean delegateIdCheck(Long workflowID, Long delegateID) {
        if(workflowID.equals(delegateID)) {
      	  return false; 
        }		
		return true;
	}
}