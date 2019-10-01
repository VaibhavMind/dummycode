package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.HRISChangeWorkflow;

/**
 * @author vivekjain
 * 
 */
public interface HRISChangeWorkflowDAO {

	HRISChangeWorkflow findById(Long hrisChangeWorkflowId);

	void save(HRISChangeWorkflow hrisChangeWorkflow);

	void delete(HRISChangeWorkflow hrisChangeWorkflow);

	void update(HRISChangeWorkflow hrisChangeWorkflow);

	List<HRISChangeWorkflow> findByCompanyId(Long companyId);

	void deleteByCondition(Long companyId);

}
