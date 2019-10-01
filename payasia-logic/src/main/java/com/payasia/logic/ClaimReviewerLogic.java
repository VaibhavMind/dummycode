package com.payasia.logic;

import java.util.List;

import javax.security.sasl.AuthenticationException;

import org.springframework.transaction.annotation.Transactional;

import com.payasia.common.form.ClaimReviewerForm;
import com.payasia.common.form.LeaveReviewerResponseForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;

@Transactional
public interface ClaimReviewerLogic {

	List<ClaimReviewerForm> getWorkFlowRuleList();

	List<ClaimReviewerForm> getClaimTemplateList(Long companyId);

	void saveClaimReviewer(ClaimReviewerForm claimReviewerForm, Long companyId) throws AuthenticationException;

	ClaimReviewerForm getClaimReviewers(String searchCondition, String searchText, PageRequest pageDTO,
			SortCondition sortDTO, Long companyId);

	ClaimReviewerForm getClaimReviewerData(Long filterIds[], Long companyId);

	ClaimReviewerForm searchEmployee(PageRequest pageDTO, SortCondition sortDTO, String empName, String empNumber,
			Long companyId, Long employeeId);

	List<ClaimReviewerForm> getClaimTemplateList(Long companyId, Long employeeId);

	ClaimReviewerForm checkClaimReviewer(Long employeeId, Long companyId, ClaimReviewerForm claimReviewerForm);

	void deleteClaimReviewer(Long[] employeeIds);

	void updateClaimReviewer(ClaimReviewerForm claimReviewerForm, Long companyId) throws AuthenticationException;

	ClaimReviewerForm importClaimReviewer(ClaimReviewerForm claimReviewerForm, Long companyId);

	ClaimReviewerForm getClaimReviewers(Long claimTemplateId, Long companyId);

	LeaveReviewerResponseForm searchEmployeeByCompany(PageRequest pageDTO, SortCondition sortDTO, String empName,
			String empNumber, Long companyId, Long employeeId);

}
