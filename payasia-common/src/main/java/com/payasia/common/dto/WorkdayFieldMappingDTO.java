package com.payasia.common.dto;

import java.io.Serializable;
import java.util.Map;

import com.payasia.dao.bean.DataDictionary;

public class WorkdayFieldMappingDTO  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long fieldMappingId;
	private Long workdayFieldId;
	private Long hroFieldId;
	private String workdayFieldLabel;
	private String hroFieldLabel;
	private String section;
	private int order;
	private DataDictionary hroDataDictionary;
	private Map<String, String> dataTransformationMap;
	
	public Long getFieldMappingId() {
		return fieldMappingId;
	}
	public void setFieldMappingId(Long fieldMappingId) {
		this.fieldMappingId = fieldMappingId;
	}
	public Long getWorkdayFieldId() {
		return workdayFieldId;
	}
	public void setWorkdayFieldId(Long workdayFieldId) {
		this.workdayFieldId = workdayFieldId;
	}
	public Long getHroFieldId() {
		return hroFieldId;
	}
	public void setHroFieldId(Long hroFieldId) {
		this.hroFieldId = hroFieldId;
	}
	public String getWorkdayFieldLabel() {
		return workdayFieldLabel;
	}
	public void setWorkdayFieldLabel(String workdayFieldLabel) {
		this.workdayFieldLabel = workdayFieldLabel;
	}
	public String getHroFieldLabel() {
		return hroFieldLabel;
	}
	public void setHroFieldLabel(String hroFieldLabel) {
		this.hroFieldLabel = hroFieldLabel;
	}
	public String getSection() {
		return section;
	}
	public void setSection(String section) {
		this.section = section;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public DataDictionary getHroDataDictionary() {
		return hroDataDictionary;
	}
	public void setHroDataDictionary(DataDictionary hroDataDictionary) {
		this.hroDataDictionary = hroDataDictionary;
	}
	public Map<String, String> getDataTransformationMap() {
		return dataTransformationMap;
	}
	public void setDataTransformationMap(Map<String, String> dataTransformationMap) {
		this.dataTransformationMap = dataTransformationMap;
	}
}
