package com.payasia.logic;

import com.payasia.common.form.HRISPreferenceForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;

public interface HRISPreferenceLogic {

	HRISPreferenceForm getHRISPreference(Long companyId);

	void saveHRISPreference(HRISPreferenceForm hrisPreferenceForm,
			Long companyId);

	HRISPreferenceForm searchEmployee(PageRequest pageDTO,
			SortCondition sortDTO, Long employeeId, String empName,
			String empNumber, Long companyId);

}
