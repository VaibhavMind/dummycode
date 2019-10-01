package com.payasia.dao;

import java.util.List;

import com.payasia.common.dto.ClaimReviewerConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.EmployeeClaimReviewer;

public interface EmployeeClaimReviewerDAO {

	void update(EmployeeClaimReviewer employeeClaimReviewer);

	void save(EmployeeClaimReviewer employeeClaimReviewer);

	void delete(EmployeeClaimReviewer employeeClaimReviewer);

	EmployeeClaimReviewer findById(long employeeClaimReviewerId);

	List<EmployeeClaimReviewer> findByCondition(
			ClaimReviewerConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO, Long companyId);

	int getCountByCondition(ClaimReviewerConditionDTO conditionDTO,
			Long companyId);

	List<EmployeeClaimReviewer> findByEmployeeIdCompanyId(Long employeeId,
			Long companyId);

	List<EmployeeClaimReviewer> findByClaimTemplateIdAndWorkFlowId(
			Long claimTemplateId, Long workFlowRuleId);

	List<EmployeeClaimReviewer> findByClaimTemplateIdAndEmpId(
			Long employeeClaimTemplateId, Long employeeId);

	List<EmployeeClaimReviewer> checkEmployeeClaimReviewer(Long employeeId);

	List<EmployeeClaimReviewer> checkExistingClaimReviewer(Long employeeId,
			Long companyId, Long employeeClaimTemplateId);

	List<EmployeeClaimReviewer> findByCondition(Long employeeId,
			Long employeeClaimTemplateId, Long companyId);

	void deleteByCondition(Long employeeId, Long employeeClaimTemplateId);

}
