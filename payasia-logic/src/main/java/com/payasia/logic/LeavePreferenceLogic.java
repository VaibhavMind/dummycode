package com.payasia.logic;

import java.util.List;

import com.payasia.common.form.EmployeeFilterListForm;
import com.payasia.common.form.LeavePreferenceForm;

public interface LeavePreferenceLogic {

	LeavePreferenceForm getLeavePreference(Long companyId);

	void saveLeavePreference(LeavePreferenceForm leavePreferenceForm,
			Long companyId);

	List<LeavePreferenceForm> getMonthList();

	List<LeavePreferenceForm> getLeaveTransactionList();

	List<EmployeeFilterListForm> getEmployeeFilterList(Long companyId);

}
