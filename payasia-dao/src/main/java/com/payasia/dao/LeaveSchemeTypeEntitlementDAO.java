package com.payasia.dao;

import java.math.BigDecimal;
import java.util.List;

import com.payasia.dao.bean.LeaveSchemeTypeEntitlement;

public interface LeaveSchemeTypeEntitlementDAO {

	void save(LeaveSchemeTypeEntitlement leaveSchemeTypeEntitlement);

	void deleteByCondition(long leaveSchemeTypeGrantingId);

	LeaveSchemeTypeEntitlement findById(Long leaveSchemeTypeDetailId);

	void update(LeaveSchemeTypeEntitlement leaveSchemeTypeEntitlement);

	List<LeaveSchemeTypeEntitlement> findByLeaveSchemeTypeGrantingId(
			long leaveSchemeTypeGrantingId);

	BigDecimal findMaxDays(Long leaveGrantingId);

}
