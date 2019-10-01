package com.payasia.dao;

import com.payasia.dao.bean.LionTimesheetApplicationWorkflow;

public interface LionTimesheetApplicationWorkflowDAO {

	void update(
			LionTimesheetApplicationWorkflow lionTimesheetApplicationWorkflow);

	void delete(
			LionTimesheetApplicationWorkflow lionTimesheetApplicationWorkflow);

	void save(LionTimesheetApplicationWorkflow lionTimesheetApplicationWorkflow);

	LionTimesheetApplicationWorkflow findById(Long lionReviewerId);

}
