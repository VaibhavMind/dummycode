package com.payasia.common.form;

public class CoherentTimesheetPreferenceForm {
	private long id;
	private String cutOffDay;
	
	private Boolean useSystemMailAsFromAddress;
	private long dataDictionaryId;
	private Long costCategoryDictId;
	private Long dailyRateDictId;
	private Long autoTimewriteDictId;
	
	private String workingHourPerday;
	private long costCenter;
	private long location;
	private Boolean isValidate72Hours;
	
	
	public Boolean getIsValidate72Hours() {
		return isValidate72Hours;
	}

	public void setIsValidate72Hours(Boolean isValidate72Hours) {
		this.isValidate72Hours = isValidate72Hours;
	}

	public String getWorkingHourPerday() {
		return workingHourPerday;
	}

	public void setWorkingHourPerday(String workingHourPerday) {
		this.workingHourPerday = workingHourPerday;
	}

	public long getCostCenter() {
		return costCenter;
	}

	public void setCostCenter(long costCenter) {
		this.costCenter = costCenter;
	}

	public long getLocation() {
		return location;
	}

	public void setLocation(long location) {
		this.location = location;
	}

	
	
	public Boolean getUseSystemMailAsFromAddress() {
		return useSystemMailAsFromAddress;
	}

	public void setUseSystemMailAsFromAddress(Boolean useSystemMailAsFromAddress) {
		this.useSystemMailAsFromAddress = useSystemMailAsFromAddress;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCutOffDay() {
		return cutOffDay;
	}

	public void setCutOffDay(String cutOffDay) {
		this.cutOffDay = cutOffDay;
	}

	public long getDataDictionaryId() {
		return dataDictionaryId;
	}

	public void setDataDictionaryId(long dataDictionaryId) {
		this.dataDictionaryId = dataDictionaryId;
	}

	public Long getCostCategoryDictId() {
		return costCategoryDictId;
	}

	public void setCostCategoryDictId(Long costCategoryDictId) {
		this.costCategoryDictId = costCategoryDictId;
	}

	public Long getDailyRateDictId() {
		return dailyRateDictId;
	}

	public void setDailyRateDictId(Long dailyRateDictId) {
		this.dailyRateDictId = dailyRateDictId;
	}

	public Long getAutoTimewriteDictId() {
		return autoTimewriteDictId;
	}

	public void setAutoTimewriteDictId(Long autoTimewriteDictId) {
		this.autoTimewriteDictId = autoTimewriteDictId;
	}

}
