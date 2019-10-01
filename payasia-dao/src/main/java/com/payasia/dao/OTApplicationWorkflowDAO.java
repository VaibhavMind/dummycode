package com.payasia.dao;

import com.payasia.dao.bean.OTApplicationWorkflow;

public interface OTApplicationWorkflowDAO {

	OTApplicationWorkflow findByID(long otApplicationWorkflowId);

	void delete(OTApplicationWorkflow otApplicationWorkflow);

	void save(OTApplicationWorkflow otApplicationWorkflow);

	void update(OTApplicationWorkflow otApplicationWorkflow);

}
