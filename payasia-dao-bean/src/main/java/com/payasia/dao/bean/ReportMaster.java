package com.payasia.dao.bean;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * The persistent class for the Report_Master database table.
 * 
 */
@Entity
@Table(name = "Report_Master")
public class ReportMaster implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Report_ID")
	private long reportId;

	@Column(name = "Report_Desc")
	private String reportDesc;

	@Column(name = "Report_Name")
	private String reportName;

	 
	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	 
	@ManyToOne
	@JoinColumn(name = "Default_Output_Format")
	private ReportOutputFormatMapping reportOutputFormatMapping;

	 
	@OneToMany(mappedBy = "reportMaster")
	private Set<ReportOutputFormatMapping> reportOutputFormatMappings;

	public ReportMaster() {
	}

	public long getReportId() {
		return this.reportId;
	}

	public void setReportId(long reportId) {
		this.reportId = reportId;
	}

	public String getReportDesc() {
		return this.reportDesc;
	}

	public void setReportDesc(String reportDesc) {
		this.reportDesc = reportDesc;
	}

	public String getReportName() {
		return this.reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public Company getCompany() {
		return this.company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public ReportOutputFormatMapping getReportOutputFormatMapping() {
		return this.reportOutputFormatMapping;
	}

	public void setReportOutputFormatMapping(
			ReportOutputFormatMapping reportOutputFormatMapping) {
		this.reportOutputFormatMapping = reportOutputFormatMapping;
	}

	public Set<ReportOutputFormatMapping> getReportOutputFormatMappings() {
		return this.reportOutputFormatMappings;
	}

	public void setReportOutputFormatMappings(
			Set<ReportOutputFormatMapping> reportOutputFormatMappings) {
		this.reportOutputFormatMappings = reportOutputFormatMappings;
	}

}