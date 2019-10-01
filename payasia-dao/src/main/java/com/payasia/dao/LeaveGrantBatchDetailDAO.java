package com.payasia.dao;

import com.payasia.dao.bean.LeaveGrantBatchDetail;

public interface LeaveGrantBatchDetailDAO {

	void update(LeaveGrantBatchDetail leaveGrantBatchDetail);

	void delete(LeaveGrantBatchDetail leaveGrantBatchDetail);

	void save(LeaveGrantBatchDetail leaveGrantBatchDetail);

	LeaveGrantBatchDetail findByID(Long leaveGrantBatchDetailId);

	LeaveGrantBatchDetail findLeaveGrantBranchDetailByCompID(Long leaveGrantBatchDetailId, Long companyId);

}
