package com.payasia.logic;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

import com.payasia.common.dto.LeaveSchemeDTO;
import com.payasia.common.form.EmployeeFilterListForm;
import com.payasia.common.form.LeaveSchemeForm;
import com.payasia.common.form.LeaveSchemeResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.EmployeeLeaveSchemeType;
import com.payasia.dao.bean.LeaveSchemeType;
import com.payasia.dao.bean.LeaveSchemeTypeShortList;

@Transactional
public interface LeaveSchemeLogic {
	LeaveSchemeResponse viewLeaveScheme(Long companyId, String searchCondition,
			String searchText, PageRequest pageDTO, SortCondition sortDTO);

	String configureClaimItem();

	String addCustomField();

	String addLeaveScheme(Long companyId, LeaveSchemeForm leaveSchemeForm);

	void deleteLeaveScheme(Long companyId, Long leaveSchemeId);

	LeaveSchemeForm getLeaveScheme(Long companyId, Long leaveSchemeId);

	String configureLeaveScheme(LeaveSchemeForm leaveSchemeForm, Long companyId);

	Set<LeaveSchemeForm> getLeaveTypeList(Long companyId, Long leaveSchemeId);

	LeaveSchemeResponse addLeaveType(String[] leaveTypeId, Long leaveSchemeId,
			Long companyId);

	LeaveSchemeResponse viewLeaveType(Long leaveSchemeId, Long companyId,
			PageRequest pageDTO, SortCondition sortDTO);

	LeaveSchemeForm getLeaveTypeForEdit(Long leaveSchemeTypeId);

	void editLeaveType(Long leaveSchemeTypeId, LeaveSchemeForm leaveSchemeForm);

	LeaveSchemeForm getLeaveSchemeAppcodeList();

	LeaveSchemeResponse saveLeaveSchemeDetailTypes(
			LeaveSchemeForm leaveSchemeForm);

	LeaveSchemeForm getLeaveSchemeTypeForEdit(Long companyId,
			Long leaveSchemeTypeId);

	List<EmployeeFilterListForm> getEmployeeFilterList(Long companyId,
			Long employeeId, boolean isAdmin);

	List<EmployeeFilterListForm> getEditEmployeeFilterList(
			Long leaveSchemeTypeId, Long companyId);

	void deleteFilter(Long filterId, Long companyId);

	LeaveSchemeResponse assignedLeaveSchemes(Long companyId,
			String searchCondition, String searchText, PageRequest pageDTO,
			SortCondition sortDTO, Long leaveTypeId);

	Set<LeaveSchemeForm> getLeaveSchemeList(Long companyId, Long leaveTypeId);

	LeaveSchemeForm getLeaveTypeLeaveScheme(Long companyId, Long leaveSchemeId,
			Long leaveTypeId);

	String configureLeaveSchemeTypeWorkFlow(LeaveSchemeForm leaveSchemeForm,
			Long companyId);

	LeaveSchemeResponse viewAssignedEmployees(Long leaveSchemeId,
			Long companyId, PageRequest pageDTO, SortCondition sortDTO);

	Set<LeaveSchemeForm> getLeaveTypeListForCombo(Long companyId,
			Long leaveSchemeId, Long leaveSchemeTypeId);

	List<LeaveSchemeDTO> getAllLeaveSchemes(Long companyId);

	List<LeaveSchemeForm> getAppcodeListForLeaveDistMeth();

	List<LeaveSchemeForm> getAppcodeListForLeaveRoundingMeth();

	List<LeaveSchemeForm> getAppcodeListForProrationMeth();

	List<LeaveSchemeForm> getAppcodeListForProrationBasedOnMeth();

	List<LeaveSchemeForm> getAppcodeListForEarnedProrationMeth();

	List<LeaveSchemeForm> getAppcodeListForRoundOffRuleMeth();

	String saveEmployeeFilterList(String metaData, Long leaveSchemeTypeId,
			Long companyId);

	LeaveSchemeResponse deleteLeaveType(Long leaveSchemeTypeId,
			Long leaveSchemeId, Long companyId);

	LeaveSchemeResponse callRedistributeProc(Long leaveSchemeTypeId);

	List<LeaveSchemeForm> getAppcodeListForApplyAfterFrom();

	void setEmployeeLeaveSchemeTypes(
			List<LeaveSchemeTypeShortList> leaveSchemeTypeShortLists,
			Long companyId,
			List<Long> leaveSchemeEmployeeIds,
			LeaveSchemeType leaveSchType,
			HashMap<String, EmployeeLeaveSchemeType> employeeLeaveSchemeTypeMap,
			List<Long> formIds, Date currentDate);

	List<LeaveSchemeDTO> getAllLeaveSchemesGroup(Long companyId);

}
