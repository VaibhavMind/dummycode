package com.payasia.logic.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.payasia.common.form.EmployeePreferencesForm;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.EmployeePreferenceMasterDAO;
import com.payasia.dao.EmployeeTypeMasterDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.EmployeePreferenceMaster;
import com.payasia.dao.bean.EmployeeTypeMaster;
import com.payasia.logic.EmployeePreferencesLogic;

/**
 * The Class EmployeePreferencesLogicImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Component
public class EmployeePreferencesLogicImpl implements EmployeePreferencesLogic {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(EmployeePreferencesLogicImpl.class);

	/** The company dao. */
	@Resource
	CompanyDAO companyDAO;

	/** The employee type master dao. */
	@Resource
	EmployeeTypeMasterDAO employeeTypeMasterDAO;

	/** The employee preference master dao. */
	@Resource
	EmployeePreferenceMasterDAO employeePreferenceMasterDAO;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.EmployeePreferencesLogic#employeePreferencesSave(com
	 * .payasia.common.form.EmployeePreferencesForm, java.lang.Long)
	 */
	@Override
	public void employeePreferencesSave(
			EmployeePreferencesForm employeePreferencesForm, Long companyId) {
		EmployeePreferenceMaster employeePreferenceMaster = new EmployeePreferenceMaster();

		Company company = companyDAO.findById(companyId);
		employeePreferenceMaster.setCompany(company);

		employeePreferenceMaster.setProbationPeriod(employeePreferencesForm
				.getProbationPeriod());
		employeePreferenceMaster
				.setAllowFutureDateJoining(employeePreferencesForm
						.isAllowFutureDateJoining());
		employeePreferenceMaster
				.setDisplayDirectIndirectEmployees(employeePreferencesForm
						.isDisplayDirectIndirectEmployee());
		employeePreferenceMaster
				.setDisplayResignedEmployees(employeePreferencesForm
						.isDisplayResignedEmployee());

		EmployeeTypeMaster employeeTypeMaster = employeeTypeMasterDAO
				.findById(employeePreferencesForm.getEmployeeTypeId());
		employeePreferenceMaster.setEmployeeTypeMaster(employeeTypeMaster);

		employeePreferenceMasterDAO.save(employeePreferenceMaster);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.EmployeePreferencesLogic#getDefaultEmpStatus(java.lang
	 * .Long)
	 */
	@Override
	public List<EmployeePreferencesForm> getDefaultEmpStatus(Long companyId) {
		List<EmployeePreferencesForm> employeePreferencesFormList = new ArrayList<EmployeePreferencesForm>();
		List<EmployeeTypeMaster> employeeTypeMasterList = employeeTypeMasterDAO
				.findByConditionCompany(companyId);

		for (EmployeeTypeMaster employeeTypeMaster : employeeTypeMasterList) {
			EmployeePreferencesForm employeePreferencesForm = new EmployeePreferencesForm();
			try {
				employeePreferencesForm.setEmployeeType(URLEncoder.encode(
						employeeTypeMaster.getEmpType(), "UTF-8"));
			} catch (UnsupportedEncodingException ex) {
				LOGGER.error(ex.getMessage(), ex);
			}
			employeePreferencesForm.setEmployeeTypeId(employeeTypeMaster
					.getEmpTypeId());
			employeePreferencesFormList.add(employeePreferencesForm);
		}

		return employeePreferencesFormList;

	}

}