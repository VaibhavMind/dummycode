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
 * The persistent class for the Report_Output_Format_Mapping database table.
 * 
 */
@Entity
@Table(name = "Report_Output_Format_Mapping")
public class ReportOutputFormatMapping extends CompanyBaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Report_Output_Format_Mapping_ID")
	private long reportOutputFormatMappingID;

	 
	@OneToMany(mappedBy = "reportOutputFormatMapping")
	private Set<ReportMaster> reportMasters;

	 
	@ManyToOne
	@JoinColumn(name = "Report_ID")
	private ReportMaster reportMaster;

	 
	@ManyToOne
	@JoinColumn(name = "Report_Output_Format_ID")
	private ReportOutputFormatMaster reportOutputFormatMaster;

	public ReportOutputFormatMapping() {
	}

	public long getReportOutputFormatMappingID() {
		return this.reportOutputFormatMappingID;
	}

	public void setReportOutputFormatMappingID(long reportOutputFormatMappingID) {
		this.reportOutputFormatMappingID = reportOutputFormatMappingID;
	}

	public Set<ReportMaster> getReportMasters() {
		return this.reportMasters;
	}

	public void setReportMasters(Set<ReportMaster> reportMasters) {
		this.reportMasters = reportMasters;
	}

	public ReportMaster getReportMaster() {
		return this.reportMaster;
	}

	public void setReportMaster(ReportMaster reportMaster) {
		this.reportMaster = reportMaster;
	}

	public ReportOutputFormatMaster getReportOutputFormatMaster() {
		return this.reportOutputFormatMaster;
	}

	public void setReportOutputFormatMaster(
			ReportOutputFormatMaster reportOutputFormatMaster) {
		this.reportOutputFormatMaster = reportOutputFormatMaster;
	}

}