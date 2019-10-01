package com.payasia.dao.impl;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.EmployeePreferenceMasterDAO;
import com.payasia.dao.bean.EmployeePreferenceMaster;

/**
 * The Class EmployeePreferenceMasterDAOImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Repository
public class EmployeePreferenceMasterDAOImpl extends BaseDAO implements
		EmployeePreferenceMasterDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.BaseDAO#getBaseEntity()
	 */
	@Override
	protected Object getBaseEntity() {
		EmployeePreferenceMaster employeePreferenceMaster = new EmployeePreferenceMaster();
		return employeePreferenceMaster;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmployeePreferenceMasterDAO#save(com.payasia.dao.bean
	 * .EmployeePreferenceMaster)
	 */
	@Override
	public void save(EmployeePreferenceMaster employeePreferenceMaster) {
		super.save(employeePreferenceMaster);
	}

}
