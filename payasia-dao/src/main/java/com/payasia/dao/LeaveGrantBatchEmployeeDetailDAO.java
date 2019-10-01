package com.payasia.dao;

import java.util.List;

import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.LeaveGrantBatchEmployeeDetail;

public interface LeaveGrantBatchEmployeeDetailDAO {

	void update(LeaveGrantBatchEmployeeDetail leaveGrantBatchEmployeeDetail);

	void delete(LeaveGrantBatchEmployeeDetail leaveGrantBatchEmployeeDetail);

	void save(LeaveGrantBatchEmployeeDetail leaveGrantBatchEmployeeDetail);

	LeaveGrantBatchEmployeeDetail findByID(Long leaveGrantBatchEmployeeDetailId);

	Integer getCountByCondition(Long leaveGrantBatchDetailId);

	List<LeaveGrantBatchEmployeeDetail> findByCondition(
			Long leaveGrantBatchDetailId, String employeeNumber,
			PageRequest pageDTO, SortCondition sortDTO);

	LeaveGrantBatchEmployeeDetail findLeaveGrantEmpDetailByCompID(Long leaveGrantBatchEmpDetailId, Long companyId);

}
