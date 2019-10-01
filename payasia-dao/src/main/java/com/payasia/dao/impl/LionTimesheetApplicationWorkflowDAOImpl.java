package com.payasia.dao.impl;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.LionTimesheetApplicationWorkflowDAO;
import com.payasia.dao.bean.LionTimesheetApplicationWorkflow;

@Repository
public class LionTimesheetApplicationWorkflowDAOImpl extends BaseDAO implements
		LionTimesheetApplicationWorkflowDAO {

	@Override
	protected Object getBaseEntity() {

		LionTimesheetApplicationWorkflow lionTimesheetApplicationWorkflow = new LionTimesheetApplicationWorkflow();
		return lionTimesheetApplicationWorkflow;
	}

	@Override
	public void update(
			LionTimesheetApplicationWorkflow lionTimesheetApplicationWorkflow) {
		super.update(lionTimesheetApplicationWorkflow);
	}

	@Override
	public void delete(
			LionTimesheetApplicationWorkflow lionTimesheetApplicationWorkflow) {
		super.delete(lionTimesheetApplicationWorkflow);
	}

	@Override
	public void save(
			LionTimesheetApplicationWorkflow lionTimesheetApplicationWorkflow) {
		super.save(lionTimesheetApplicationWorkflow);
	}

	@Override
	public LionTimesheetApplicationWorkflow findById(Long lionReviewerId) {
		return super.findById(LionTimesheetApplicationWorkflow.class,
				lionReviewerId);

	}

}
