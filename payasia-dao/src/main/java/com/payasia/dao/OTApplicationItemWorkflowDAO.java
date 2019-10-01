package com.payasia.dao;

import com.payasia.dao.bean.OTApplicationItemWorkflow;

public interface OTApplicationItemWorkflowDAO {

	OTApplicationItemWorkflow findByID(long otApplicationItemWorkflowId);

	void delete(OTApplicationItemWorkflow otApplicationItemWorkflow);

	void save(OTApplicationItemWorkflow otApplicationItemWorkflow);

	void update(OTApplicationItemWorkflow otApplicationItemWorkflow);

}
