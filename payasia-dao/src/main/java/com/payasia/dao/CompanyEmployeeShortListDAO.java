package com.payasia.dao;

import java.util.List;

import javax.persistence.Tuple;

import com.payasia.dao.bean.CompanyEmployeeShortList;

/**
 * @author vivekjain
 * 
 */
/**
 * The Interface CompanyEmployeeShortListDAO.
 */
public interface CompanyEmployeeShortListDAO {
	/**
	 * purpose : find CompanyEmployeeShortList Objects List By Condition
	 * employeeId ,roleId and companyId.
	 * 
	 * @param Long
	 *            the employeeId
	 * @param Long
	 *            the roleId
	 * @param Long
	 *            the companyId
	 * @return CompanyEmployeeShortList List
	 */
	List<CompanyEmployeeShortList> findByCondition(Long employeeId,
			Long roleId, Long companyId);

	/**
	 * purpose : update CompanyEmployeeShortList Object.
	 * 
	 * @param CompanyEmployeeShortList
	 *            the companyEmployeeShortListId
	 */
	void update(CompanyEmployeeShortList companyEmployeeShortListId);

	/**
	 * purpose : find CompanyEmployeeShortList Object By
	 * companyEmployeeShortListId.
	 * 
	 * @param long the companyEmployeeShortListId
	 * @return CompanyEmployeeShortList EntityBean
	 */
	CompanyEmployeeShortList findById(long companyEmployeeShortListId);

	/**
	 * purpose : delete CompanyEmployeeShortList Object.
	 * 
	 * @param CompanyEmployeeShortList
	 *            the companyEmployeeShortListId
	 */
	void delete(CompanyEmployeeShortList companyEmployeeShortList);

	/**
	 * purpose : delete CompanyEmployeeShortList Object By Condition employeeId
	 * ,roleId and companyId .
	 * 
	 * @param Long
	 *            the employeeId
	 * @param Long
	 *            the roleId
	 * @param Long
	 *            the companyId
	 */
	void deleteByCondition(Long employeeId, Long roleId, Long companyId);

	/**
	 * purpose : save CompanyEmployeeShortList Object.
	 * 
	 * @param CompanyEmployeeShortList
	 *            the companyEmployeeShortListId
	 */
	void save(CompanyEmployeeShortList companyEmployeeShortList);

	/**
	 * @param employeeId
	 * @param roleId
	 */
	void deleteByEmployeeAndRole(Long employeeId, Long roleId);

	List<Tuple> getCompanyEmpShortlistCount(Long employeeId, Long roleId);

}
