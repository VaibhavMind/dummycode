package com.payasia.api.leave.impl;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payasia.api.leave.EmployeeTeamLeaveApi;
import com.payasia.api.leave.model.Filters;
import com.payasia.api.leave.model.MultiSortMeta;
import com.payasia.api.leave.model.SearchParam;
import com.payasia.api.utils.ApiMessageHandler;
import com.payasia.api.utils.ApiMessageHandler.Type;
import com.payasia.api.utils.ApiUtils;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.EmployeeShortListDTO;
import com.payasia.common.dto.LeaveApplicationWorkflowDTO;
import com.payasia.common.dto.TeamLeaveDTO;
import com.payasia.common.form.LeaveBalanceSummaryForm;
import com.payasia.common.form.LeaveBalanceSummaryResponse;
import com.payasia.common.form.LeavePreferenceForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.dao.EmployeeDAO;
import com.payasia.logic.LeaveBalanceSummaryLogic;
import com.payasia.logic.LeavePreferenceLogic;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@RestController
@RequestMapping(value=ApiUtils.API_ROOT_PATH_FOR_EMPLOYEE+"/leave/")
public class EmployeeTeamLeaveApiImpl implements EmployeeTeamLeaveApi {

	@Resource
	private LeaveBalanceSummaryLogic leaveBalanceSummaryLogic;
	
	@Resource
    private EmployeeDAO employeeDAO;
	
	@Resource
	 private MessageSource messageSource;
	
	@Resource
	private LeavePreferenceLogic leavePreferenceLogic;
	
	@Override
	@PostMapping(value = "view-manager-name-for-employee")
	public ResponseEntity<?> doShowManagerNameForEmployee(@RequestBody String jsonStr) {
		Long companyID = Long.parseLong(UserContext.getWorkingCompanyId());
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObj=JSONObject.fromObject(jsonStr, jsonConfig);
		String employeeNumber = jsonObj.getString("employeeNumber");
		Long loginEmployeeID= Long.parseLong(UserContext.getUserId());
		EmployeeShortListDTO employeeShortListDTO = leaveBalanceSummaryLogic.getEmployeeNameForManagerDup(loginEmployeeID, employeeNumber,
				companyID);
		if(employeeShortListDTO==null){
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.leave.manager.name.employee", new Object[] {}, UserContext.getLocale()).toString()),HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(employeeShortListDTO , HttpStatus.OK);
	}
	
	
	@Override
	@PostMapping(value = "view-post-leave-scheme-data-for-employee")
	public ResponseEntity<?> doShowPostLeaveSchemeDataForEmployee(@RequestBody String jsonStr) {

		Long companyID = Long.parseLong(UserContext.getWorkingCompanyId());
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObj=JSONObject.fromObject(jsonStr, jsonConfig);
		String employeeNumber = jsonObj.getString("employeeNumber");
		String year = jsonObj.getString("year");
		LeaveBalanceSummaryResponse leaveBalSummaryResponse = leaveBalanceSummaryLogic.getPostLeaveSchemeData(employeeNumber,year, companyID);
		if(leaveBalSummaryResponse.getLeaveBalanceSummaryFormList().isEmpty()){
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.leave.view.scheme.employee.data", new Object[] {}, UserContext.getLocale()).toString()),HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(leaveBalSummaryResponse , HttpStatus.OK);
	}

	@Override
	@PostMapping(value = "view-team-employee-leave-scheme-type")
	public ResponseEntity<?> doShowTeamEmployeeLeaveSchemeType(@RequestBody SearchParam searchParamObj) {
		
		Long companyID = Long.parseLong(UserContext.getWorkingCompanyId());
		Long loginEmployeeID= Long.parseLong(UserContext.getUserId());
		final List<MultiSortMeta> multisortlist = Arrays.asList(searchParamObj.getMultiSortMeta());
		final List<Filters> filterllist = Arrays.asList(searchParamObj.getFilters());
		LeaveBalanceSummaryResponse leaveSchemeResponse = null;
		SortCondition sortDTO = new SortCondition();

		// Default sort
		sortDTO.setColumnName(searchParamObj.getSortField());
		sortDTO.setOrderType(searchParamObj.getSortOrder().equals("1") ? "ASC" : "DESC");

		// CASE 1 : page size or page no
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(searchParamObj.getPage());
		pageDTO.setPageSize(searchParamObj.getRows());
		
		
		// CASE 2 : sort data according to request parameters/MultiSortMeta []
		 if (multisortlist != null && !multisortlist.isEmpty()) {
			sortDTO.setColumnName(multisortlist.get(0).getField());
			sortDTO.setOrderType(multisortlist.get(0).getOrder().equals("1") ? "ASC" : "DESC");
		 }
		
		 leaveSchemeResponse = leaveBalanceSummaryLogic
				.getTeamEmployeeLeaveSchemeType(Integer.parseInt(filterllist.get(2).getValue()),filterllist.get(0).getValue(), pageDTO,
						sortDTO, companyID, loginEmployeeID, filterllist.get(1).getValue());
			
		if(leaveSchemeResponse.getRows().isEmpty()){
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.leave.view.team.employee.scheme.data", new Object[] {}, UserContext.getLocale()).toString()),HttpStatus.NOT_FOUND); 
		}
		return new ResponseEntity<>(leaveSchemeResponse , HttpStatus.OK);
	}

	@Override
	@PostMapping(value = "view-team-employee-leave-scheme-type-history")
	public ResponseEntity<?> doShowTeamEmployeeLeaveSchemeTypeHistoryList(@RequestBody String jsonStr) {
		
		Long companyID = Long.parseLong(UserContext.getWorkingCompanyId());
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObj=JSONObject.fromObject(jsonStr, jsonConfig);
		Long leaveSchemeTypeId = jsonObj.getLong("leaveSchemeTypeId");
		String employeeNumber = jsonObj.getString("employeeNumber");
		String searchStringEmpId = jsonObj.getString("searchStringEmpId");
		String postLeaveTypeFilterId = jsonObj.getString("postLeaveTypeFilterId");
		int year = jsonObj.getInt("year");
		Long loginEmployeeID= Long.parseLong(UserContext.getUserId());
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(1);
		pageDTO.setPageSize(1);
		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName("");
		sortDTO.setOrderType("ASC");
		
		LeaveBalanceSummaryResponse leaveSchemeResponse = leaveBalanceSummaryLogic.getTeamEmpLeaveSchemeTypeHistoryList(leaveSchemeTypeId,
						postLeaveTypeFilterId, year, employeeNumber, companyID,
						loginEmployeeID, searchStringEmpId, pageDTO, sortDTO);
		if(leaveSchemeResponse.getEmpLeaveSchemeTypeHistoryList() == null || leaveSchemeResponse.getEmpLeaveSchemeTypeHistoryList().isEmpty()){
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.leave.view.team.employee.scheme.history.data", new Object[] {}, UserContext.getLocale()).toString()),HttpStatus.NOT_FOUND); 
		}
		
		return new ResponseEntity<>(leaveSchemeResponse , HttpStatus.OK);
		
		
	}

	@Override
	@PostMapping(value = "view-scheme")
	public ResponseEntity<?> doShowLeaveScheme(@RequestBody String jsonStr) {
		
		Long companyID = Long.parseLong(UserContext.getWorkingCompanyId());
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObj=JSONObject.fromObject(jsonStr, jsonConfig);
		String employeeNumber = jsonObj.getString("employeeNumber");
		String year = jsonObj.getString("year");
		List<LeaveBalanceSummaryForm> leaveBalSummaryFrmList = leaveBalanceSummaryLogic.getLeaveScheme(employeeNumber, year,companyID);
		if(leaveBalSummaryFrmList.isEmpty()){
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.leave.view.team.employee.scheme", new Object[] {}, UserContext.getLocale()).toString()),HttpStatus.NOT_FOUND); 
		}
		return new ResponseEntity<>(leaveBalSummaryFrmList , HttpStatus.OK);
	}
	
	@Override
	@PostMapping(value = "view-my-scheme-type-list")
	public ResponseEntity<?> doShowMyLeaveSchemeTypeList(@RequestBody String jsonStr) {
		
		Long companyID = Long.parseLong(UserContext.getWorkingCompanyId());
		Long loginEmployeeID= Long.parseLong(UserContext.getUserId());
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObj=JSONObject.fromObject(jsonStr, jsonConfig);
		int year = jsonObj.getJSONArray("filters").getJSONObject(0).getInt("value");
		
				PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(1);
		pageDTO.setPageSize(0);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName("");
		sortDTO.setOrderType("ASC");
		
		LeaveBalanceSummaryResponse leaveSchemeResponse = leaveBalanceSummaryLogic
				.getMyLeaveSchemeType(year, pageDTO, sortDTO, companyID,
						loginEmployeeID);
		if(leaveSchemeResponse.getRows().isEmpty()){
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.leave.view.team.employee.scheme.type.list", new Object[] {}, UserContext.getLocale()).toString()),HttpStatus.NOT_FOUND); 
		}
		
		return new ResponseEntity<>(leaveSchemeResponse , HttpStatus.OK);
		
		
	}
	
	@Override
	@PostMapping(value = "view-my-leave-preference")
	public ResponseEntity<?> doShowLeavePreference() {
		Long companyID = Long.parseLong(UserContext.getWorkingCompanyId());
		LeavePreferenceForm leavePreferenceForm = leavePreferenceLogic.getLeavePreference(companyID);
		return new ResponseEntity<>(leavePreferenceForm , HttpStatus.OK);
	}
	
	
	
	
	@Override
	@PostMapping(value = "view-transaction-history-info")
	public ResponseEntity<?> doGetLeaveTransactionHistoryInfo(@RequestBody String jsonStr) {
		Long companyID = Long.parseLong(UserContext.getWorkingCompanyId());
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObj=JSONObject.fromObject(jsonStr, jsonConfig);
		
		Long leaveAppId =jsonObj.getLong("leaveApplicationid");
		Long leaveApplicationId = FormatPreserveCryptoUtil.decrypt(leaveAppId);
		String transactionType = jsonObj.getString("leaveTransactionType");
		
		LeaveBalanceSummaryResponse transactionHistoryResponse = leaveBalanceSummaryLogic
				.getLeaveTransactionHistory(leaveApplicationId, transactionType, companyID);
		List<LeaveApplicationWorkflowDTO> applicationWorkflowDTOs = transactionHistoryResponse
				.getEmployeeLeaveSchemeTypeHistoryDTO().getWorkflowList();
		for (LeaveApplicationWorkflowDTO applicationWorkflowDTO : applicationWorkflowDTOs) {
			if (StringUtils.isNotBlank(applicationWorkflowDTO.getStatus())) {
				applicationWorkflowDTO.setStatus(
						messageSource.getMessage(applicationWorkflowDTO.getStatus(), new Object[] {}, UserContext.getLocale()));
			}

		}
		return new ResponseEntity<>(transactionHistoryResponse , HttpStatus.OK);
	}


	@Override
	@PostMapping(value = "view-manager-number-for-employee")
	public ResponseEntity<?> doShowManagerNumberForEmployee(@RequestBody String jsonStr) {
		Long companyID = Long.parseLong(UserContext.getWorkingCompanyId());
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObj=JSONObject.fromObject(jsonStr, jsonConfig);
		String employeeNumber = jsonObj.getString("employeeNumber");
		List<LeaveBalanceSummaryForm> leaveBalanceSummaryFormList = null;
		Long loginEmployeeID= Long.parseLong(UserContext.getUserId());
		leaveBalanceSummaryFormList = leaveBalanceSummaryLogic.getEmployeeIdForManager(companyID, employeeNumber, loginEmployeeID);
	
		return new ResponseEntity<>(leaveBalanceSummaryFormList , HttpStatus.OK);
	}
	
	@Override
	@PostMapping(value = "team-members-info")
	public ResponseEntity<?> teamMembersDetails(){
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		List<TeamLeaveDTO> teamLeaveDTOList = null;
		teamLeaveDTOList = leaveBalanceSummaryLogic.getTeamMemberInfo(companyId, employeeId);
		if(!teamLeaveDTOList.isEmpty()) {
			return new ResponseEntity<>(teamLeaveDTOList , HttpStatus.OK);
		}
		else {
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("data.info.nodata", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
		}
	}

}
