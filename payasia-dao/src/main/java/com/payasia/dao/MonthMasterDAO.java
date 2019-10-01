package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.MonthMaster;

/**
 * The Interface MonthMasterDAO.
 */
/**
 * @author vivekjain
 * 
 */
public interface MonthMasterDAO {

	/**
	 * Find MonthMaster Object by monthId.
	 * 
	 * @param monthId
	 *            the month id
	 * @return the month master
	 */
	MonthMaster findById(Long monthId);

	/**
	 * Find MonthMaster Objects List by monthName.
	 * 
	 * @param monthName
	 *            the month name
	 * @return the list
	 */
	List<MonthMaster> findByName(String monthName);

	/**
	 * Find all MonthMaster Objects List.
	 * 
	 * @return the list
	 */
	List<MonthMaster> findAll();

	/**
	 * Find MonthMaster Object by month name.
	 * 
	 * @param monthName
	 *            the month name
	 * @return the month master
	 */
	MonthMaster findByMonthName(String monthName);
}
