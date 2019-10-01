package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.LeaveApplicationCustomField;

public interface LeaveApplicationCustomFieldDAO {

	LeaveApplicationCustomField findById(Long empLeaveSchemeTypeId);

	void update(LeaveApplicationCustomField leaveApplicationCustomField);

	void save(LeaveApplicationCustomField leaveApplicationCustomField);

	List<LeaveApplicationCustomField> findByCondition(Long leaveApplicationId);

}
