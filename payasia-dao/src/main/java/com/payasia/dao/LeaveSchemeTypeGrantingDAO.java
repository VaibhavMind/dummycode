package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.LeaveSchemeTypeGranting;

public interface LeaveSchemeTypeGrantingDAO {

	LeaveSchemeTypeGranting findById(Long leaveSchemeTypeGrantingId);

	void update(LeaveSchemeTypeGranting leaveSchemeTypeGranting);

	void save(LeaveSchemeTypeGranting leaveSchemeTypeGranting);

	void delete(LeaveSchemeTypeGranting leaveSchemeTypeGranting);

	LeaveSchemeTypeGranting saveObj(
			LeaveSchemeTypeGranting leaveSchemeTypeGranting);

	List<LeaveSchemeTypeGranting> findByCondition(Long leaveSchemeTypeId);

}
