package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.LeaveSchemeTypeCustomProration;

public interface LeaveSchemeTypeCustomProrationDAO {

	void update(LeaveSchemeTypeCustomProration leaveSchemeTypeCustomProration);

	void save(LeaveSchemeTypeCustomProration leaveSchemeTypeCustomProration);

	void delete(LeaveSchemeTypeCustomProration leaveSchemeTypeCustomProration);

	LeaveSchemeTypeCustomProration findByID(
			long leaveSchemeTypeCustomProrationId);

	void deleteByCondition(Long leaveSchemeTypeProrationId);

	List<LeaveSchemeTypeCustomProration> findByLeaveSchemeTypeProrationId(
			long leaveSchemeTypeProrationId);

}
