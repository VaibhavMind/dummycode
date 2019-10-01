package com.payasia.dao;

import com.payasia.dao.bean.EmployeePhoto;

/**
 * The Interface EmployeePhotoDAO.
 */
/**
 * @author vivekjain
 * 
 */
public interface EmployeePhotoDAO {

	/**
	 * Save EmployeePhoto Object.
	 * 
	 * @param employeePhoto
	 *            the employee photo
	 */
	void save(EmployeePhoto employeePhoto);

	/**
	 * Find EmployeePhoto Object by employee id.
	 * 
	 * @param employeeId
	 *            the employee id
	 * @return the employee photo
	 */
	EmployeePhoto findByEmployeeId(Long employeeId);

	/**
	 * Update EmployeePhoto Object.
	 * 
	 * @param employeePhoto
	 *            the employee photo
	 */
	void update(EmployeePhoto employeePhoto);

	void delete(EmployeePhoto employeePhoto);

	EmployeePhoto saveReturn(EmployeePhoto employeePhoto);
	
	EmployeePhoto findByEmployeeAndCompanyId(Long employeeId,Long companyid);

}
