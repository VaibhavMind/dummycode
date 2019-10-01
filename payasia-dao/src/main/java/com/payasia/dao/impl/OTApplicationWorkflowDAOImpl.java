package com.payasia.dao.impl;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.OTApplicationWorkflowDAO;
import com.payasia.dao.bean.OTApplicationWorkflow;

@Repository
public class OTApplicationWorkflowDAOImpl extends BaseDAO implements
		OTApplicationWorkflowDAO {

	@Override
	protected Object getBaseEntity() {
		OTApplicationWorkflow otApplicationWorkflow = new OTApplicationWorkflow();
		return otApplicationWorkflow;
	}

	@Override
	public void update(OTApplicationWorkflow otApplicationWorkflow) {
		super.update(otApplicationWorkflow);

	}

	@Override
	public void save(OTApplicationWorkflow otApplicationWorkflow) {
		super.save(otApplicationWorkflow);
	}

	@Override
	public void delete(OTApplicationWorkflow otApplicationWorkflow) {
		super.delete(otApplicationWorkflow);

	}

	@Override
	public OTApplicationWorkflow findByID(long otApplicationWorkflowId) {
		return super.findById(OTApplicationWorkflow.class,
				otApplicationWorkflowId);
	}
}
