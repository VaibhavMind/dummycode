package com.payasia.dao;

import java.util.List;

import com.payasia.common.dto.OTReviewerConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.EmployeeOTReviewer;

public interface EmployeeOTReviewerDAO {

	void update(EmployeeOTReviewer employeeOTReviewer);

	void save(EmployeeOTReviewer employeeOTReviewer);

	void delete(EmployeeOTReviewer employeeOTReviewer);

	EmployeeOTReviewer findById(long employeeOTReviewerId);

	void deleteByCondition(Long employeeId);

	List<EmployeeOTReviewer> findByCondition(
			OTReviewerConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO, Long companyId);

	int getCountByCondition(OTReviewerConditionDTO conditionDTO, Long companyId);

	List<EmployeeOTReviewer> findByEmployeeIdCompanyId(Long employeeId,
			Long companyId);

	List<EmployeeOTReviewer> findByOTTemplateIdAndWorkFlowId(Long otTemplateId,
			Long workFlowRuleId);

}
