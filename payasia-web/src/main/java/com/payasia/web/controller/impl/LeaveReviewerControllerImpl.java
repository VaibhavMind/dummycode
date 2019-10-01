package com.payasia.web.controller.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.payasia.common.dto.DataImportLogDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.EmployeeListForm;
import com.payasia.common.form.LeaveReviewerForm;
import com.payasia.common.form.LeaveReviewerResponseForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FileUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.EmployeeLeaveSchemeDAO;
import com.payasia.dao.EmployeeLeaveSchemeTypeDAO;
import com.payasia.dao.bean.EmployeeLeaveScheme;
import com.payasia.logic.LeaveReviewerLogic;
import com.payasia.web.controller.LeaveReviewerController;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@Controller
public class LeaveReviewerControllerImpl implements LeaveReviewerController {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(LeaveReviewerControllerImpl.class);

	@Resource
	LeaveReviewerLogic leaveReviewerLogic;
	
	@Resource
	EmployeeLeaveSchemeDAO employeeLeaveSchemeDAO;
	
	@Resource
	EmployeeLeaveSchemeTypeDAO employeeLeaveSchemeTypeDAO;

	@Autowired
	private MessageSource messageSource;

	@Override
	@RequestMapping(value = "/admin/leaveReviewer/viewLeaveReviewers.html", method = RequestMethod.GET)
	@ResponseBody
	public String viewLeaveReviewers(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "searchCondition", required = true) String searchCondition,
			@RequestParam(value = "searchText", required = true) String searchText,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows) {
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		LeaveReviewerResponseForm leaveReviewerFormResponse = leaveReviewerLogic
				.getLeaveReviewers(searchCondition, searchText, pageDTO,
						sortDTO, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(
				leaveReviewerFormResponse, jsonConfig);

		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;

	}

	@Override
	@RequestMapping(value = "/admin/leaveReviewer/getLeaveTypeWorkFlow.html", method = RequestMethod.POST)
	@ResponseBody
	public String getLeaveTypeWorkFlow(
			@RequestParam(value = "employeeLeaveSchemeTypeId", required = true) Long employeeLeaveSchemeTypeId,
			@RequestParam(value = "employeeLeaveSchemeId", required = true) Long employeeLeaveSchemeId) {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		EmployeeLeaveScheme employeeLeaveScheme= employeeLeaveSchemeDAO.findSchemeByCompanyId(employeeLeaveSchemeId,companyId);
		if(employeeLeaveScheme == null)
		{
			throw new PayAsiaSystemException("Authentication Exception");
		}
		
		/*EmployeeLeaveSchemeType employeeLeaveSchemeType= employeeLeaveSchemeTypeDAO.findSchemeTypeByCompanyId(employeeLeaveSchemeTypeId,companyId);
		if(employeeLeaveSchemeType == null)
		{
			throw new PayAsiaSystemException("Authentication Exception");
		}*/
		LeaveReviewerForm leaveReviewerForm = leaveReviewerLogic
				.getLeaveTypeWorkFlow(employeeLeaveSchemeTypeId,
						employeeLeaveSchemeId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveReviewerForm,
				jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/leaveReviewer/getLeaveReviewerData.html", method = RequestMethod.POST)
	@ResponseBody
	public String getLeaveReviewerData(
			@RequestParam(value = "filterIds", required = true) Long[] filterIds) {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		/* ID DECRYPT */
		filterIds[0]= FormatPreserveCryptoUtil.decrypt(filterIds[0]);
		filterIds[1]= FormatPreserveCryptoUtil.decrypt(filterIds[1]);
		filterIds[2]= FormatPreserveCryptoUtil.decrypt(filterIds[2]);
		LeaveReviewerResponseForm leaveReviewerForm = leaveReviewerLogic
				.getLeaveReviewerData(filterIds, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveReviewerForm,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/admin/leaveReviewer/saveLeaveReviewer.html", method = RequestMethod.POST)
	@ResponseBody
	public String saveLeaveReviewer(
			@ModelAttribute("leaveReviewerForm") LeaveReviewerForm leaveReviewerForm) {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		Long employeeId = leaveReviewerForm.getEmployeeId();
		Long loggedInEmployeeId = Long.parseLong(UserContext.getUserId());
		LeaveReviewerForm leaveReviewerFormResponse =  null;
		/* ID DECRYPT */
		if(leaveReviewerForm.getLeaveReviewerId1() != null)
		{
			leaveReviewerForm.setLeaveReviewerId1(FormatPreserveCryptoUtil.decrypt(leaveReviewerForm.getLeaveReviewerId1()));
		}
		if(leaveReviewerForm.getLeaveReviewerId2() != null)
		{
			leaveReviewerForm.setLeaveReviewerId2(FormatPreserveCryptoUtil.decrypt(leaveReviewerForm.getLeaveReviewerId2()));
		}
		if(leaveReviewerForm.getLeaveReviewerId3() != null)
		{
			leaveReviewerForm.setLeaveReviewerId3(FormatPreserveCryptoUtil.decrypt(leaveReviewerForm.getLeaveReviewerId3()));
		}		
		boolean isValid = reviwerIdCheckForLeave(employeeId, leaveReviewerForm.getLeaveReviewerId1(), leaveReviewerForm.getLeaveReviewerId2(), leaveReviewerForm.getLeaveReviewerId3());
		
		if(isValid){
		
		 leaveReviewerFormResponse = leaveReviewerLogic
				.checkLeaveReviewer(employeeId,
						leaveReviewerForm.getEmployeeLeaveSchemeId(),
						leaveReviewerForm.getEmployeeLeaveSchemeTypeId(),
						companyId);
		
		
		if (leaveReviewerFormResponse.getEmployeeStatus().equalsIgnoreCase(
				"notexists")  ) {

			try {

				leaveReviewerLogic.saveLeaveReviewer(leaveReviewerForm,
						companyId, loggedInEmployeeId);
				leaveReviewerFormResponse.setStatus("success");

			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
				leaveReviewerFormResponse.setStatus("failure");

			}
		}
		}
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(
				leaveReviewerFormResponse, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/leaveReviewer/updateLeaveReviewer.html", method = RequestMethod.POST)
	@ResponseBody
	public String updateLeaveReviewer(
			@ModelAttribute("leaveReviewerForm") LeaveReviewerForm leaveReviewerForm) {

		/* ID DECRYPT */
		if(leaveReviewerForm.getLeaveReviewerId1() != null)
		{
			leaveReviewerForm.setLeaveReviewerId1(FormatPreserveCryptoUtil.decrypt(leaveReviewerForm.getLeaveReviewerId1()));
		}
		if(leaveReviewerForm.getLeaveReviewerId2() != null)
		{
			leaveReviewerForm.setLeaveReviewerId2(FormatPreserveCryptoUtil.decrypt(leaveReviewerForm.getLeaveReviewerId2()));
		}
		if(leaveReviewerForm.getLeaveReviewerId3() != null)
		{
			leaveReviewerForm.setLeaveReviewerId3(FormatPreserveCryptoUtil.decrypt(leaveReviewerForm.getLeaveReviewerId3()));
		}
		
		LeaveReviewerForm leaveReviewerFormResponse = new LeaveReviewerForm();
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		Long loggedInEmployeeId = Long.parseLong(UserContext.getUserId());
		
       boolean isValid = reviwerIdCheckForLeave(leaveReviewerForm.getLeaveReviewerId1(), leaveReviewerForm.getLeaveReviewerId2(), leaveReviewerForm.getLeaveReviewerId3());
		
		if(isValid){
		
		try {
			leaveReviewerLogic.updateLeaveReviewer(leaveReviewerForm,
					companyId, loggedInEmployeeId);
			leaveReviewerFormResponse.setStatus("success");
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			leaveReviewerFormResponse.setStatus("failure");

		  }
		}else{
			leaveReviewerFormResponse.setStatus("failure");
		}
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(
				leaveReviewerFormResponse, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/leaveReviewer/deleteLeaveReviewer.html", method = RequestMethod.POST)
	@ResponseBody
	public String deleteLeaveReviewer(
			@RequestParam(value = "filterIds", required = true) Long[] filterIds,
			ModelMap model, HttpServletRequest request) {

		LeaveReviewerForm leaveReviewerFormResponse = new LeaveReviewerForm();
		try {
			leaveReviewerLogic.deleteLeaveReviewer(filterIds);
			leaveReviewerFormResponse.setStatus("success");
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			leaveReviewerFormResponse.setStatus("failure");

		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(
				leaveReviewerFormResponse, jsonConfig);
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
	@RequestMapping(value = "/admin/leaveReviewer/searchEmployee.html", method = RequestMethod.POST)
	@ResponseBody
	public String searchEmployee(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "empName", required = true) String empName,
			@RequestParam(value = "empNumber", required = true) String empNumber) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		Long employeeId = Long.parseLong(UserContext.getUserId());

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		LeaveReviewerResponseForm leaveReviewerResponse = leaveReviewerLogic
				.searchEmployee(pageDTO, sortDTO, empName, empNumber,
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
	@RequestMapping(value = "/admin/leaveReviewer/searchEmployeeBySessionCompany.html", method = RequestMethod.POST)
	@ResponseBody
	public String searchEmployeeBySessionCompany(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "empName", required = true) String empName,
			@RequestParam(value = "empNumber", required = true) String empNumber) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		Long employeeId = Long.parseLong(UserContext.getUserId());

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		LeaveReviewerResponseForm leaveReviewerResponse = leaveReviewerLogic
				.searchEmployeeBySessionCompany(pageDTO, sortDTO, empName,
						empNumber, companyId, employeeId);

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
	@RequestMapping(value = "/admin/leaveReviewer/getLeaveType.html", method = RequestMethod.POST)
	@ResponseBody
	public String getLeaveType(
			@RequestParam(value = "employeeId", required = true) Long employeeId,
			Locale locale) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		LeaveReviewerResponseForm leaveReviewerResponseForm = leaveReviewerLogic
				.getLeaveType(employeeId, companyId);
		try {
			if (leaveReviewerResponseForm.getErrorMsg() != null) {
				leaveReviewerResponseForm.setErrorMsg(URLEncoder.encode(
						messageSource.getMessage(
								leaveReviewerResponseForm.getErrorMsg(),
								new Object[] {}, locale), "UTF-8"));
			}
		} catch (UnsupportedEncodingException | NoSuchMessageException exception) {
			LOGGER.error(exception.getMessage(), exception);
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(
				leaveReviewerResponseForm, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "admin/leaveReviewer/isAllowManagerSelfApproveLeave.html", method = RequestMethod.POST)
	@ResponseBody
	public String isAllowManagerSelfApproveLeave() {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		String status = leaveReviewerLogic
				.isAllowManagerSelfApproveLeave(companyId);
		return status;
	}

	@Override
	@RequestMapping(value = "/admin/leaveReviewer/getCompanyGroupEmployeeId.html", method = RequestMethod.POST)
	@ResponseBody
	public String getCompanyGroupEmployeeId(
			@RequestParam(value = "searchString", required = true) String searchString) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		Long employeeId = Long.parseLong(UserContext.getUserId());
		List<EmployeeListForm> employeeListFormList = leaveReviewerLogic
				.getCompanyGroupEmployeeId(companyId, searchString, employeeId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(employeeListFormList,
				jsonConfig);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/admin/leaveReviewer/getEmployeeId.html", method = RequestMethod.POST)
	@ResponseBody
	public String getEmployeeId(
			@RequestParam(value = "searchString", required = true) String searchString) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		Long employeeId = Long.parseLong(UserContext.getUserId());
		List<EmployeeListForm> adminPaySlipFormFormList = leaveReviewerLogic
				.getEmployeeId(companyId, searchString, employeeId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(adminPaySlipFormFormList,
				jsonConfig);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/admin/leaveReviewer/getActiveWithFutureLeaveScheme.html", method = RequestMethod.POST)
	@ResponseBody
	public String getActiveWithFutureLeaveScheme(
			@RequestParam(value = "employeeId", required = true) Long employeeId,
			Locale locale) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		LeaveReviewerResponseForm leaveReviewerResponseForm = leaveReviewerLogic
				.getActiveWithFutureLeaveScheme(employeeId, companyId);

		try {
			if (leaveReviewerResponseForm.getErrorMsg() != null) {
				leaveReviewerResponseForm.setErrorMsg(URLEncoder.encode(
						messageSource.getMessage(
								leaveReviewerResponseForm.getErrorMsg(),
								new Object[] {}, locale), "UTF-8"));
			}
		} catch (UnsupportedEncodingException | NoSuchMessageException exception) {
			LOGGER.error(exception.getMessage(), exception);
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(
				leaveReviewerResponseForm, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/leaveReviewer/getLeaveTypeList.html", method = RequestMethod.POST)
	@ResponseBody
	public String getLeaveTypeListForleaveScheme(
			@RequestParam(value = "employeeLeaveSchemeId", required = true) Long employeeLeaveSchemeId,
			Locale locale) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		LeaveReviewerResponseForm leaveReviewerResponseForm = leaveReviewerLogic
				.getLeaveTypeListForleaveScheme(employeeLeaveSchemeId,
						companyId);

		try {
			if (leaveReviewerResponseForm.getErrorMsg() != null) {
				leaveReviewerResponseForm.setErrorMsg(URLEncoder.encode(
						messageSource.getMessage(
								leaveReviewerResponseForm.getErrorMsg(),
								new Object[] {}, locale), "UTF-8"));
			}
		} catch (UnsupportedEncodingException | NoSuchMessageException exception) {
			LOGGER.error(exception.getMessage(), exception);
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(
				leaveReviewerResponseForm, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/leaveReviewer/importLeaveReviewer.html", method = RequestMethod.POST)
	@ResponseBody
	public String importLeaveReviewer(
			@ModelAttribute(value = "leaveReviewerForm") LeaveReviewerForm leaveReviewerForm,
			Locale locale) throws Exception {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		LeaveReviewerForm leaveReviewerFrm = new LeaveReviewerForm();
		try {
			
			boolean isFileValid = false;
			if (leaveReviewerForm.getFileUpload()!=null) {
				isFileValid = FileUtils.isValidFile(leaveReviewerForm.getFileUpload(), leaveReviewerForm.getFileUpload().getOriginalFilename(), PayAsiaConstants.ALLOWED_ONLY_UPLOAD_EXCLE_FILE_EXT, PayAsiaConstants.ALLOWED_ONLY_UPLOAD_EXCLE_MINE_TYPE, PayAsiaConstants.FILE_SIZE);
			}

			if(isFileValid){
				leaveReviewerFrm = leaveReviewerLogic
						.importLeaveReviewer(leaveReviewerForm, companyId);
				if (leaveReviewerFrm.getDataImportLogDTOs() != null) {
					for (DataImportLogDTO dataImportLogDTO : leaveReviewerFrm
							.getDataImportLogDTOs()) {
						try {
							dataImportLogDTO.setRemarks(URLEncoder.encode(messageSource
									.getMessage(dataImportLogDTO.getRemarks(),
											new Object[] {}, locale), "UTF-8"));
						} catch (UnsupportedEncodingException | NoSuchMessageException exception) {
							LOGGER.error(exception.getMessage(), exception);
							throw new PayAsiaSystemException(exception.getMessage(),
									exception);
						}
					}

				}
			}
		} catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveReviewerFrm,
				jsonConfig);
		return jsonObject.toString();
	}
	@Override
	@RequestMapping(value = "/employee/leaveReviewer/searchEmployee.html", method = RequestMethod.POST)
	@ResponseBody
	public String searchEmployeeEmp(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "empName", required = true) String empName,
			@RequestParam(value = "empNumber", required = true) String empNumber) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);
		Long employeeId = Long.parseLong(UserContext.getUserId());
		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		LeaveReviewerResponseForm leaveReviewerResponse = leaveReviewerLogic
				.searchEmployee(pageDTO, sortDTO, empName, empNumber,
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
	 * @param : Long employeeId, Long leaveReviewerId1, Long leaveReviewerId2, Long leaveReviewerId3
	 * @param : This method used to check reviewer are same .
	 * */
	private boolean reviwerIdCheckForLeave(Long employeeId, Long leaveReviewerId1, Long leaveReviewerId2, Long leaveReviewerId3) {

		leaveReviewerId1 = leaveReviewerId1 != null ? leaveReviewerId1 : 0l;
		leaveReviewerId2 = leaveReviewerId2 != null ? leaveReviewerId2 : 0l;
		
		if(employeeId.equals(leaveReviewerId1) || employeeId.equals(leaveReviewerId2)|| employeeId.equals(leaveReviewerId3)) {
			return false;
		} else if (leaveReviewerId1.equals(leaveReviewerId2) || leaveReviewerId1.equals(leaveReviewerId3) || leaveReviewerId2.equals(leaveReviewerId3)) {
			return false;
		}
		return true;
	}
	
	
	/**
	 * @param : Long employeeId, Long leaveReviewerId1, Long leaveReviewerId2, Long leaveReviewerId3
	 * @param : This method used to check reviewer in CASE OF UPDATE  .
	 * */
	private boolean reviwerIdCheckForLeave(Long leaveReviewerId1, Long leaveReviewerId2, Long leaveReviewerId3) {

		leaveReviewerId1 = leaveReviewerId1 != null ? leaveReviewerId1 : 0l;
		leaveReviewerId2 = leaveReviewerId2 != null ? leaveReviewerId2 : 0l;
		
		 if (leaveReviewerId1.equals(leaveReviewerId2) || leaveReviewerId1.equals(leaveReviewerId3) || leaveReviewerId2.equals(leaveReviewerId3)) {
			return false;
		}
		return true;
	}

}