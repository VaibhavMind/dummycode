package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.TimeZoneMaster;

/**
 * The Interface TimeZoneMasterDAO.
 */
public interface TimeZoneMasterDAO {

	/**
	 * Find TimeZoneMaster Object by timeZoneName.
	 * 
	 * @param timeZoneName
	 *            the time zone name
	 * @return the time zone master
	 */
	TimeZoneMaster findByName(String timeZoneName);

	/**
	 * Find TimeZoneMaster Object by timeZoneID.
	 * 
	 * @param timeZoneID
	 *            the time zone id
	 * @return the time zone master
	 */
	TimeZoneMaster findById(long timeZoneID);

	/**
	 * Find all TimeZoneMaster Objects List.
	 * 
	 * @return the list
	 */
	List<TimeZoneMaster> findAll();

}
