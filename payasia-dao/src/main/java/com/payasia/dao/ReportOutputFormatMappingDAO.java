package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.ReportOutputFormatMapping;

/**
 * The Interface ReportOutputFormatMappingDAO.
 */
/**
 * @author vivekjain
 * 
 */
public interface ReportOutputFormatMappingDAO {

	/**
	 * Find ReportOutputFormatMapping Object List by report id.
	 * 
	 * @param reportId
	 *            the report id
	 * @return the list
	 */
	List<ReportOutputFormatMapping> findByReportId(Long reportId);

	/**
	 * Find ReportOutputFormatMapping Object by reportFormatMappingId.
	 * 
	 * @param reportFormatMappingId
	 *            the report format mapping id
	 * @return the report output format mapping
	 */
	ReportOutputFormatMapping findById(Long reportFormatMappingId);
}
