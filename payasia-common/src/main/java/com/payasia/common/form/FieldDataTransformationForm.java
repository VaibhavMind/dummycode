package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

import com.payasia.common.dto.WorkdayFieldMappingDataTransformationDTO;

public class FieldDataTransformationForm implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private List<WorkdayFieldMappingDataTransformationDTO> dataTransformationList;
	private Long fMappingId;
	
	public List<WorkdayFieldMappingDataTransformationDTO> getDataTransformationList() {
		return dataTransformationList;
	}
	public void setDataTransformationList(List<WorkdayFieldMappingDataTransformationDTO> dataTransformationList) {
		this.dataTransformationList = dataTransformationList;
	}
	public Long getfMappingId() {
		return fMappingId;
	}
	public void setfMappingId(Long fMappingId) {
		this.fMappingId = fMappingId;
	}
}
