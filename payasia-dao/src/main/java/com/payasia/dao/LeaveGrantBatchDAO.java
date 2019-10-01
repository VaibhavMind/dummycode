package com.payasia.dao;

import java.util.List;

import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.LeaveGrantBatch;

public interface LeaveGrantBatchDAO {

	void update(LeaveGrantBatch leaveGrantBatch);

	void delete(LeaveGrantBatch leaveGrantBatch);

	void save(LeaveGrantBatch leaveGrantBatch);

	LeaveGrantBatch findByID(Long leaveGrantBatchId);

	Boolean callDeleteLeaveGrantBatchProc(Long leaveGrantBatchDetailID,
			Long leaveGrantBatchEmployeeDetailID);

	List<LeaveGrantBatch> findByCondition(Long companyId, String fromDate,
			String toDate, Long leaveType, PageRequest pageDTO,
			SortCondition sortDTO);

	Integer getCountByCondition(Long companyId, String fromDate, String toDate,
			Long leaveType);

}
