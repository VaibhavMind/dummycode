package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.EmployeeTypeMaster;

/**
 * The Interface EmployeeTypeMasterDAO.
 */
/**
 * @author vivekjain
 * 
 */
public interface EmployeeTypeMasterDAO {

	/**
	 * Find EmployeeTypeMaster Objects List by condition company.
	 * 
	 * @param companyId
	 *            the company id
	 * @return the list
	 */
	List<EmployeeTypeMaster> findByConditionCompany(long companyId);

	/**
	 * Find EmployeeTypeMaster Object by Employee Type id.
	 * 
	 * @param empTypeId
	 *            the emp type id
	 * @return the employee type master
	 */
	EmployeeTypeMaster findById(long empTypeId);
}
