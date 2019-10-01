package com.payasia.web.controller.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.LeaveSchemeForm;
import com.payasia.common.form.LeaveSchemeResponse;
import com.payasia.common.form.LeaveTypeForm;
import com.payasia.common.form.LeaveTypeResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.dao.LeaveTypeMasterDAO;
import com.payasia.dao.bean.LeaveTypeMaster;
import com.payasia.logic.LeaveSchemeLogic;
import com.payasia.logic.LeaveTypeLogic;
import com.payasia.web.controller.LeaveTypeController;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@Controller
public class LeaveTypeControllerImpl implements LeaveTypeController {
	private static final Logger LOGGER = Logger
			.getLogger(LeaveTypeControllerImpl.class);
	@Resource
	LeaveTypeLogic leaveTypeLogic;

	@Resource
	LeaveSchemeLogic leaveSchemeLogic;
	
	@Resource
	LeaveTypeMasterDAO  leaveTypeMasterDAO;

	@RequestMapping(value = "/admin/leaveType/viewLeaveType.html", method = RequestMethod.POST)
	public @ResponseBody @Override String viewLeaveType(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "searchCondition", required = false) String searchCriteria,
			@RequestParam(value = "searchText", required = false) String keyword,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows) {

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);
		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		LeaveTypeResponse leaveTypeResponse = leaveTypeLogic
				.getLeaveTypeFormList(searchCriteria, keyword, pageDTO,
						sortDTO, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveTypeResponse,
				jsonConfig);
		return jsonObject.toString();
	}

	@RequestMapping(value = "/admin/leaveType/assignedLeaveSchemes.html", method = RequestMethod.POST)
	@Override
	@ResponseBody public String assignedLeaveSchemes(
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "searchCondition", required = true) String searchCondition,
			@RequestParam(value = "searchText", required = true) String searchText,
			@RequestParam(value = "leaveTypeId", required = true) Long leaveTypeId) {
		/* ID DECRYPT */
		leaveTypeId= FormatPreserveCryptoUtil.decrypt(leaveTypeId);
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);
		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		LeaveSchemeResponse leaveSchemeResponse = leaveSchemeLogic
				.assignedLeaveSchemes(companyId, searchCondition, searchText,
						pageDTO, sortDTO, leaveTypeId);
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
	@RequestMapping(value = "/admin/leaveType/updateLeaveType.html", method = RequestMethod.POST)
	@ResponseBody public String updateLeaveType(
			@ModelAttribute("leaveTypeForm") LeaveTypeForm leaveTypeForm,
			Long leaveTypeId) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		String status = leaveTypeLogic.updateLeaveType(leaveTypeForm,
				leaveTypeId, companyId);
		return status;
	}

	@Override
	@RequestMapping(value = "/admin/leaveType/deleteLeaveType.html", method = RequestMethod.POST)
	@ResponseBody public String deleteLeaveType(
			@RequestParam(value = "leaveTypeId", required = true) Long leaveTypeId) {
		/* ID DECRYPT */
		leaveTypeId= FormatPreserveCryptoUtil.decrypt(leaveTypeId);
		String status;
		try {
            Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
			LeaveTypeMaster leaveTypeMaster = leaveTypeMasterDAO.findLeaveTypeByCompId(leaveTypeId,companyId);
			if(leaveTypeMaster == null)
			{
				throw new PayAsiaSystemException("Authentication Exception");
			}
			leaveTypeLogic.deleteLeaveType(leaveTypeId);
			status = "false";
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			status = "true";
		}
		return status;

	}

	@Override
	@RequestMapping(value = "/admin/leaveType/editLeaveType.html", method = RequestMethod.POST)
	@ResponseBody public String editLeaveType(
			@RequestParam(value = "leaveTypeId", required = true) Long leaveTypeId) {
		/* ID DECRYPT */
		leaveTypeId= FormatPreserveCryptoUtil.decrypt(leaveTypeId);
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		LeaveTypeForm leaveTypeForm = leaveTypeLogic.editLeaveType(leaveTypeId, companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject
				.fromObject(leaveTypeForm, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/leaveType/updateLeaveTypeSortOrder.html", method = RequestMethod.POST)
	@ResponseBody public String updateLeaveTypeSortOrder(
			@RequestParam(value = "sortOrder", required = true) String[] sortOrder) {
		leaveTypeLogic.updateLeaveTypeSortOrder(sortOrder);
		return "true";

	}

	@Override
	@RequestMapping(value = "/admin/leaveType/addLeaveType.html", method = RequestMethod.POST)
	@ResponseBody public String saveLeaveType(
			@ModelAttribute("leaveTypeForm") LeaveTypeForm leaveTypeForm) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		String status = leaveTypeLogic.saveLeaveType(leaveTypeForm, companyId);
		return status;
	}

	@Override
	@RequestMapping(value = "/admin/leaveType/getLeaveSchemes.html", method = RequestMethod.POST)
	@ResponseBody public String getLeaveType(
			@RequestParam(value = "leaveTypeId", required = true) Long leaveTypeId) {
		/* ID DECRYPT */
		leaveTypeId= FormatPreserveCryptoUtil.decrypt(leaveTypeId);
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		LeaveSchemeResponse leaveSchemeResponse = new LeaveSchemeResponse();
		Set<LeaveSchemeForm> leaveTypeList = leaveSchemeLogic
				.getLeaveSchemeList(companyId, leaveTypeId);
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
	@RequestMapping(value = "/admin/leaveType/addLeaveScheme.html", method = RequestMethod.POST)
	public @ResponseBody void addLeaveScheme(
			@RequestParam(value = "leaveSchemeId", required = true) String[] leaveSchemeId,
			@RequestParam(value = "leaveTypeId", required = true) Long leaveTypeId) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		leaveTypeLogic.addLeaveScheme(leaveSchemeId, leaveTypeId, companyId);
	}

	@Override
	@RequestMapping(value = "/admin/leaveType/deleteLeaveScheme.html", method = RequestMethod.POST)
	@ResponseBody public String deleteLeaveScheme(
			@RequestParam(value = "leaveSchemeId", required = true) Long leaveSchemeId,
			@RequestParam(value = "leaveSchemeTypeId", required = true) Long leaveSchemeTypeId) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		/* ID DECRYPT */
		leaveSchemeId= FormatPreserveCryptoUtil.decrypt(leaveSchemeId);
		leaveSchemeTypeId= FormatPreserveCryptoUtil.decrypt(leaveSchemeTypeId);
		String status;
		try {
			leaveTypeLogic.deleteLeaveScheme(leaveSchemeId, leaveSchemeTypeId,
					companyId);
			status = "false";
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			status = "true";
		}
		return status;
	}
}
