package com.payasia.logic;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.payasia.common.form.EmployeePreferencesForm;

@Transactional
public interface EmployeePreferencesLogic {
	/**
	 * purpose : Save Employee Preferences.
	 * 
	 * @param EmployeePreferencesForm
	 *            the employeePreferencesForm
	 * @param Long
	 *            the companyId
	 */
	void employeePreferencesSave(
			EmployeePreferencesForm employeePreferencesForm, Long companyId);

	/**
	 * purpose : Get Default Employee Status.
	 * 
	 * @param Long
	 *            the companyId
	 * @return EmployeePreferencesForm contains List
	 */
	List<EmployeePreferencesForm> getDefaultEmpStatus(Long companyId);

}