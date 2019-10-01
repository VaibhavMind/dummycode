package com.payasia.dao;

import com.payasia.dao.bean.LeaveSchemeTypeGrantCondition;

public interface LeaveSchemeTypeGrantConditionDAO {

	void save(LeaveSchemeTypeGrantCondition leaveSchemeTypeGrantCondition);

	void deleteByAvailingId(Long leaveSchemeTypeAvailingId);

}
