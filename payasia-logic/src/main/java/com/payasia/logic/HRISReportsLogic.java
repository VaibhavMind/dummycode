package com.payasia.logic;

import java.util.List;

import com.payasia.common.dto.EmployeeLoginHistoryReportDTO;
import com.payasia.common.dto.HRISReportDTO;
import com.payasia.common.dto.RolePrivilegeReportDTO;
import com.payasia.common.form.ClaimReportsForm;
import com.payasia.common.form.EmployeeDetailsResponse;
import com.payasia.common.form.HRISReportsForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.form.SwitchCompanyResponse;

public interface HRISReportsLogic {

	HRISReportDTO genHRISHeadcountReport(Long employeeId, Long companyId,
			HRISReportsForm hrisReportsForm, Boolean isManager);

	SwitchCompanyResponse getSwitchCompanyList(PageRequest pageDTO,
			SortCondition sortDTO, Long employeeId, String searchCondition,
			String searchText, String groupName, Boolean includeInactiveCompany);

	EmployeeDetailsResponse getEmployeeList(String searchCondition,
			String searchText, String companyName);

	List<EmployeeLoginHistoryReportDTO> genLoginDetailsReport(
			ClaimReportsForm claimReportsForm, Long companyId);

	List<RolePrivilegeReportDTO> genRolePrivilegeReport(Long employeeId,
			Long companyId, ClaimReportsForm claimReportsForm, Boolean isManager);

}
