package com.payasia.logic.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.Tuple;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.payasia.common.dto.CompanyConditionDTO;
import com.payasia.common.dto.EmployeeConditionDTO;
import com.payasia.common.dto.EmployeeLoginHistoryReportDTO;
import com.payasia.common.dto.HRISReportDTO;
import com.payasia.common.dto.PayAsiaCompanyStatisticReportDTO;
import com.payasia.common.dto.RolePrivilegeReportDTO;
import com.payasia.common.form.ClaimReportsForm;
import com.payasia.common.form.EmployeeDetailForm;
import com.payasia.common.form.EmployeeDetailsResponse;
import com.payasia.common.form.HRISReportsForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.form.SwitchCompanyForm;
import com.payasia.common.form.SwitchCompanyResponse;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.CompanyGroupDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EmployeeLoginHistoryDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyGroup;
import com.payasia.dao.bean.CompanyGroup_;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeLoginHistory;
import com.payasia.logic.BaseLogic;
import com.payasia.logic.HRISReportsLogic;

@Component
public class HRISReportsLogicImpl extends BaseLogic implements HRISReportsLogic {
	private static final Logger LOGGER = Logger
			.getLogger(HRISReportsLogicImpl.class);
	@Resource
	CompanyDAO companyDAO;
	@Resource
	EmployeeDAO employeeDAO;
	@Resource
	EmployeeLoginHistoryDAO employeeLoginHistoryDAO;
	@Resource
	CompanyGroupDAO companyGroupDAO;

	@Override
	public HRISReportDTO genHRISHeadcountReport(Long employeeId,
			Long companyId, HRISReportsForm hrisReportsForm, Boolean isManager) {
		HRISReportDTO leaveReportDTO = new HRISReportDTO();

		Employee employeeVO = employeeDAO.findById(employeeId);
		Company companyVO = companyDAO.findById(companyId);
		List<PayAsiaCompanyStatisticReportDTO> companyStatisticReportDTOs = companyDAO
				.getPayAsiaCompanyStatisticReport(
						hrisReportsForm.getAsOnDate(),
						companyVO.getDateFormat(),
						hrisReportsForm.getCompanyIds(),
						hrisReportsForm.isIncludeInactiveCompany());

		// BeanComparator beanComparator = new BeanComparator("companyGroup");
		// Collections.sort(companyStatisticReportDTOs, beanComparator);
		leaveReportDTO.setHrisHeadCountEmpDataList(companyStatisticReportDTOs);
		leaveReportDTO.setCreatedBy(getEmployeeName(employeeVO));

		return leaveReportDTO;
	}

	private String getEmployeeName(Employee employee) {
		String employeeName = employee.getFirstName();

		if (StringUtils.isNotBlank(employee.getMiddleName())) {
			employeeName = employeeName + " " + employee.getMiddleName();
		}
		if (StringUtils.isNotBlank(employee.getLastName())) {
			employeeName = employeeName + " " + employee.getLastName();
		}

		return employeeName;
	}

	@Override
	public List<EmployeeLoginHistoryReportDTO> genLoginDetailsReport(
			ClaimReportsForm claimReportsForm, Long companyId) {

		List<EmployeeLoginHistoryReportDTO> employeeLoginHistoryReportDTOList = new ArrayList<EmployeeLoginHistoryReportDTO>();

		String employeeIds = claimReportsForm.getEmployeeIds();

		Company company = companyDAO.findById(companyId);

		List<Long> employeeIdsLong = new ArrayList<Long>();

		if (employeeIds.length() > 0) {
			String[] employeeIdArray = employeeIds.split(",");
			for (int i = 0; i < employeeIdArray.length; i++) {
				employeeIdsLong.add(Long.parseLong(employeeIdArray[i]));
			}

			List<EmployeeLoginHistory> employeeLoginHistoryList = employeeLoginHistoryDAO
					.findByEmployeeIdAndDate(employeeIdsLong,
							claimReportsForm.getStartDate(),
							claimReportsForm.getEndDate(),
							company.getDateFormat());
			for (EmployeeLoginHistory employeeLoginHistory : employeeLoginHistoryList) {
				EmployeeLoginHistoryReportDTO employeeLoginHistoryReportDTO = new EmployeeLoginHistoryReportDTO();
				employeeLoginHistoryReportDTO
						.setEmployeeNumber(employeeLoginHistory.getEmployee()
								.getEmployeeNumber());
				employeeLoginHistoryReportDTO
						.setEmployeeName(getEmployeeName(employeeLoginHistory
								.getEmployee()));
				employeeLoginHistoryReportDTO
						.setCompanyName(employeeLoginHistory.getEmployee()
								.getCompany().getCompanyName());
				employeeLoginHistoryReportDTO.setLoggedInDate(DateUtils
						.timeStampToStringWithTime(
								employeeLoginHistory.getLoggedInDate(),
								company.getDateFormat()));
				employeeLoginHistoryReportDTO.setIPAddress(employeeLoginHistory
						.getIpAddress() == null ? "" : employeeLoginHistory
						.getIpAddress());

				employeeLoginHistoryReportDTOList
						.add(employeeLoginHistoryReportDTO);
			}

		}

		return employeeLoginHistoryReportDTOList;
	}

	@Override
	public List<RolePrivilegeReportDTO> genRolePrivilegeReport(Long employeeId,
			Long companyId, ClaimReportsForm claimReportsForm, Boolean isManager) {

		CompanyGroup companyGroup = companyGroupDAO
				.findByGroupName(claimReportsForm.getGroupId());

		List<RolePrivilegeReportDTO> rolePrivilegeReportDTOs = companyDAO
				.getEmployeeRolePrivilegeReport(
						claimReportsForm.getCompanyIds(),
						companyGroup.getGroupId());

		return rolePrivilegeReportDTOs;
	}

	@Override
	public SwitchCompanyResponse getSwitchCompanyList(PageRequest pageDTO,
			SortCondition sortDTO, Long employeeId, String searchCondition,
			String searchText, String groupName, Boolean includeInactiveCompany) {
		CompanyConditionDTO conditionDTO = new CompanyConditionDTO();
		if (StringUtils.isNotBlank(searchCondition)) {
			if (searchCondition.equals(PayAsiaConstants.COMPANY_NAME)) {
				if (StringUtils.isNotBlank(searchText)) {
					conditionDTO.setCompanyName(searchText.trim());
				}

			}
			if (searchCondition.equals(PayAsiaConstants.COMPANY_CODE)) {
				if (StringUtils.isNotBlank(searchText)) {
					conditionDTO.setCompanyCode(searchText.trim());
				}

			}
			if (searchCondition.equals(PayAsiaConstants.GROUP_CODE)) {
				if (StringUtils.isNotBlank(searchText)) {
					conditionDTO.setGroupCode(searchText.trim());
				}

			}
		}
		if (StringUtils.isNotBlank(groupName)) {
			conditionDTO.setGroupName(groupName.trim());
		}

		conditionDTO.setIncludeActiveStatus(true);
		if (includeInactiveCompany != null) {
			conditionDTO.setIncludeInactiveCompany(includeInactiveCompany);
		}

		conditionDTO.setIncludeDemoCompanyStatus(true);
		conditionDTO.setIncludeDemoCompany(false);

		List<SwitchCompanyForm> switchCompanyFormList = new ArrayList<SwitchCompanyForm>();
		SortCondition sortDTO1 = new SortCondition();
		sortDTO1.setColumnName(SortConstants.MANAGE_ROLES_GROUP_NAME);
		sortDTO1.setOrderType(SortConstants.DB_ORDER_BY_ASC);
		List<Tuple> companyListVO = companyDAO.findAssignedCompanyToUserByCond(
				sortDTO1, null, employeeId, conditionDTO);
		for (Tuple companyTuple : companyListVO) {
			SwitchCompanyForm switchCompanyForm = new SwitchCompanyForm();
			switchCompanyForm.setCompanyId((Long) companyTuple.get(
					getAlias(Company_.companyId), Long.class));
			switchCompanyForm.setCompanyName((String) companyTuple.get(
					getAlias(Company_.companyName), String.class));
			switchCompanyForm.setCompanyCode((String) companyTuple.get(
					getAlias(Company_.companyCode), String.class));
			switchCompanyForm.setGroupId((Long) companyTuple.get(
					getAlias(CompanyGroup_.groupId), Long.class));
			switchCompanyForm.setGroupName((String) companyTuple.get(
					getAlias(CompanyGroup_.groupName), String.class));
			switchCompanyForm.setGroupCode((String) companyTuple.get(
					getAlias(CompanyGroup_.groupCode), String.class));
			switchCompanyFormList.add(switchCompanyForm);

		}

		SwitchCompanyResponse response = new SwitchCompanyResponse();
		response.setSwitchCompanyFormList(switchCompanyFormList);

		return response;

	}

	@Override
	public EmployeeDetailsResponse getEmployeeList(String searchCondition,
			String searchText, String companyName) {

		EmployeeConditionDTO conditionDTO = new EmployeeConditionDTO();
		SortCondition sortDTO1 = new SortCondition();
		sortDTO1.setColumnName(SortConstants.EMPLOYEE_NUMBER);
		sortDTO1.setOrderType(SortConstants.DB_ORDER_BY_ASC);

		Company company = companyDAO.findByCompanyName(companyName);

		if (searchCondition.equals("employeeNumber")) {
			conditionDTO.setEmployeeNumber(searchText);
		} else if (searchCondition.equals("employeeName")) {
			conditionDTO.setEmployeeName(searchText);
		}

		List<Employee> employees = employeeDAO.findByCondition(conditionDTO,
				null, sortDTO1, company.getCompanyId());

		List<EmployeeDetailForm> employeeDetailForms = new ArrayList<EmployeeDetailForm>();

		for (Employee employee : employees) {
			EmployeeDetailForm employeeDetailForm = new EmployeeDetailForm();
			employeeDetailForm.setEmployeeNumber(employee.getEmployeeNumber());
			String fullName = employee.getFirstName()
					+ (employee.getLastName() != null ? (' ' + employee
							.getLastName()) : ' ');
			//employeeDetailForm.setOtherName(fullName);
			employeeDetailForm.setEmployeeId(String.valueOf(employee
					.getEmployeeId()));
			employeeDetailForms.add(employeeDetailForm);
		}

		EmployeeDetailsResponse employeeDetailsResponse = new EmployeeDetailsResponse();
		employeeDetailsResponse.setEmployeeDetailFormList(employeeDetailForms);
		return employeeDetailsResponse;
	}

}
