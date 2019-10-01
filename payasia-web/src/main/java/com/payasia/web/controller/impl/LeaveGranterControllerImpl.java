package com.payasia.web.controller.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.LeaveGranterForm;
import com.payasia.common.form.LeaveGranterFormResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.form.YearEndProcessingForm;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.dao.LeaveGrantBatchDetailDAO;
import com.payasia.dao.LeaveGrantBatchEmployeeDetailDAO;
import com.payasia.dao.bean.LeaveGrantBatchDetail;
import com.payasia.dao.bean.LeaveGrantBatchEmployeeDetail;
import com.payasia.logic.LeaveGranterLogic;
import com.payasia.web.controller.LeaveGranterController;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * The Class LeaveGranterControllerImpl.
 */
@Controller
public class LeaveGranterControllerImpl implements LeaveGranterController {
	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(LeaveGranterControllerImpl.class);

	@Resource
	LeaveGranterLogic leaveGranterLogic;
	
	@Resource
	LeaveGrantBatchEmployeeDetailDAO leaveGrantBatchEmployeeDetailDAO;
	
	@Resource
	LeaveGrantBatchDetailDAO leaveGrantBatchDetailDao;

	@Override
	@RequestMapping(value = "/admin/leaveGranter/getLeaveGrantBatchDetailList.html", method = RequestMethod.POST)
	@ResponseBody
	public String getLeaveGrantBatchDetailList(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "fromDate", required = false) String fromDate,
			@RequestParam(value = "toDate", required = false) String toDate,
			@RequestParam(value = "leaveType", required = false) Long leaveType) {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		LeaveGranterFormResponse leaveGranterFormResponse = leaveGranterLogic
				.getLeaveGrantBatchDetailList(pageDTO, sortDTO, companyId,
						fromDate, toDate, leaveType);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveGranterFormResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/admin/leaveGranter/getLeaveGrantBatchEmpDetailList.html", method = RequestMethod.POST)
	@ResponseBody
	public String getLeaveGrantBatchEmpDetailList(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "leaveGrantBatchDetailId", required = true) Long leaveGrantBatchDetailId,
			@RequestParam(value = "employeeNumber", required = false) String employeeNumber) {
		
		/* ID DECRYPT */
		leaveGrantBatchDetailId= FormatPreserveCryptoUtil.decrypt(leaveGrantBatchDetailId);
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);
		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		LeaveGranterFormResponse leaveGranterFormResponse = leaveGranterLogic
				.getLeaveGrantBatchEmpDetailList(pageDTO, sortDTO, companyId,
						leaveGrantBatchDetailId, employeeNumber);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveGranterFormResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/admin/leaveGranter/deleteLeaveGrantBatchDetail.html", method = RequestMethod.POST)
	@ResponseBody
	public String deleteLeaveGrantBatchDetail(
			@RequestParam(value = "leaveGrantBatchDetailId", required = true) Long leaveGrantBatchDetailId) {
		/* ID DECRYPT */
		leaveGrantBatchDetailId= FormatPreserveCryptoUtil.decrypt(leaveGrantBatchDetailId);
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		LeaveGrantBatchDetail leaveGrantBatchDetail =leaveGrantBatchDetailDao.findLeaveGrantBranchDetailByCompID(leaveGrantBatchDetailId, companyId);
		if(leaveGrantBatchDetail == null)
		{
			throw new PayAsiaSystemException("Authentication Exception");
		}
		String status = leaveGranterLogic
				.deleteLeaveGrantBatchDetail(leaveGrantBatchDetailId);
		return status;
	}

	@Override
	@RequestMapping(value = "/admin/leaveGranter/deleteLeaveGrantBatchEmpDetail.html", method = RequestMethod.POST)
	@ResponseBody
	public String deleteLeaveGrantBatchEmpDetail(
			@RequestParam(value = "leaveGrantBatchEmpDetailId", required = true) Long leaveGrantBatchEmpDetailId) {
		/* ID DECRYPT */
		leaveGrantBatchEmpDetailId= FormatPreserveCryptoUtil.decrypt(leaveGrantBatchEmpDetailId);
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		LeaveGrantBatchEmployeeDetail leaveGrantBatchEmployeeDetail = leaveGrantBatchEmployeeDetailDAO.findLeaveGrantEmpDetailByCompID(leaveGrantBatchEmpDetailId, companyId);
		if(leaveGrantBatchEmployeeDetail == null)
		{
			throw new PayAsiaSystemException("Authentication Exception");
		}
		String status = leaveGranterLogic
				.deleteLeaveGrantBatchEmpDetail(leaveGrantBatchEmpDetailId);
		return status;
	}

	@Override
	@RequestMapping(value = "/admin/leaveGranter/getLeaveScheme.html", method = RequestMethod.POST)
	@ResponseBody
	public String getLeaveScheme() {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		List<YearEndProcessingForm> leaveSchemeList = leaveGranterLogic
				.getLeaveScheme(companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray
				.fromObject(leaveSchemeList, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/leaveGranter/getLeaveType.html", method = RequestMethod.POST)
	@ResponseBody
	public String getLeaveType() {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		List<LeaveGranterForm> leaveTypeList = leaveGranterLogic
				.getLeaveType(companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(leaveTypeList, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/leaveGranter/getLeaveGranterEmpList.html", method = RequestMethod.POST)
	@ResponseBody
	public String getLeaveGranterEmpList(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "leaveSchemeId", required = false) Long leaveSchemeId,
			@RequestParam(value = "leaveTypeId", required = false) Long leaveTypeId) {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);
		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		LeaveGranterFormResponse leaveGranterFormResponse = leaveGranterLogic
				.getLeaveGranterEmpList(pageDTO, sortDTO, companyId,
						leaveSchemeId, leaveTypeId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveGranterFormResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/admin/leaveGranter/getLeaveGranterLeaveTypeList.html", method = RequestMethod.POST)
	@ResponseBody
	public String getLeaveGranterLeaveTypeList(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "leaveSchemeId", required = false) Long leaveSchemeId) {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);
		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		LeaveGranterFormResponse leaveGranterFormResponse = leaveGranterLogic
				.getLeaveGranterLeaveTypeList(companyId, leaveSchemeId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveGranterFormResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/admin/leaveGranter/getAnnualRollbackEmpList.html", method = RequestMethod.POST)
	@ResponseBody
	public String getAnnualRollbackEmpList(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "fromDate", required = false) String fromDate,
			@RequestParam(value = "toDate", required = false) String toDate) {

		Long companyId =  Long.parseLong(UserContext.getWorkingCompanyId());
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);
		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		LeaveGranterFormResponse leaveGranterFormResponse = leaveGranterLogic
				.getAnnualRollbackEmpList(pageDTO, sortDTO, companyId,
						fromDate, toDate);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveGranterFormResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/admin/leaveGranter/callLeaveGrantProc.html", method = RequestMethod.POST)
	@ResponseBody
	public String callLeaveGrantProc(
			@RequestParam(value = "leaveSchemeTypeIds", required = true) String leaveSchemeTypeIds,
			@RequestParam(value = "isNewHires", required = true) Boolean isNewHires,
			@RequestParam(value = "fromDate", required = true) String fromDate,
			@RequestParam(value = "toDate", required = true) String toDate,
			@RequestParam(value = "employeeIdsList", required = true) String employeeIdsList) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		Boolean status = false;
		try {
			status = leaveGranterLogic.callLeaveGrantProc(companyId,
					leaveSchemeTypeIds, isNewHires, fromDate, toDate,
					employeeIdsList);
		} catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
		}
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(status, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/leaveGranter/rollbackResignedEmployeeLeaveProc.html", method = RequestMethod.POST)
	@ResponseBody
	public String rollbackResignedEmployeeLeaveProc(
			@RequestParam(value = "employeeIds", required = true) String employeeIds) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		Long loggedInEmployeeId = Long.parseLong(UserContext.getUserId());
		Boolean status = leaveGranterLogic.rollbackResignedEmployeeLeaveProc(
				companyId, employeeIds, loggedInEmployeeId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(status, jsonConfig);
		return jsonObject.toString();
	}

}
