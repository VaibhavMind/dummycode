package com.payasia.dao;

import com.payasia.dao.bean.CoherentShiftApplicationWorkflow;

public interface CoherentShiftApplicationWorkflowDAO {

	CoherentShiftApplicationWorkflow findById(long id);

	void save(CoherentShiftApplicationWorkflow coherentShiftApplicationWorkflow);

	CoherentShiftApplicationWorkflow saveAndReturn(
			CoherentShiftApplicationWorkflow coherentShiftApplicationWorkflow);

	void update(
			CoherentShiftApplicationWorkflow coherentShiftApplicationWorkflow);

	CoherentShiftApplicationWorkflow findByCondition(Long shiftApplicationID,
			Long createdById);

	void delete(
			CoherentShiftApplicationWorkflow coherentShiftApplicationWorkflow);

}
