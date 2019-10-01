package com.payasia.dao;

import com.payasia.dao.bean.ClaimPreference;

public interface ClaimPreferenceDAO {

	void update(ClaimPreference claimPreference);

	void delete(ClaimPreference claimPreference);

	void save(ClaimPreference claimPreference);

	ClaimPreference findByCompanyId(Long companyId);

	ClaimPreference findByID(Long claimPreferenceId);

	ClaimPreference saveReturn(ClaimPreference claimPreference);

}
