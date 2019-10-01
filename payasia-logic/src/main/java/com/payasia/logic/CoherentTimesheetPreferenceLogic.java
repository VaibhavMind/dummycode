package com.payasia.logic;

import com.payasia.common.form.ClaimReviewerForm;
import com.payasia.common.form.CoherentOTEmployeeListForm;
import com.payasia.common.form.CoherentTimesheetPreferenceForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;

public interface CoherentTimesheetPreferenceLogic {

	void saveCoherentTimesheetPreference(
			CoherentTimesheetPreferenceForm coherentTimesheetPreferenceForm,
			Long companyId);

	CoherentTimesheetPreferenceForm getCoherentTimesheetPreference(
			Long companyId);

	ClaimReviewerForm searchEmployee(PageRequest pageDTO,
			SortCondition sortDTO, String empName, String empNumber,
			Long companyId, Long employeeId);

	String saveCoherentOTEmployee(Long otEmployeeId, Long employeeId,
			Long companyId);

	CoherentOTEmployeeListForm searchCoherentSelectOTEmployees(Long companyId,
			String searchCondition, String searchText);

	void deleteCoherentOTEmployee(String employeeId);

}
