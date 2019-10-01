package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.LeaveSchemeTypeCustomField;

public interface LeaveSchemeTypeCustomFieldDAO {

	void save(LeaveSchemeTypeCustomField leaveSchemeTypeCustomField);

	void delete(LeaveSchemeTypeCustomField leaveSchemeTypeCustomField);

	void update(LeaveSchemeTypeCustomField leaveSchemeTypeCustomField);

	LeaveSchemeTypeCustomField findById(long customRoundFieldId);

	List<LeaveSchemeTypeCustomField> findByLeaveSchemeTypeAvailingLeaveId(
			long leaveSchemeTypeAvailingLeaveId);

}
