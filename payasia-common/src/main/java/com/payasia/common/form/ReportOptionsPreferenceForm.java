package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

/**
 * The Class ReportOptionsPreferenceForm.
 */
public class ReportOptionsPreferenceForm implements Serializable{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7487133273442955818L;

	/** The report id. */
	private Long reportId;
	
	/** The report desc. */
	private String reportDesc;
	
	/** The report name. */
	private String reportName;
	
	/** The report output format mapping id. */
	private Long reportOutputFormatMappingID;
	
	/** The default format output. */
	private Long defaultFormatOutput;
	
	/** The report output format form. */
	private List<ReportOutputFormatForm> reportOutputFormatForm;
	
	
	/**
	 * Gets the report id.
	 *
	 * @return the report id
	 */
	public Long getReportId() {
		return reportId;
	}
	
	/**
	 * Sets the report id.
	 *
	 * @param reportId the new report id
	 */
	public void setReportId(Long reportId) {
		this.reportId = reportId;
	}
	
	/**
	 * Gets the report desc.
	 *
	 * @return the report desc
	 */
	public String getReportDesc() {
		return reportDesc;
	}
	
	/**
	 * Sets the report desc.
	 *
	 * @param reportDesc the new report desc
	 */
	public void setReportDesc(String reportDesc) {
		this.reportDesc = reportDesc;
	}
	
	/**
	 * Gets the report name.
	 *
	 * @return the report name
	 */
	public String getReportName() {
		return reportName;
	}
	
	/**
	 * Sets the report name.
	 *
	 * @param reportName the new report name
	 */
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}
	
	/**
	 * Gets the report output format mapping id.
	 *
	 * @return the report output format mapping id
	 */
	public Long getReportOutputFormatMappingID() {
		return reportOutputFormatMappingID;
	}
	
	/**
	 * Sets the report output format mapping id.
	 *
	 * @param reportOutputFormatMappingID the new report output format mapping id
	 */
	public void setReportOutputFormatMappingID(Long reportOutputFormatMappingID) {
		this.reportOutputFormatMappingID = reportOutputFormatMappingID;
	}
	
	/**
	 * Gets the default format output.
	 *
	 * @return the default format output
	 */
	public Long getDefaultFormatOutput() {
		return defaultFormatOutput;
	}
	
	/**
	 * Sets the default format output.
	 *
	 * @param defaultFormatOutput the new default format output
	 */
	public void setDefaultFormatOutput(Long defaultFormatOutput) {
		this.defaultFormatOutput = defaultFormatOutput;
	}
	
	/**
	 * Gets the serialversionuid.
	 *
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	/**
	 * Gets the report output format form.
	 *
	 * @return the report output format form
	 */
	public List<ReportOutputFormatForm> getReportOutputFormatForm() {
		return reportOutputFormatForm;
	}
	
	/**
	 * Sets the report output format form.
	 *
	 * @param reportOutputFormatForm the new report output format form
	 */
	public void setReportOutputFormatForm(List<ReportOutputFormatForm> reportOutputFormatForm) {
		this.reportOutputFormatForm = reportOutputFormatForm;
	}
	
	

}
