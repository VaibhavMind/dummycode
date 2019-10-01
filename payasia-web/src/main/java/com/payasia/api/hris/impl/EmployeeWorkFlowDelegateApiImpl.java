package com.payasia.api.hris.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.payasia.api.hris.EmployeeWorkFlowDelegateApi;
import com.payasia.api.hris.model.Filters;
import com.payasia.api.hris.model.MultiSortMeta;
import com.payasia.api.hris.model.SearchParam;
import com.payasia.api.utils.ApiMessageHandler;
import com.payasia.api.utils.ApiMessageHandler.Type;
import com.payasia.api.utils.ApiUtils;
import com.payasia.api.utils.SearchSortUtils;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.CompanyModuleDTO;
import com.payasia.common.dto.SearchSortDTO;
import com.payasia.common.form.WorkFlowDelegateForm;
import com.payasia.common.form.WorkFlowDelegateResponse;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.logic.WorkFlowDelegateLogic;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * @author gauravkumar
 * @param :
 *            This class used for Work-Flow Delegate APIs
 * 
 */
@RestController
@RequestMapping(value = ApiUtils.API_ROOT_PATH_FOR_EMPLOYEE_HRIS)
public class EmployeeWorkFlowDelegateApiImpl implements EmployeeWorkFlowDelegateApi {

	@Resource
	private WorkFlowDelegateLogic workFlowDelegateLogic;
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private SearchSortUtils searchSortUtils;

	@Override
	@PostMapping(value = "save-workflow-delegate")
	public ResponseEntity<?> saveEmployeeWorkFlowDelegate(@RequestBody WorkFlowDelegateForm workFlowDelegateForm) {

		final Long companyId = Long.valueOf(UserContext.getWorkingCompanyId());
		final Long userId = Long.valueOf(UserContext.getUserId());

		workFlowDelegateForm.setUserId(userId);
		boolean flag = delegateIdCheck(workFlowDelegateForm.getUserId(), workFlowDelegateForm.getDelegateToId());
		if (flag) {
			workFlowDelegateLogic.saveWorkFlowDelegate(workFlowDelegateForm, companyId);
			return new ResponseEntity<>(new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(), messageSource.getMessage("data.info.save", new Object[]{}, UserContext.getLocale()).toString()), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("data.info.repeat", new Object[]{}, UserContext.getLocale()).toString()), HttpStatus.NOT_FOUND);
		}
	}

	@Override
	@PostMapping(value = "update-workflow-delegate")
	public ResponseEntity<?> updateEmployeeWorkFlowDelegate(@RequestBody WorkFlowDelegateForm workFlowDelegateForm,
			@RequestParam(value = "workflowDelegateId", required = true) long workflowDelegateId) {

		final Long companyId = Long.valueOf(UserContext.getWorkingCompanyId());

		workflowDelegateId = FormatPreserveCryptoUtil.decrypt(workflowDelegateId);
		workFlowDelegateForm.setWorkFlowDelegateId((FormatPreserveCryptoUtil.decrypt(workFlowDelegateForm.getWorkFlowDelegateId())));

		boolean flag = delegateIdCheck(workFlowDelegateForm.getUserId(), workFlowDelegateForm.getDelegateToId());

		if (flag) {
			workFlowDelegateLogic.updateWorkFlowDelegate(workFlowDelegateForm, workflowDelegateId, companyId);
			return new ResponseEntity<>(
					new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(), messageSource
							.getMessage("data.info.update", new Object[] {}, UserContext.getLocale()).toString()), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(
					new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource
							.getMessage("data.info.repeat", new Object[] {}, UserContext.getLocale()).toString()), HttpStatus.NOT_FOUND);
		}
	}

	@Override
	@PostMapping(value = "delete-workflow-delegate")
	public ResponseEntity<?> deleteWorkFlowDelegate(@RequestBody String jsonStr) {

		final Long companyId = Long.valueOf(UserContext.getWorkingCompanyId());

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObj = JSONObject.fromObject(jsonStr, jsonConfig);
		Long workflowDelegateId = jsonObj.getLong("workflowDelegateId");
		workflowDelegateId = FormatPreserveCryptoUtil.decrypt(workflowDelegateId);

		workFlowDelegateLogic.deleteWorkFlowDelegate(workflowDelegateId, companyId);
		return new ResponseEntity<>(
				new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(), messageSource
						.getMessage("data.info.deleted", new Object[] {}, UserContext.getLocale()).toString()), HttpStatus.OK);
	}

	@Override
	@PostMapping(value = "search-workflow-delegate")
	public ResponseEntity<?> searchEmployeeWorkFlowDelegateData(@RequestBody SearchParam searchParamObj,
			@RequestParam(value = "workFlowType", required = true) String workFlowType) {

		final Long companyId = Long.valueOf(UserContext.getWorkingCompanyId());
		final List<MultiSortMeta> multisortlist = Arrays.asList(searchParamObj.getMultiSortMeta());
		final List<Filters> filterllist = Arrays.asList(searchParamObj.getFilters());

		CompanyModuleDTO companyModuleDTO = new CompanyModuleDTO();
		WorkFlowDelegateResponse workFlowresponse = new WorkFlowDelegateResponse();
		
		SearchSortDTO searchSortDTO = searchSortUtils.getSearchSortObject(searchParamObj);

		companyModuleDTO.setHasClaimModule(UserContext.isClaimModule());
		companyModuleDTO.setHasHrisModule(UserContext.isHrisModule());
		companyModuleDTO.setHasLeaveModule(UserContext.isLeaveModule());
		companyModuleDTO.setHasLundinTimesheetModule(UserContext.isLundinTimesheetModule());
		companyModuleDTO.setHasLionTimesheetModule(UserContext.isLionTimesheetModule());
		companyModuleDTO.setHasCoherentTimesheetModule(UserContext.isCoherentTimesheetModule());

		if (multisortlist != null && !multisortlist.isEmpty()) {
			searchSortDTO.getSortCondition().setColumnName(multisortlist.get(0).getField());
			searchSortDTO.getSortCondition().setOrderType(multisortlist.get(0).getOrder().equals("1") ? PayAsiaConstants.ASC : PayAsiaConstants.DESC);
		}

		if ((filterllist != null && !filterllist.isEmpty())) {
			workFlowresponse = workFlowDelegateLogic.getWorkFlowDelegateList(searchSortDTO.getPageRequest(), searchSortDTO.getSortCondition(),
					filterllist.get(0).getField(), filterllist.get(0).getValue(), workFlowType, companyId, companyModuleDTO);

		} else {
			workFlowresponse = workFlowDelegateLogic.getWorkFlowDelegateList(searchSortDTO.getPageRequest(), searchSortDTO.getSortCondition(), null, null, workFlowType,
					companyId, companyModuleDTO);
		}

		if (workFlowresponse != null) {
			return new ResponseEntity<>(workFlowresponse, HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("data.info.nodata", new Object[]{}, UserContext.getLocale()).toString()), HttpStatus.NOT_FOUND);
	}

	@Override
	@PostMapping(value = "view-workflow-delegate")
	public ResponseEntity<?> viewWorkFlowDelegate(@RequestBody String jsonStr) {

		final Long companyId = Long.valueOf(UserContext.getWorkingCompanyId());

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObj = JSONObject.fromObject(jsonStr, jsonConfig);
		Long workflowDelegateId = jsonObj.getLong("workflowDelegateId");

		workflowDelegateId = FormatPreserveCryptoUtil.decrypt(workflowDelegateId);

		WorkFlowDelegateForm workFlowDelegateForm = workFlowDelegateLogic.getWorkFlowDelegateData(workflowDelegateId, companyId);

		if (workFlowDelegateForm != null) {
			return new ResponseEntity<>(workFlowDelegateForm, HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("data.info.nodata", new Object[]{}, UserContext.getLocale()).toString()), HttpStatus.NOT_FOUND);
	}

	@Override
	@PostMapping(value = "search-workflow-employee")
	public @ResponseBody ResponseEntity<?> searchEmployee(@RequestBody SearchParam searchParamObj) {

		final Long companyId = Long.valueOf(UserContext.getWorkingCompanyId());
		final Long employeeId = Long.valueOf(UserContext.getUserId());
		final List<MultiSortMeta> multisortlist = Arrays.asList(searchParamObj.getMultiSortMeta());
		final List<Filters> filterllist = Arrays.asList(searchParamObj.getFilters());

		WorkFlowDelegateResponse workFlowresponse = new WorkFlowDelegateResponse();

		SearchSortDTO searchSortDTO = searchSortUtils.getSearchSortObject(searchParamObj);

		if (multisortlist != null && !multisortlist.isEmpty()) {
			searchSortDTO.getSortCondition().setColumnName(multisortlist.get(0).getField());
			searchSortDTO.getSortCondition().setOrderType(multisortlist.get(0).getOrder().equals("1") ? PayAsiaConstants.ASC : PayAsiaConstants.DESC);
		}

		if ((filterllist != null && !filterllist.isEmpty())) {
			workFlowresponse = workFlowDelegateLogic.searchEmployee(searchSortDTO.getPageRequest(), searchSortDTO.getSortCondition(), employeeId,
					filterllist.get(0).getValue(), filterllist.get(1).getValue(), companyId);
		} else {
			workFlowresponse = workFlowDelegateLogic.searchEmployee(searchSortDTO.getPageRequest(), searchSortDTO.getSortCondition(), employeeId, null, null, companyId);
		}

		if (workFlowresponse != null) {
			return new ResponseEntity<>(workFlowresponse, HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("data.info.nodata", new Object[]{}, UserContext.getLocale()).toString()), HttpStatus.NOT_FOUND);
	}

	@Override
	@PostMapping(value = "workflow-category-list")
	public ResponseEntity<?> getWorkFilterList() {

		CompanyModuleDTO companyModuleDTO = getModule();
		Map<String, Object> workMap = new HashMap<>();

		List<WorkFlowDelegateForm> workList = workFlowDelegateLogic.getWorkflowTypeList(companyModuleDTO);

		workMap.put("workflowList", workList);
		return new ResponseEntity<>(workMap, HttpStatus.OK);
	}

	/*
	 * Function to check DelegateID
	 */
	private boolean delegateIdCheck(Long workflowID, Long delegateID) {
		if (workflowID.equals(delegateID)) {
			return false;
		}
		return true;
	}

	// TODO: HRIS MODULE IS ENABLED; REMAINING OTHERS ARE DISABLED
	private CompanyModuleDTO getModule() {
		CompanyModuleDTO companyModuleDTO = new CompanyModuleDTO();
		companyModuleDTO.setHasClaimModule(UserContext.isClaimModule());
		companyModuleDTO.setHasLeaveModule(UserContext.isLeaveModule());
		companyModuleDTO.setHasLundinTimesheetModule(UserContext.isLundinTimesheetModule());
		companyModuleDTO.setHasLionTimesheetModule(UserContext.isLionTimesheetModule());
		companyModuleDTO.setHasHrisModule(UserContext.isHrisModule());
		companyModuleDTO.setHasCoherentTimesheetModule(UserContext.isCoherentTimesheetModule());
		return companyModuleDTO;
	}

}
