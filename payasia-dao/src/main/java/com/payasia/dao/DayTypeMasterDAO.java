package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.DayTypeMaster;

/**
 * The Interface DayTypeMasterDAO.
 */
/**
 * @author vivekjain
 * 
 */
public interface DayTypeMasterDAO {

	/**
	 * purpose : Find DayTypeMaster Objects List.
	 * 
	 * @return the list
	 */
	List<DayTypeMaster> findAll();

	/**
	 * purpose : Find DayTypeMaster Object by dayTypeId.
	 * 
	 * @param dayTypeId
	 *            the day type id
	 * @return the day type master
	 */
	DayTypeMaster findById(Long dayTypeId);

}
