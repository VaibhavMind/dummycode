package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.EmployeeHRISReviewer;

public interface EmployeeHRISReviewerDAO {

	EmployeeHRISReviewer findById(Long employeeHRISReviewerId);

	void save(EmployeeHRISReviewer employeeHRISReviewer);

	void delete(EmployeeHRISReviewer employeeHRISReviewer);

	void update(EmployeeHRISReviewer employeeHRISReviewer);

	List<EmployeeHRISReviewer> findByCondition(Long employeeId, Long companyId);

	void deleteByCondition(Long employeeId);

	List<EmployeeHRISReviewer> checkEmployeeHRISReviewer(Long employeeId);

	EmployeeHRISReviewer findByWorkFlowCondition(Long employeeId,
			Long workFlowRuleId);
	EmployeeHRISReviewer findById(Long employeeHRISReviewerId,Long companyId);
	
	void deleteByCondition(Long employeeId,Long companyId);
	

}
