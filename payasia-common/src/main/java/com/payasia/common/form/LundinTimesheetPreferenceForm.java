package com.payasia.common.form;

public class LundinTimesheetPreferenceForm {
	
	private long id;
	private String cutOffDay;
	private Boolean useSystemMailAsFromAddress;
	private long dataDictionaryId;
	private Long costCategoryDictId;
	private Long dailyRateDictId;
	private Long autoTimewriteDictId;
	
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
