package com.payasia.dao;

import com.payasia.dao.bean.TimesheetApplicationWorkflow;

public interface TimesheetApplicationWorkflowDAO {
	void save(TimesheetApplicationWorkflow lundinTimesheetWorkflow);

	TimesheetApplicationWorkflow findByCondition(Long timesheetId, Long createdById);

	void deleteByCondition(Long timesheetId);

	TimesheetApplicationWorkflow saveReturn(
			TimesheetApplicationWorkflow lundinTimesheetWorkflow);

	void delete(TimesheetApplicationWorkflow workflow);
}
