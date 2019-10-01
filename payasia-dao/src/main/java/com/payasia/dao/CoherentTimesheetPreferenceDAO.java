package com.payasia.dao;

import com.payasia.dao.bean.CoherentTimesheetPreference;

public interface CoherentTimesheetPreferenceDAO {

	CoherentTimesheetPreference findByCompanyId(Long companyId);

	void save(CoherentTimesheetPreference coherentTimesheetPreference);

	void delete(CoherentTimesheetPreference coherentTimesheetPreference);

	void update(CoherentTimesheetPreference coherentTimesheetPreference);

	CoherentTimesheetPreference findByID(Long coherentTimesheetPreference);

}
