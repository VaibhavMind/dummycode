package com.payasia.logic;

import java.util.List;

import com.payasia.common.dto.LundinDailyPaidTimesheetDTO;
import com.payasia.common.dto.LundinTimesheetReportDTO;
import com.payasia.common.dto.LundinTimewritingReportDTO;
import com.payasia.common.form.EmployeeListForm;
import com.payasia.common.form.LeaveReportsResponse;
import com.payasia.common.form.LundinTimesheetReportsForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.DataDictionary;

public interface LundinTimesheetReportsLogic {

	LeaveReportsResponse searchEmployee(PageRequest pageDTO,
			SortCondition sortDTO, String searchCondition, String searchText,
			Long companyId, Long employeeId, Boolean includeResignedEmployees,
			long departmentId);

	List<LundinTimesheetReportsForm> otBatchList(Long companyId, int year);

	LundinTimesheetReportDTO genTimewritingAndCostAllocationReportExcelFile(
			Long companyId, Long employeeId,
			LundinTimesheetReportsForm lundinTimesheetReportsForm);

	List<LundinTimesheetReportsForm> lundinDepartmentList(Long companyId);

	List<LundinTimesheetReportsForm> lundinBlockList(Long companyId);

	List<LundinTimesheetReportsForm> lundinAFEList(Long companyId, Long blockId);

	LundinTimewritingReportDTO getOTBatchName(Long companyId, Long fromBatchId,
			Long toBatchId);

	LundinTimesheetReportDTO showDailyPaidTimesheet(Long companyId,
			Long employeeId,
			LundinTimesheetReportsForm lundinTimesheetReportsForm,
			Boolean isManager, String[] dataDictionaryIds);

	LundinDailyPaidTimesheetDTO getDailyPaidBatchName(Long companyId,
			Long batchId);

	List<LundinTimesheetReportsForm> lundinEmployeeList(Long employeeId,
			Long companyId);

	LundinTimesheetReportDTO showTimesheetStatusReport(Long companyId,
			Long employeeId,
			LundinTimesheetReportsForm lundinTimesheetReportsForm,
			Boolean isManager, String[] dataDictionaryIds);

	List<EmployeeListForm> findEmployeeBasedOnDepartment(Long companyId,
			Long departmentId, Long employeeId);

	LeaveReportsResponse searchEmployeeByManager(PageRequest pageDTO,
			SortCondition sortDTO, String searchCondition, String searchText,
			Long companyId, Long employeeId, Boolean includeResignedEmployees,
			long departmentId);

	List<Object[]> getTimesheetStatusDepartmentEmpList(Long companyId,
			String dateFormat, Long employeeId,
			DataDictionary deptDataDictionary);

	String isEmployeeRoleAsTimesheetReviewer(Long companyId, Long employeeId);

}
