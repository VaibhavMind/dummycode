package com.payasia.dao;

import com.payasia.dao.bean.CoherentOvertimeApplicationWorkflow;

public interface CoherentOvertimeApplicationWorkflowDAO {

	CoherentOvertimeApplicationWorkflow findById(long id);

	void save(
			CoherentOvertimeApplicationWorkflow coherentOvertimeApplicationWorkflow);

	void update(
			CoherentOvertimeApplicationWorkflow coherentOvertimeApplicationWorkflow);

	CoherentOvertimeApplicationWorkflow saveAndReturn(
			CoherentOvertimeApplicationWorkflow coherentOvertimeApplicationWorkflow);

	CoherentOvertimeApplicationWorkflow findByCondition(Long otTimesheetId,
			Long createdById);

	void delete(
			CoherentOvertimeApplicationWorkflow coherentOvertimeApplicationWorkflow);

}
