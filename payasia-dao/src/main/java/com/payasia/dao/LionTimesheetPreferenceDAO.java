package com.payasia.dao;

import com.payasia.dao.bean.LionTimesheetPreference;

public interface LionTimesheetPreferenceDAO {

	void save(LionTimesheetPreference lionTimesheetPreference);
	
	void update(LionTimesheetPreference lionTimesheetPreference);

	LionTimesheetPreference findByCompanyId(Long companyId);

	
}
