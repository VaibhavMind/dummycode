package com.payasia.dao;

import java.util.List;

import javax.persistence.Tuple;

import com.payasia.common.dto.EmployeeConditionDTO;
import com.payasia.common.dto.EmployeeShortListDTO;
import com.payasia.dao.bean.EmployeeTimesheetReviewer;

public interface EmployeeTimesheetReviewerDAO {
	List<EmployeeTimesheetReviewer> findByCondition(Long employeeId,
			Long companyId);

	void deleteByCondition(Long employeeId);

	List<EmployeeTimesheetReviewer> checkEmployeeOTReviewer(Long employeeId);

	EmployeeTimesheetReviewer findByWorkFlowCondition(Long employeeId,
			Long workFlowRuleId);

	void update(EmployeeTimesheetReviewer lundinEmployeeReviewer);

	void delete(EmployeeTimesheetReviewer lundinEmployeeReviewer);

	void save(EmployeeTimesheetReviewer lundinEmployeeReviewer);

	EmployeeTimesheetReviewer findById(Long ingersollEmployeeOTReviewerId);

	EmployeeTimesheetReviewer findByEmployeeId(Long employeeId);

	List<Tuple> getEmployeeReviewerList(Long companyId);

	EmployeeTimesheetReviewer findByReviewerId(Long reviewerId);

	List<Tuple> getEmployeeListByManager(Long companyId, Long employeeId);

	List<Tuple> getEmployeeIdsTupleForManager(Long companyId, Long employeeId,
			EmployeeShortListDTO employeeShortListDTO);

	List<Tuple> getEmployeeIdsTupleForTimesheetReviewer(
			EmployeeConditionDTO conditionDTO, Long companyId, Long employeeId,
			EmployeeShortListDTO employeeShortListDTO);
	
	void deleteByCondition(Long employeeId, Long companyId);
	

}
