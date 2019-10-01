package com.payasia.dao;

import com.payasia.dao.bean.HRISPreference;

public interface HRISPreferenceDAO {

	HRISPreference findById(Long hrisPreferenceId);

	void save(HRISPreference hrisPreference);

	void delete(HRISPreference hrisPreference);

	void update(HRISPreference hrisPreference);

	HRISPreference findByCompanyId(Long companyId);

}
