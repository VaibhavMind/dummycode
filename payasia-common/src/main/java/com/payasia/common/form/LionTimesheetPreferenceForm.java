package com.payasia.common.form;


public class LionTimesheetPreferenceForm {

	private String periodStart;
	private long cutoffCycleId;
	private long locationId;
	private Boolean useSystemMailAsFromAddress;
	private Boolean overridePeriodStart;
	
		
	public Boolean getOverridePeriodStart() {
		return overridePeriodStart;
	}

	public void setOverridePeriodStart(Boolean overridePeriodStart) {
		this.overridePeriodStart = overridePeriodStart;
	}

	public String getPeriodStart() {
		return periodStart;
	}

	public void setPeriodStart(String periodStart) {
		this.periodStart = periodStart;
	}


	public long getCutoffCycleId() {
		return cutoffCycleId;
	}

	public void setCutoffCycleId(long cutoffCycleId) {
		this.cutoffCycleId = cutoffCycleId;
	}

	public long getLocationId() {
		return locationId;
	}

	public void setLocationId(long locationId) {
		this.locationId = locationId;
	}

	public Boolean getUseSystemMailAsFromAddress() {
		return useSystemMailAsFromAddress;
	}

	public void setUseSystemMailAsFromAddress(Boolean useSystemMailAsFromAddress) {
		this.useSystemMailAsFromAddress = useSystemMailAsFromAddress;
	}

			
}
