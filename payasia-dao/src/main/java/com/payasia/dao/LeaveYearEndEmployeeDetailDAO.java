package com.payasia.dao;

import java.util.List;

import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.LeaveYearEndEmployeeDetail;

public interface LeaveYearEndEmployeeDetailDAO {

	List<LeaveYearEndEmployeeDetail> findByCondition(Long leaveYearEndBatchId,
			String employeeNumber, PageRequest pageDTO, SortCondition sortDTO);

	Integer getCountByCondition(Long leaveYearEndBatchId, String employeeNumber);

	LeaveYearEndEmployeeDetail findByID(long leaveYearEndEmployeeDetailId);

	void update(LeaveYearEndEmployeeDetail leaveYearEndEmployeeDetail);

}
