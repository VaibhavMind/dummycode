package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.payasia.common.dto.DataImportLogDTO;

public class HolidayListMasterForm implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2574169075365187306L;
	private Long holidayId;
	private Long countryId;
	private String countryName;
	private Long stateId;
	private String stateName;
	private String dated;
	private String day;
	private String occasion;
	private Integer year;
	private CommonsMultipartFile fileUpload;
	private List<Integer> yearList;
	private String status;
	private boolean restricted = false;
	private long companyId;
	
	private boolean dataValid;
	private List<DataImportLogDTO> dataImportLogDTOs;
	
	
	public boolean isDataValid() {
		return dataValid;
	}
	public void setDataValid(boolean dataValid) {
		this.dataValid = dataValid;
	}
	public List<DataImportLogDTO> getDataImportLogDTOs() {
		return dataImportLogDTOs;
	}
	public void setDataImportLogDTOs(List<DataImportLogDTO> dataImportLogDTOs) {
		this.dataImportLogDTOs = dataImportLogDTOs;
	}
	public String getCountryName() {
		return countryName;
	}
	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}
	public String getStateName() {
		return stateName;
	}
	public void setStateName(String stateName) {
		this.stateName = stateName;
	}
	public String getDated() {
		return dated;
	}
	public void setDated(String dated) {
		this.dated = dated;
	}
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public String getOccasion() {
		return occasion;
	}
	public void setOccasion(String occasion) {
		this.occasion = occasion;
	}
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	public boolean isRestricted() {
		return restricted;
	}
	public void setRestricted(boolean restricted) {
		this.restricted = restricted;
	}
	public long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}
	public Long getHolidayId() {
		return holidayId;
	}
	public void setHolidayId(Long holidayId) {
		this.holidayId = holidayId;
	}
	public Long getCountryId() {
		return countryId;
	}
	public void setCountryId(Long countryId) {
		this.countryId = countryId;
	}
	public Long getStateId() {
		return stateId;
	}
	public void setStateId(Long stateId) {
		this.stateId = stateId;
	}
	public CommonsMultipartFile getFileUpload() {
		return fileUpload;
	}
	public void setFileUpload(CommonsMultipartFile fileUpload) {
		this.fileUpload = fileUpload;
	}
	public List<Integer> getYearList() {
		return yearList;
	}
	public void setYearList(List<Integer> yearList) {
		this.yearList = yearList;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	
	
}
