package com.payasia.logic;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.payasia.common.form.OTReviewerForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;

@Transactional
public interface OTReviewerLogic {

	List<OTReviewerForm> getOTTemplateList(Long companyId);

	List<OTReviewerForm> getWorkFlowRuleList();

	OTReviewerForm getOTReviewers(Long otTemplateId);

	void saveOTReviewer(OTReviewerForm otReviewerForm, Long companyId);

	OTReviewerForm getOTReviewers(String searchCondition, String searchText,
			PageRequest pageDTO, SortCondition sortDTO, Long companyId);

	OTReviewerForm getOTReviewerData(Long employeeId, Long companyId);

	void updateOTReviewer(OTReviewerForm otReviewerForm, Long companyId);

	void deleteOTReviewer(Long employeeId);

	OTReviewerForm checkEmployeeReviewer(Long employeeId, Long companyId);

}
