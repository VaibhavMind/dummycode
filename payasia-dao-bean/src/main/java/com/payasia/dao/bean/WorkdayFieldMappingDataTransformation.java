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
@Table(name = "Workday_Field_Mapping_Data_Transformation")
public class WorkdayFieldMappingDataTransformation extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Workday_Field_Mapping_Data_Transformation_ID")
	private Long fieldMappingDataTransformationId;
	
	@ManyToOne
	@JoinColumn(name = "Workday_FTP_Field_Mapping_ID ")
	private WorkdayFtpFieldMapping workdayFtpFieldMapping;

	@Column(name = "Workday_Field_Value")
	private String workdayFieldValue;

	@Column(name = "HRO_Field_Value")
	private String hroFieldValue;
	
	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	public Long getFieldMappingDataTransformationId() {
		return fieldMappingDataTransformationId;
	}

	public void setFieldMappingDataTransformationId(Long fieldMappingDataTransformationId) {
		this.fieldMappingDataTransformationId = fieldMappingDataTransformationId;
	}

	public WorkdayFtpFieldMapping getWorkdayFtpFieldMapping() {
		return workdayFtpFieldMapping;
	}

	public void setWorkdayFtpFieldMapping(WorkdayFtpFieldMapping workdayFtpFieldMapping) {
		this.workdayFtpFieldMapping = workdayFtpFieldMapping;
	}

	public String getWorkdayFieldValue() {
		return workdayFieldValue;
	}

	public void setWorkdayFieldValue(String workdayFieldValue) {
		this.workdayFieldValue = workdayFieldValue;
	}

	public String getHroFieldValue() {
		return hroFieldValue;
	}

	public void setHroFieldValue(String hroFieldValue) {
		this.hroFieldValue = hroFieldValue;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

}
