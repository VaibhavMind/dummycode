package com.payasia.common.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CalendarTempDataForm implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5171177306028511519L;
	private long[] weekdayId= new long[7];
	private List<ShiftForm> shifts= new ArrayList<ShiftForm>();
	private long[] months= new long[12];
	private long[] entityId= new long[2];
	private String[] entityName= new String[2];
	private List<Long> dataDictionaryId;
	private List<String> fieldName;
	private List<String> dataType;
	
	public long[] getWeekdayId() {
		return weekdayId;
	}
	public void setWeekdayId(long[] weekdayId) {
		 if(weekdayId !=null){
			   this.weekdayId = Arrays.copyOf(weekdayId, weekdayId.length); 
		} 
	}
	public List<ShiftForm> getShifts() {
		return shifts;
	}
	public void setShifts(List<ShiftForm> shifts) {
		this.shifts = shifts;
	}
	public long[] getMonths() {
		return months;
	}
	public void setMonths(long[] months) {
		 if(months !=null){
			   this.months = Arrays.copyOf(months, months.length); 
		} 
	}
	public long[] getEntityId() {
		return entityId;
	}
	public void setEntityId(long[] entityId) {
		 if(entityId !=null){
			   this.entityId = Arrays.copyOf(entityId, entityId.length); 
		} 
	}
	public String[] getEntityName() {
		return entityName;
	}
	public void setEntityName(String[] entityName) {
		 if(entityName !=null){
			   this.entityName = Arrays.copyOf(entityName, entityName.length); 
		} 
	}
	public List<Long> getDataDictionaryId() {
		return dataDictionaryId;
	}
	public void setDataDictionaryId(List<Long> dataDictionaryId) {
		this.dataDictionaryId = dataDictionaryId;
	}
	public List<String> getFieldName() {
		return fieldName;
	}
	public void setFieldName(List<String> fieldName) {
		this.fieldName = fieldName;
	}
	public List<String> getDataType() {
		return dataType;
	}
	public void setDataType(List<String> dataType) {
		this.dataType = dataType;
	}
	
	
}
