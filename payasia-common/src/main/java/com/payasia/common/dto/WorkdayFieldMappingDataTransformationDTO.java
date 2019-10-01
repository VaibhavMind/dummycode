package com.payasia.common.dto;

import java.io.Serializable;

public class WorkdayFieldMappingDataTransformationDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long dataTransformationId;
	
	private Long workdayFtpFieldMappingId;

	private String workdayFieldValue;

	private String hroFieldValue;


	public Long getDataTransformationId() {
		return dataTransformationId;
	}

	public void setDataTransformationId(Long dataTransformationId) {
		this.dataTransformationId = dataTransformationId;
	}

	public Long getWorkdayFtpFieldMappingId() {
		return workdayFtpFieldMappingId;
	}

	public void setWorkdayFtpFieldMappingId(Long workdayFtpFieldMappingId) {
		this.workdayFtpFieldMappingId = workdayFtpFieldMappingId;
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



}
