package com.payasia.dao;

import com.payasia.dao.bean.LeaveSchemeTypeYearEnd;

public interface LeaveSchemeTypeYearEndDAO {

	LeaveSchemeTypeYearEnd findById(Long leaveSchemeTypeYearEndId);

	void update(LeaveSchemeTypeYearEnd leaveSchemeTypeYearEnd);

	void save(LeaveSchemeTypeYearEnd leaveSchemeTypeYearEnd);

	void delete(LeaveSchemeTypeYearEnd leaveSchemeTypeYearEnd);

	LeaveSchemeTypeYearEnd saveObj(LeaveSchemeTypeYearEnd schemeTypeYearEnd);

}
