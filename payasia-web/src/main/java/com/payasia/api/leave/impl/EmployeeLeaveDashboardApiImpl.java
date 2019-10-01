package com.payasia.api.leave.impl;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.payasia.api.leave.EmployeeLeaveDashboardApi;
import com.payasia.api.leave.model.Filters;
import com.payasia.api.leave.model.MultiSortMeta;
import com.payasia.api.leave.model.SearchParam;
import com.payasia.api.utils.ApiMessageHandler;
import com.payasia.api.utils.ApiMessageHandler.Type;
import com.payasia.api.utils.ApiUtils;
import com.payasia.api.utils.PrivilegeUtils;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.EmployeeLeaveSchemeTypeDTO;
import com.payasia.common.form.LeaveBalanceSummaryResponse;
import com.payasia.common.form.LeavePreferenceForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.logic.CompanyInformationLogic;
import com.payasia.logic.LeaveBalanceSummaryLogic;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@RestController
@RequestMapping(value=ApiUtils.API_ROOT_PATH_FOR_EMPLOYEE+"/leave/")
public class EmployeeLeaveDashboardApiImpl implements EmployeeLeaveDashboardApi {
	@Resource
	private CompanyInformationLogic companyInformationLogic;
	
	@Resource
	private LeaveBalanceSummaryLogic leaveBalanceSummaryLogic;
	@Resource
	private MessageSource messageSource;
	
	@Autowired
	private PrivilegeUtils privilegeUtils;
	
	@PostMapping(value = "view-leave-dashboard")
	public @ResponseBody ResponseEntity<?> doShowLeaveDashboard(@RequestBody SearchParam searchParamObj,@RequestParam("fromYear") int fromYear,
			@RequestParam("toYear") int toYear,@RequestParam("year")int year) {
		
		Long loginEmployeeID=Long.valueOf(UserContext.getUserId());
		Long companyID = Long.parseLong(UserContext.getWorkingCompanyId());
		final List<MultiSortMeta> multisortlist = Arrays.asList(searchParamObj.getMultiSortMeta());
		final List<Filters> filterllist = Arrays.asList(searchParamObj.getFilters());
		LeaveBalanceSummaryResponse leaveSchemeResponse = null;
		SortCondition sortDTO = new SortCondition();
		
		sortDTO.setColumnName(searchParamObj.getSortField());
		sortDTO.setOrderType(searchParamObj.getSortOrder().equals("1") ? "ASC" : "DESC");
		
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(searchParamObj.getPage());
		pageDTO.setPageSize(searchParamObj.getRows());
		 if (multisortlist != null && !multisortlist.isEmpty()) {
			sortDTO.setColumnName(multisortlist.get(0).getField());
			sortDTO.setOrderType(multisortlist.get(0).getOrder().equals("1") ? "ASC" : "DESC");
		 }
		if(filterllist != null && !filterllist.isEmpty()){
			
			leaveSchemeResponse = leaveBalanceSummaryLogic.getDashBoardByManagerEmpOnLeaveList(filterllist.get(0).getValue(),filterllist.get(1).getValue(),
					pageDTO, sortDTO,companyID, loginEmployeeID,privilegeUtils.getRole(),messageSource,year,fromYear,toYear);
		}
		 if(leaveSchemeResponse == null || leaveSchemeResponse.getRows().isEmpty()){
			 return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.leave.dashboard.data", new Object[] {}, UserContext.getLocale()).toString()),HttpStatus.NOT_FOUND);
		 }
		return new ResponseEntity<>(leaveSchemeResponse, HttpStatus.OK);
	}

	@Override
	@PostMapping(value = "encashed-leave-visible")
	public ResponseEntity<?> isEncashedVisible() {
		Long companyID = Long.parseLong(UserContext.getWorkingCompanyId());
		LeavePreferenceForm leavePreferenceForm = leaveBalanceSummaryLogic.isEncashedVisible(companyID);
		return new ResponseEntity<>(leavePreferenceForm, HttpStatus.OK);
	}
	
	@Override
	@PostMapping(value = "year-list")
	public ResponseEntity<?> doShowYearList() {
		Long companyID = Long.parseLong(UserContext.getWorkingCompanyId());
		List<EmployeeLeaveSchemeTypeDTO> yearList = leaveBalanceSummaryLogic.getDistinctYears(companyID);
		 if(yearList.isEmpty()){
			 return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.leave.year.list", new Object[] {}, UserContext.getLocale()).toString()),HttpStatus.NOT_FOUND);
		 }
		return new ResponseEntity<>(yearList, HttpStatus.OK);
	}


	@Override
	@PostMapping(value = "employee-leave-scheme-type-history-list")
	public ResponseEntity<?> doShowEmployeeLeaveSchemeTypeHistoryList(@RequestBody String jsonStr) {
		
		String employeeNumber=UserContext.getLoginId();
		Long loginEmployeeID = Long.parseLong(UserContext.getUserId());
	    Long companyID = Long.parseLong(UserContext.getWorkingCompanyId());
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObj=JSONObject.fromObject(jsonStr, jsonConfig);		
		Long leaveSchemeTypeId = jsonObj.getLong("leaveSchemeTypeId");
		String postLeaveTypeFilterId = jsonObj.getString("postLeaveTypeFilterId");
		int year = jsonObj.getInt("year");
		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName("");
		sortDTO.setOrderType("ASC");
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(1);
		pageDTO.setPageSize(0);
		LeaveBalanceSummaryResponse leaveSchemeResponse = leaveBalanceSummaryLogic.getEmpLeaveSchemeTypeHistoryList(
				leaveSchemeTypeId, postLeaveTypeFilterId, year, employeeNumber, companyID, loginEmployeeID, pageDTO,
				sortDTO, false);
		 if(leaveSchemeResponse.getEmpLeaveSchemeTypeHistoryList().isEmpty()){
			 return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.leave.employee.scheme.type.history.data", new Object[] {}, UserContext.getLocale()).toString()),HttpStatus.NOT_FOUND);
		 }
		return new ResponseEntity<>(leaveSchemeResponse, HttpStatus.OK);
	}
	

}
