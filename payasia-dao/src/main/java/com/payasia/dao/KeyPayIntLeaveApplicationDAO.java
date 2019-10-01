package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.IntegrationMaster;
import com.payasia.dao.bean.KeyPayIntLeaveApplication;

public interface KeyPayIntLeaveApplicationDAO {

	void save(KeyPayIntLeaveApplication keyPayIntLeaveApplication);

	Long getMaxApprovedLeaveAppId(Long companyId);

	void update(KeyPayIntLeaveApplication keyPayIntLeaveApplication);

	List<KeyPayIntLeaveApplication> findByCondition(int syncStatus, String leaveStatus, Long companyId);

	KeyPayIntLeaveApplication findByLeaveAppId(Long companyId, Long leaveApplicationId, String leaveStatus);

	IntegrationMaster findByKeyPayDetailByCompanyId(Long companyId);

}
