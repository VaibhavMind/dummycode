package com.payasia.common.form;

import java.io.Serializable;

/**
 * The Class ReportOutputFormatForm.
 */
public class ReportOutputFormatForm implements Serializable{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7487133273442955818L;

	/** The report output format id. */
	private Long reportOutputFormatID;
	
	/** The report output format mapping id. */
	private Long reportOutputFormatMappingID;
	
	/** The report output format type. */
	private String reportOutputFormatType;
	
	/** The report output format desc. */
	private String reportOutputFormatDesc;
	
	/**
	 * Gets the report output format id.
	 *
	 * @return the report output format id
	 */
	public Long getReportOutputFormatID() {
		return reportOutputFormatID;
	}
	
	/**
	 * Sets the report output format id.
	 *
	 * @param reportOutputFormatID the new report output format id
	 */
	public void setReportOutputFormatID(Long reportOutputFormatID) {
		this.reportOutputFormatID = reportOutputFormatID;
	}
	
	/**
	 * Gets the report output format type.
	 *
	 * @return the report output format type
	 */
	public String getReportOutputFormatType() {
		return reportOutputFormatType;
	}
	
	/**
	 * Sets the report output format type.
	 *
	 * @param reportOutputFormatType the new report output format type
	 */
	public void setReportOutputFormatType(String reportOutputFormatType) {
		this.reportOutputFormatType = reportOutputFormatType;
	}
	
	/**
	 * Gets the report output format desc.
	 *
	 * @return the report output format desc
	 */
	public String getReportOutputFormatDesc() {
		return reportOutputFormatDesc;
	}
	
	/**
	 * Sets the report output format desc.
	 *
	 * @param reportOutputFormatDesc the new report output format desc
	 */
	public void setReportOutputFormatDesc(String reportOutputFormatDesc) {
		this.reportOutputFormatDesc = reportOutputFormatDesc;
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
	public void setReportOutputFormatMappingID(
			Long reportOutputFormatMappingID) {
		this.reportOutputFormatMappingID = reportOutputFormatMappingID;
	}

}
