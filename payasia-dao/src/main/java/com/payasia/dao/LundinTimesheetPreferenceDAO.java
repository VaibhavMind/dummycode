package com.payasia.dao;

import com.payasia.dao.bean.LundinTimesheetPreference;

public interface LundinTimesheetPreferenceDAO {

	void delete(LundinTimesheetPreference lundinTimesheetPreference);

	void save(LundinTimesheetPreference lundinTimesheetPreference);

	void update(LundinTimesheetPreference lundinTimesheetPreference);

	LundinTimesheetPreference findByID(Long lundinTimesheetPreferenceId);

	LundinTimesheetPreference findByCompanyId(Long companyId);

}
