package com.payasia.dao.impl;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.OTApplicationItemWorkflowDAO;
import com.payasia.dao.bean.OTApplicationItemWorkflow;

@Repository
public class OTApplicationItemWorkflowDAOImpl extends BaseDAO implements
		OTApplicationItemWorkflowDAO {

	@Override
	protected Object getBaseEntity() {
		OTApplicationItemWorkflow otApplicationItemWorkflow = new OTApplicationItemWorkflow();
		return otApplicationItemWorkflow;
	}

	@Override
	public void update(OTApplicationItemWorkflow otApplicationItemWorkflow) {
		super.update(otApplicationItemWorkflow);

	}

	@Override
	public void save(OTApplicationItemWorkflow otApplicationItemWorkflow) {
		super.save(otApplicationItemWorkflow);
	}

	@Override
	public void delete(OTApplicationItemWorkflow otApplicationItemWorkflow) {
		super.delete(otApplicationItemWorkflow);

	}

	@Override
	public OTApplicationItemWorkflow findByID(long otApplicationItemWorkflowId) {
		return super.findById(OTApplicationItemWorkflow.class,
				otApplicationItemWorkflowId);
	}

}
