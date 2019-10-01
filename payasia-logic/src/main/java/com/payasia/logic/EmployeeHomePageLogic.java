package com.payasia.logic;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.payasia.common.dto.CompanyModuleDTO;
import com.payasia.common.form.EmployeeFilterListForm;
import com.payasia.common.form.EmployeeHomePageForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;

@Transactional
public interface EmployeeHomePageLogic {

	EmployeeHomePageForm getPayslipDetails(Long companyId);

	EmployeeHomePageForm getLeaveDetails(Long companyId, Long employeeId);

	String getPasswordExpiryReminder(Long employeeId, Long companyId);

	EmployeeHomePageForm getClaimDetails(Long companyId, Long employeeId);

	EmployeeHomePageForm generateEmployeeActivationCode(Long employeeId);

	EmployeeHomePageForm getAllRecentActivityList(Long companyId,
			Long employeeId, CompanyModuleDTO companyModuleDTO, Long languageId);

	String getDefaultEmailCCByEmp(Long companyId, Long employeeId,
			String moduleName, boolean moduleEnabled);

	List<EmployeeFilterListForm> getDefaultEmailCCListByEmployee(
			Long companyId, Long employeeId, String moduleName,
			boolean moduleEnabled);

	void saveDefaultEmailCCByEmployee(Long companyId, Long employeeId,
			String ccEmailIds, String moduleName, boolean moduleEnabled);
	
	Map<String, Object> getAllRecentActivityListCount(Long companyId, Long employeeId,
			CompanyModuleDTO companyModuleDTO, Long languageId, String requestType, List<String> listOfPrivilege,
			List<String> listOfRoles);

	EmployeeHomePageForm getRecentActivityList(Long companyId, Long employeeId, CompanyModuleDTO companyModuleDTO,
			Long languageId, String recentActivityType, PageRequest pageDTO, SortCondition sortDTO,
			List<String> listOfPrivilege, List<String> listOfRole);



}
