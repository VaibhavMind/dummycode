package com.payasia.common.dto;

public class LundinTimesheetEventReminderDTO {
	
	private Long reminderEventConfigId;
	
	private Long reminderEventId;
	private String reminderEventName;
	
	private Long recepientId;
	private String recepientName;
	public Long getReminderEventConfigId() {
		return reminderEventConfigId;
	}
	public void setReminderEventConfigId(Long reminderEventConfigId) {
		this.reminderEventConfigId = reminderEventConfigId;
	}
	public Long getReminderEventId() {
		return reminderEventId;
	}
	public void setReminderEventId(Long reminderEventId) {
		this.reminderEventId = reminderEventId;
	}
	public String getReminderEventName() {
		return reminderEventName;
	}
	public void setReminderEventName(String reminderEventName) {
		this.reminderEventName = reminderEventName;
	}
	public Long getRecepientId() {
		return recepientId;
	}
	public void setRecepientId(Long recepientId) {
		this.recepientId = recepientId;
	}
	public String getRecepientName() {
		return recepientName;
	}
	public void setRecepientName(String recepientName) {
		this.recepientName = recepientName;
	}
	
}
