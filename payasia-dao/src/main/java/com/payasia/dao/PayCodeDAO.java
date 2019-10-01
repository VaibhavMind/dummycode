package com.payasia.dao;

import java.util.List;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.Paycode;

/**
 * The Interface PayCodeDAO.
 */
/**
 * @author vivekjain
 * 
 */
public interface PayCodeDAO {

	/**
	 * Update Paycode Object.
	 * 
	 * @param paycode
	 *            the paycode
	 */
	void update(Paycode paycode);

	/**
	 * Save Paycode Object.
	 * 
	 * @param paycode
	 *            the paycode
	 */
	void save(Paycode paycode);

	/**
	 * Delete Paycode Object.
	 * 
	 * @param paycode
	 *            the paycode
	 */
	void delete(Paycode paycode);

	/**
	 * Find Paycode Object by payCodeId.
	 * 
	 * @param payCodeId
	 *            the pay code id
	 * @return the paycode
	 */
	Paycode findByID(long payCodeId);

	/**
	 * Gets Paycode Objects List by condition companyId.
	 * 
	 * @param companyId
	 *            the company id
	 * @param pageDTO
	 *            the page dto
	 * @param sortDTO
	 *            the sort dto
	 * @return the all pay code by condition company
	 */
	List<Paycode> getAllPayCodeByConditionCompany(Long companyId,
			PageRequest pageDTO, SortCondition sortDTO);

	/**
	 * Gets the Paycode Object List by search String and companyId.
	 * 
	 * @param searchString
	 *            the search string
	 * @param companyId
	 *            the company id
	 * @return the pay code
	 */
	List<Paycode> getPayCode(String searchString, Long companyId);

	/**
	 * Gets the count for all pay code by companyId.
	 * 
	 * @param companyId
	 *            the company id
	 * @return the count for all pay code
	 */
	int getCountForAllPayCode(Long companyId);

	/**
	 * Gets the Paycode Object by search String and companyId.
	 * 
	 * @param searchString
	 *            the search string
	 * @param companyId
	 *            the company id
	 * @return the pay code by name
	 */
	Paycode getPayCodeByName(String searchString, Long companyId);

	/**
	 * Gets the sort path for pay code.
	 * 
	 * @param sortDTO
	 *            the sort dto
	 * @param payCodeRoot
	 *            the pay code root
	 * @return the sort path for pay code
	 */
	Path<String> getSortPathForPayCode(SortCondition sortDTO,
			Root<Paycode> payCodeRoot);

}
