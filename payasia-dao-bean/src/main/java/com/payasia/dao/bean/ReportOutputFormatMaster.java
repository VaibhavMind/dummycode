package com.payasia.dao.bean;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * The persistent class for the Report_Output_Format_Master database table.
 * 
 */
@Entity
@Table(name = "Report_Output_Format_Master")
public class ReportOutputFormatMaster implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Report_Output_Format_ID")
	private long reportOutputFormatID;

	@Column(name = "Report_Output_Format_Desc")
	private String reportOutputFormatDesc;

	@Column(name = "Report_Output_Format_Type")
	private String reportOutputFormatType;

	 
	@OneToMany(mappedBy = "reportOutputFormatMaster")
	private Set<ReportOutputFormatMapping> reportOutputFormatMappings;

	public ReportOutputFormatMaster() {
	}

	public long getReportOutputFormatID() {
		return this.reportOutputFormatID;
	}

	public void setReportOutputFormatID(long reportOutputFormatID) {
		this.reportOutputFormatID = reportOutputFormatID;
	}

	public String getReportOutputFormatDesc() {
		return this.reportOutputFormatDesc;
	}

	public void setReportOutputFormatDesc(String reportOutputFormatDesc) {
		this.reportOutputFormatDesc = reportOutputFormatDesc;
	}

	public String getReportOutputFormatType() {
		return this.reportOutputFormatType;
	}

	public void setReportOutputFormatType(String reportOutputFormatType) {
		this.reportOutputFormatType = reportOutputFormatType;
	}

	public Set<ReportOutputFormatMapping> getReportOutputFormatMappings() {
		return this.reportOutputFormatMappings;
	}

	public void setReportOutputFormatMappings(
			Set<ReportOutputFormatMapping> reportOutputFormatMappings) {
		this.reportOutputFormatMappings = reportOutputFormatMappings;
	}

}