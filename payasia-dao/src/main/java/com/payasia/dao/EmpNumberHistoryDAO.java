/**
 * @author abhisheksachdeva
 *
 */
package com.payasia.dao;

import java.util.List;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.EmpNumberHistory;

/**
 * The Interface EmpNumberHistoryDAO.
 */
public interface EmpNumberHistoryDAO {

	/**
	 * Gets the sort path for emp number history.
	 * 
	 * @param sortDTO
	 *            the sort dto
	 * @param empNumberHistoryRoot
	 *            the emp number history root
	 * @return the sort path for emp number history
	 */
	Path<String> getSortPathForEmpNumberHistory(SortCondition sortDTO,
			Root<EmpNumberHistory> empNumberHistoryRoot);

	/**
	 * Save EmpNumberHistory Object.
	 * 
	 * @param empNumberHistory
	 *            the emp number history
	 */
	void save(EmpNumberHistory empNumberHistory);

	/**
	 * Find all EmpNumberHistory Objects List.
	 * 
	 * @param pageDTO
	 *            the page dto
	 * @param sortDTO
	 *            the sort dto
	 * @return the list
	 */
	List<EmpNumberHistory> findAll(PageRequest pageDTO, SortCondition sortDTO,
			Long companyId);

	/**
	 * Gets the count for all.
	 * 
	 * @return the count for all
	 */
	int getCountForAll(Long companyId);

}
