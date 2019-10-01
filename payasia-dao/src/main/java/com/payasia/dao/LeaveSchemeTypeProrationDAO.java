package com.payasia.dao;

import com.payasia.dao.bean.LeaveSchemeTypeProration;

public interface LeaveSchemeTypeProrationDAO {

	void delete(LeaveSchemeTypeProration leaveSchemeTypeProration);

	void save(LeaveSchemeTypeProration leaveSchemeTypeProration);

	void update(LeaveSchemeTypeProration leaveSchemeTypeProration);

	LeaveSchemeTypeProration findById(Long leaveSchemeTypeProrationId);

	LeaveSchemeTypeProration saveObj(
			LeaveSchemeTypeProration schemeTypeProration);

}
