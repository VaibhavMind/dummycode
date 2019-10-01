package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.LeaveSchemeTypeShortList;

public interface LeaveSchemeTypeShortListDAO {

	void save(LeaveSchemeTypeShortList leaveSchemeTypeShortList);

	void deleteByCondition(Long leaveSchemeTypeId);

	List<LeaveSchemeTypeShortList> findByCondition(Long leaveSchemeTypeId);

	LeaveSchemeTypeShortList findById(Long filterId);

	void delete(LeaveSchemeTypeShortList leaveSchemeTypeShortList);

	List<LeaveSchemeTypeShortList> findSchemeTypeByCompany(Long leaveSchemeTypeId, Long companyId);

}
