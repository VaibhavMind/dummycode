package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.TimesheetWorkflow;

/**
 * @author vivekjain
 * 
 */
public interface TimesheetWorkflowDAO {

	TimesheetWorkflow findById(Long ingersollOTWorkflowId);

	List<TimesheetWorkflow> findByCompanyId(Long companyId);

	void deleteByCondition(Long otWorkflowId);

	void delete(TimesheetWorkflow lundinWorkflow);

	void update(TimesheetWorkflow lundinWorkflow);

	void save(TimesheetWorkflow lundinWorkflow);

}
