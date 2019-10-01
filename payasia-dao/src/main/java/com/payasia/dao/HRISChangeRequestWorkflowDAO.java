/**
 * @author vivekjain
 * 
 */
package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.HRISChangeRequestWorkflow;

public interface HRISChangeRequestWorkflowDAO {

	HRISChangeRequestWorkflow findById(Long hrisChangeRequestWorkflowId);

	void save(HRISChangeRequestWorkflow hrisChangeRequestWorkflow);

	void delete(HRISChangeRequestWorkflow hrisChangeRequestWorkflow);

	void update(HRISChangeRequestWorkflow hrisChangeRequestWorkflow);

	HRISChangeRequestWorkflow findByCondition(Long hrisChangeReqId,
			Long createdById, List<String> hrisStatusList);

	HRISChangeRequestWorkflow savePersist(
			HRISChangeRequestWorkflow hrisChangeRequestWorkflow);

	HRISChangeRequestWorkflow findByCondition(Long hrisChangeReqId,
			Long createdById);

	HRISChangeRequestWorkflow findByConditionChangeRequest(
			Long hrisChangeReqId, Long createdById);

}
