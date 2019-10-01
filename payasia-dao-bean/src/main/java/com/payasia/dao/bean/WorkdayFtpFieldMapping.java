package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Workday_FTP_Field_Mapping")
public class WorkdayFtpFieldMapping extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Workday_FTP_Field_Mapping_ID")
	private Long workdayFtpFieldMappingId;

	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	@ManyToOne
	@JoinColumn(name = "Workday_Field_ID ")
	private WorkdayFieldMaster workdayField;

	@ManyToOne
	@JoinColumn(name = "HRO_Field_ID")
	private DataDictionary hroField;

	public WorkdayFtpFieldMapping() {
	}

	public Long getWorkdayFtpFieldMappingId() {
		return workdayFtpFieldMappingId;
	}

	public void setWorkdayFtpFieldMappingId(Long workdayFtpFieldMappingId) {
		this.workdayFtpFieldMappingId = workdayFtpFieldMappingId;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public WorkdayFieldMaster getWorkdayField() {
		return workdayField;
	}

	public void setWorkdayField(WorkdayFieldMaster workdayField) {
		this.workdayField = workdayField;
	}

	public DataDictionary getHroField() {
		return hroField;
	}

	public void setHroField(DataDictionary hroField) {
		this.hroField = hroField;
	}

}
