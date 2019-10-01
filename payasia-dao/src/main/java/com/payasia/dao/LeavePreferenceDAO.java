package com.payasia.dao;

import java.util.Date;

import com.payasia.common.dto.GrantEmployeeLeaveVO;
import com.payasia.dao.bean.LeaveApplicationWorkflow;
import com.payasia.dao.bean.LeavePreference;

public interface LeavePreferenceDAO {

	void update(LeavePreference leavePreference);

	void delete(LeavePreference leavePreference);

	void save(LeavePreference leavePreference);

	LeavePreference findByID(Long leavePreferenceId);

	LeavePreference findByCompanyId(Long companyId);

	void callGrantEmployeeLeaveProc(GrantEmployeeLeaveVO grantEmployeeLeaveVO);

	void callForfeitProc(Long companyId, Date currentDate);

	LeaveApplicationWorkflow findWorkFlowByStatusAndLeaveAppId(long statusId, Long leaveApplicationId);

}
