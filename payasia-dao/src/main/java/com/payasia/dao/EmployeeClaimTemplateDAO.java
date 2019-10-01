package com.payasia.dao;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import com.payasia.common.dto.AddClaimDTO;
import com.payasia.common.dto.AssignClaimTemplateConditionDTO;
import com.payasia.common.dto.ClaimReviewerConditionDTO;
import com.payasia.common.dto.EmployeeClaimTemplateDataDTO;
import com.payasia.common.dto.EmployeeHeadCountReportDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.ClaimTemplate;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeClaimTemplate;

public interface EmployeeClaimTemplateDAO {

	void update(EmployeeClaimTemplate employeeClaimTemplate);

	void save(EmployeeClaimTemplate employeeClaimTemplate);

	void delete(EmployeeClaimTemplate employeeClaimTemplate);

	EmployeeClaimTemplate findByID(long employeeClaimTemplateId);

	EmployeeClaimTemplate saveReturn(EmployeeClaimTemplate employeeClaimTemplate);

	List<EmployeeClaimTemplate> findByCondition(AssignClaimTemplateConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO, Long companyId);

	Path<String> getSortPathForSearchEmployee(SortCondition sortDTO, Root<EmployeeClaimTemplate> empRoot,
			Join<EmployeeClaimTemplate, Employee> empJoin,
			Join<EmployeeClaimTemplate, ClaimTemplate> claimTemplateJoin);

	Long getCountForCondition(AssignClaimTemplateConditionDTO conditionDTO, Long companyId);

	EmployeeClaimTemplate checkEmpClaimTemplateDate(Long employeeClaimTemplateId, Long employeeId, String date,
			String dateFormat);

	List<EmployeeClaimTemplate> findByCondition(ClaimReviewerConditionDTO claimReviewerConditionDTO);

	List<EmployeeClaimTemplate> checkEmpClaimTemplateByDate(Long empLeaveSchemeId, Long employeeId, String date,
			String dateFormat);

	EmployeeClaimTemplate checkEmployeeClaimTemplateByDate(Long empClaimTemplateId, Long employeeId, String date,
			String dateFormat);

	List<Long> getEmployeesOfClaimTemplate(Long claimTemplateId);

	EmployeeClaimTemplate findByEmpIdAndClaimTemplateId(Long employeeId, Long claimTemplateId);

	EmployeeClaimTemplate checkEmpClaimTemplateOverlap(Long claimTemplateId, Long employeeId, String startDate,
			String endDate, String dateFormat, Long employeeClaimTemplateId);

	EmployeeClaimTemplate checkEmployeeClaimTemplateyName(String claimTemplateName, Long employeeId, String date,
			String dateFormat);

	EmployeeClaimTemplate findByEmpIdAndEndDate(Long employeeId, Long claimTemplateId);

	List<EmployeeClaimTemplate> findByClaimTemplate(long claimTemplateId);

	List<EmployeeHeadCountReportDTO> getClaimHeadCountReportDetail(String startDate, String endDate, String dateFormat,
			String companyIdList);

	BigDecimal getTotalBalance(Long claimTemplateItemId, Long employeeId);

	EmployeeClaimTemplate findByEmployeeClaimTemplateID(AddClaimDTO addClaimDTO);

	List<EmployeeClaimTemplateDataDTO> getEmployeeTemplateData(Long companyId, Long employeeId);

}
