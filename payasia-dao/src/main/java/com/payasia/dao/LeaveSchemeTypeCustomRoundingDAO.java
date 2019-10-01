package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.LeaveSchemeTypeCustomRounding;

public interface LeaveSchemeTypeCustomRoundingDAO {

	void save(LeaveSchemeTypeCustomRounding leaveSchemeTypeCustomRounding);

	void deleteByCondition(Long leaveSchemeTypeProrationId);

	List<LeaveSchemeTypeCustomRounding> findByLeaveSchemeTypeProrationId(
			long leaveSchemeTypeProrationId);

}
