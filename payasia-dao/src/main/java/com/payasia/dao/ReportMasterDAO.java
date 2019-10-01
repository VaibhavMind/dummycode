package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.ReportMaster;

/**
 * The Interface ReportMasterDAO.
 */
/**
 * @author vivekjain
 * 
 */
public interface ReportMasterDAO {

	/**
	 * Find ReportMaster Objects List by condition companyId.
	 * 
	 * @param companyId
	 *            the company id
	 * @return the list
	 */
	List<ReportMaster> findByConditionCompany(Long companyId);

	/**
	 * Find ReportMaster Object by reportId.
	 * 
	 * @param reportId
	 *            the report id
	 * @return the report master
	 */
	ReportMaster findById(Long reportId);

	/**
	 * Update ReportMaster Object.
	 * 
	 * @param reportMaster
	 *            the report master
	 */
	void update(ReportMaster reportMaster);
}
