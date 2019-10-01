package com.payasia.logic;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.payasia.common.form.ReportOptionsPreferenceForm;

/**
 * @author vivekjain
 * 
 */
/**
 * The Interface ReportOptionsPreferenceLogic.
 */
@Transactional
public interface ReportOptionsPreferenceLogic {
	/**
	 * purpose : get ReportFormat Options.
	 * 
	 * @param Long
	 *            the companyId
	 * @return ReportOptionsPreferenceForm contains All ReportFormat
	 */
	List<ReportOptionsPreferenceForm> getReportFormatOptions(Long companyId);

	/**
	 * purpose : Save ReportFormat .
	 * 
	 * @param String
	 *            [] the reportIdArr
	 * @param String
	 *            [] the reportFmtMappingIdArr
	 * @param Long
	 *            the companyId
	 */
	void saveReportFormat(String[] reportIdArr, String[] reportFmtMappingIdArr,
			Long companyId);

}
