package com.payasia.dao;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import com.payasia.common.dto.EmployeeAsOnLeaveDTO;
import com.payasia.common.dto.EmployeeLeaveSchemeTypeDTO;
import com.payasia.common.dto.LeaveDTO;
import com.payasia.common.dto.YearWiseSummarryDTO;
import com.payasia.common.form.LeavePreferenceForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.EmployeeLeaveSchemeType;
import com.payasia.dao.bean.LeaveSchemeType;
import com.payasia.dao.bean.LeaveTypeMaster;

public interface EmployeeLeaveSchemeTypeDAO {

	EmployeeLeaveSchemeType findById(Long empLeaveSchemeTypeId);

	void update(EmployeeLeaveSchemeType employeeLeaveSchemeType);

	void save(EmployeeLeaveSchemeType employeeLeaveSchemeType);

	EmployeeLeaveSchemeType findByLeaveType(Long empLeaveSchemeId,
			Long leaveTypeId);

	EmployeeLeaveSchemeType findByLeaveTypeAndYear(Long empLeaveSchemeId,
			Long leaveTypeId, String year);

	List<EmployeeLeaveSchemeType> findByEmpLeaveSchemeId(Long empLeaveSchemeId,
			PageRequest pageDTO, SortCondition sortDTO);

	Long getCountByEmpLeaveSchemeId(Long empLeaveSchemeId, PageRequest pageDTO,
			SortCondition sortDTO);

	Path<String> getSortPath(SortCondition sortDTO,
			Root<EmployeeLeaveSchemeType> leaveSchemeRoot,
			Join<LeaveSchemeType, LeaveTypeMaster> leaveTypeJoin);

	LeaveDTO getLeaveBalance(Long employeeLeaveSchemeTypeId);

	LeaveDTO getNoOfDays(LeaveDTO leaveDTO);

	EmployeeLeaveSchemeType saveReturn(
			EmployeeLeaveSchemeType employeeLeaveSchemeType);

	LeaveDTO distributeEmployeeLeave(Long employeeId,
			Long employeeLeaveSchemeTypeId);

	List<EmployeeLeaveSchemeType> findByConditionEmpLeaveSchemeId(
			Long empLeaveSchemeId);

	List<EmployeeAsOnLeaveDTO> findEmpLeavesAsOnDate(Long companyId,
			String employeeIds, Boolean isAllEmployees, String leaveBalAsOnDate);

	List<EmployeeAsOnLeaveDTO> findLeaveReviewerReportData(Long companyId,
			String employeeIds, Boolean isAllEmployees);

	EmployeeLeaveSchemeType findByLeaveSchIdAndEmpId(Long employeeId,
			Long leaveSchemeTypeId);

	LeaveDTO getNoOfDaysForPostLeaveTranImport(LeaveDTO leaveDTO,
			String dateFormat);

	List<EmployeeLeaveSchemeType> findByCompany(Long companyId, Date currentDate);

	List<EmployeeLeaveSchemeType> findAllByEmpLeaveSchemeId(
			Long empLeaveSchemeId);

	EmployeeLeaveSchemeType findByCondition(String employeeNumber,
			String leaveTypeName, Long companyId, String date, String dateFormat);

	EmployeeLeaveSchemeType findByEmpLeaveSchemeAndLeaveType(
			Long empLeaveSchemeId, String leaveTypeName);

	List<EmployeeAsOnLeaveDTO> findLeaveBalAsOnDateCustomEmpReport(
			Long companyId, String employeeIds, Boolean isAllEmployees,
			String leaveBalAsOnDate);

	List<EmployeeLeaveSchemeTypeDTO> getEmployeeLeaveBalSummaryProc(
			Long employeeId, Integer year, Timestamp leaveCalendarStartDate,
			Timestamp leaveCalendarEndDate);

	List<YearWiseSummarryDTO> findYearWiseSummaryReport(Long companyId,
			Integer reportYear, String startDate, String endDate,
			String leaveTypeList, String employeeIds, Boolean isManager,
			boolean includeResignedEmployees, String defaultDateFormat,
			Long leaveReviewerId);

	EmployeeLeaveSchemeType findByleaveSchemeTypeIdAndCompanyIdAndEmpId(Long employeeLeaveSchemeTypeId, Long companyId,
			Long employeeId);

	EmployeeLeaveSchemeType findSchemeTypeByCompanyId(Long employeeLeaveSchemeId, Long companyId);

	List<EmployeeLeaveSchemeTypeDTO> getEmployeeLeaveBalSummaryProcNew(Long employeeId, Integer year,
			Timestamp leaveCalendarStartDate, Timestamp leaveCalendarEndDate, LeavePreferenceForm leavePreferenceForm);

}
