package com.payasia.logic;

import java.util.List;

import com.payasia.common.dto.EmployeeLeaveSchemeTypeDTO;
import com.payasia.common.dto.LeaveReportDTO;
import com.payasia.common.dto.SelectOptionDTO;
import com.payasia.common.form.EmployeeListForm;
import com.payasia.common.form.LeavePreferenceForm;
import com.payasia.common.form.LeaveReportsForm;
import com.payasia.common.form.LeaveReportsResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.form.SwitchCompanyResponse;

public interface LeaveReportsLogic {

	LeaveReportsResponse viewLeaveReports(PageRequest pageDTO,
			SortCondition sortDTO, Long companyId);

	LeaveReportsResponse searchEmployee(PageRequest pageDTO,
			SortCondition sortDTO, String searchCondition, String searchText,
			Long companyId, Long employeeId, String metaData,
			Boolean includeResignedEmployees);

	List<LeaveReportsForm> getLeaveTypeList(Long companyId);

	List<LeaveReportsForm> getLeaveTransactionList(Long companyId);

	LeaveReportDTO showLeaveBalanceAsOnDateReport(Long companyId,
			LeaveReportsForm leaveReportsForm, Long employeeId,
			String[] dataDictionaryIds);

	LeaveReportDTO showLeaveReviewerReport(Long companyId,
			LeaveReportsForm leaveReportsForm, Long employeeId);

	LeaveReportsResponse searchEmployeeForManager(PageRequest pageDTO,
			SortCondition sortDTO, String searchCondition, String searchText,
			Long companyId, Long employeeId, String metaData,
			Boolean includeResignedEmployees);

	LeaveReportDTO showLeaveTranReport(Long companyId, Long employeeId,
			LeaveReportsForm leaveReportsForm, Boolean isManager,
			String[] dataDictionaryIds);

	LeaveReportDTO showYearWiseSummaryReport(Long employeeId, Long companyId,
			LeaveReportsForm leaveReportsForm, Boolean isManager,
			String[] dataDictionaryIds);

	LeaveReportDTO genLeaveBalAsOnDayCustReportPDF(Long companyId,
			Long employeeId, LeaveReportsForm leaveReportsForm,
			boolean isManager);

	LeaveReportDTO showDayWiseLeaveTranReport(Long companyId, Long employeeId,
			LeaveReportsForm leaveReportsForm, Boolean isManager,
			String[] dataDictionaryIds);

	List<EmployeeLeaveSchemeTypeDTO> getDistinctYears(Long companyId,
			boolean isManager);

	LeavePreferenceForm getLeavePreferenceDetail(Long companyId,
			String companyCode, boolean isAdmin);

	List<EmployeeListForm> getLeaveReviewerList(Long employeeId, Long companyId);

	LeaveReportDTO genLeaveHeadcountReport(Long employeeId, Long companyId,
			LeaveReportsForm leaveReportsForm, Boolean isManager);

	SwitchCompanyResponse getSwitchCompanyList(PageRequest pageDTO,
			SortCondition sortDTO, Long employeeId, String searchCondition,
			String searchText, String groupName);

	SwitchCompanyResponse getOrderedCompanyList(PageRequest pageDTO,
			SortCondition sortDTO, Long employeeId, String searchCondition,
			String searchText, String groupName);

	List<SelectOptionDTO> getLeaveReportList(Long companyId, String companyCode, boolean isAdmin);

//	String getLeaveReportName(String keyName, Long companyId, String companyCode, boolean isAdmin);



}
