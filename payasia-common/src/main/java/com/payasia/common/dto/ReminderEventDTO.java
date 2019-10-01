package com.payasia.common.dto;

public class ReminderEventDTO {
	
	private Long reminderEventId;
	private Long moduleId;
	private String eventName;
	private String eventDesc;
	
	public Long getReminderEventId() {
		return reminderEventId;
	}
	public void setReminderEventId(Long reminderEventId) {
		this.reminderEventId = reminderEventId;
	}
	public Long getModuleId() {
		return moduleId;
	}
	public void setModuleId(Long moduleId) {
		this.moduleId = moduleId;
	}
	public String getEventName() {
		return eventName;
	}
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}
	public String getEventDesc() {
		return eventDesc;
	}
	public void setEventDesc(String eventDesc) {
		this.eventDesc = eventDesc;
	}
	
	

}
