package com.payasia.logic.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.payasia.common.form.ReportOptionsPreferenceForm;
import com.payasia.common.form.ReportOutputFormatForm;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.ReportMasterDAO;
import com.payasia.dao.ReportOutputFormatMappingDAO;
import com.payasia.dao.ReportOutputFormatMasterDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.ReportMaster;
import com.payasia.dao.bean.ReportOutputFormatMapping;
import com.payasia.dao.bean.ReportOutputFormatMaster;
import com.payasia.logic.ReportOptionsPreferenceLogic;

/**
 * The Class ReportOptionsPreferenceLogicImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Component
public class ReportOptionsPreferenceLogicImpl implements
		ReportOptionsPreferenceLogic {

	/** The company dao. */
	@Resource
	CompanyDAO companyDAO;

	/** The report master dao. */
	@Resource
	ReportMasterDAO reportMasterDAO;

	/** The report output format mapping dao. */
	@Resource
	ReportOutputFormatMappingDAO reportOutputFormatMappingDAO;

	/** The report output format master dao. */
	@Resource
	ReportOutputFormatMasterDAO reportOutputFormatMasterDAO;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.ReportOptionsPreferenceLogic#getReportFormatOptions
	 * (java.lang.Long)
	 */
	@Override
	public List<ReportOptionsPreferenceForm> getReportFormatOptions(
			Long companyId) {

		List<ReportOptionsPreferenceForm> reportOptionsPreferenceList = new ArrayList<ReportOptionsPreferenceForm>();

		List<ReportMaster> reportMastersList = reportMasterDAO
				.findByConditionCompany(companyId);
		for (ReportMaster reportMaster : reportMastersList) {
			ReportOptionsPreferenceForm reportOptionsPreferenceForm = new ReportOptionsPreferenceForm();

			reportOptionsPreferenceForm.setReportId(reportMaster.getReportId());
			reportOptionsPreferenceForm.setReportName(reportMaster
					.getReportName());
			ReportOutputFormatMapping formatMapping = reportMaster
					.getReportOutputFormatMapping();
			reportOptionsPreferenceForm.setDefaultFormatOutput(formatMapping
					.getReportOutputFormatMappingID());

			List<ReportOutputFormatMapping> reportOutputFormatMappingList = reportOutputFormatMappingDAO
					.findByReportId(reportMaster.getReportId());
			List<ReportOutputFormatForm> reportOutputFormatList = new ArrayList<ReportOutputFormatForm>();
			for (ReportOutputFormatMapping reportOutputFormatMapping : reportOutputFormatMappingList) {

				ReportOutputFormatForm reportOutputFormatForm = new ReportOutputFormatForm();

				ReportOutputFormatMaster reportOutputFormat = reportOutputFormatMapping
						.getReportOutputFormatMaster();
				reportOutputFormatForm
						.setReportOutputFormatType(reportOutputFormat
								.getReportOutputFormatType());
				reportOutputFormatForm
						.setReportOutputFormatMappingID(reportOutputFormatMapping
								.getReportOutputFormatMappingID());
				reportOutputFormatList.add(reportOutputFormatForm);

			}
			reportOptionsPreferenceForm
					.setReportOutputFormatForm(reportOutputFormatList);
			reportOptionsPreferenceList.add(reportOptionsPreferenceForm);
		}

		return reportOptionsPreferenceList;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.ReportOptionsPreferenceLogic#saveReportFormat(java.
	 * lang.String[], java.lang.String[], java.lang.Long)
	 */
	@Override
	public void saveReportFormat(String[] reportIdArr,
			String[] reportFmtMappingIdArr, Long companyId) {
		for (int count = 0; count < reportIdArr.length; count++) {
			ReportMaster reportMaster = reportMasterDAO.findById(Long
					.parseLong(reportIdArr[count]));
			for (int countFmt = 0; countFmt < reportFmtMappingIdArr.length; countFmt++) {
				ReportOutputFormatMapping reportOutputFormatMapping = reportOutputFormatMappingDAO
						.findById(Long.parseLong(reportFmtMappingIdArr[count]));
				Company company = companyDAO.findById(companyId);
				reportMaster
						.setReportOutputFormatMapping(reportOutputFormatMapping);
				reportMaster.setCompany(company);
				reportMasterDAO.update(reportMaster);
			}
		}
	}
}
