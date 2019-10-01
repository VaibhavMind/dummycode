package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.LeaveSchemeTypeAvailingLeave;

public interface LeaveSchemeTypeAvailingLeaveDAO {

	LeaveSchemeTypeAvailingLeave findById(Long leaveSchemeTypeAvailingLeaveId);

	void update(LeaveSchemeTypeAvailingLeave leaveSchemeTypeAvailingLeave);

	void save(LeaveSchemeTypeAvailingLeave leaveSchemeTypeAvailingLeave);

	void delete(LeaveSchemeTypeAvailingLeave leaveSchemeTypeAvailingLeave);

	LeaveSchemeTypeAvailingLeave saveObj(LeaveSchemeTypeAvailingLeave availingLeave);

	LeaveSchemeTypeAvailingLeave findByLeaveSchemeType(Long leaveSchemeTypeId);

	List<LeaveSchemeTypeAvailingLeave> findByCompanyId(Long companyId);

	LeaveSchemeTypeAvailingLeave findByLeaveAppliationId(Long leaveApplicationId);

}
