package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

import com.payasia.common.dto.AppCodeDTO;
import com.payasia.common.dto.LundinTimesheetEventReminderDTO;
import com.payasia.common.dto.MailTemplateDTO;
import com.payasia.common.dto.ReminderEventDTO;

public class LundinTimesheetEventReminderForm extends PageResponse implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4945413741414008530L;
	
	private Long reminderEventConfigId;
	private Long timesheetEventId;
	private Long recepientId;
	private Long recipeintTypeID;
	private Boolean sendMailBefore;
	private Boolean sendMailOn;
	private Boolean sendMailAfter;
	private List<ReminderEventDTO> reminderEventDTOs ;
	private List<AppCodeDTO> recAppCodeDTOs;
	private List<MailTemplateDTO> mailTemplateDTOs;
	private String recepValue;
	private Integer sendMailBeforeDays;
	private Integer sendMailBeforeRepeatDays;
	private Boolean allowSendMailBeforeEventDay;
	private Long sendMailBeforeMailTemplate;
	private Long sendMailOnEventMailTemplate;
	private Boolean allowSendMailOnEventDay;
	private Boolean allowSendMailAfterEventDay;
	private Integer sendMailAfterDays;
	private Integer sendMailAfterRepeatDays;
	private Long sendMailAfterMailTemplate;
	private Integer sendMailAfterTillDays;
	private Boolean status;
	private List<LundinTimesheetEventReminderDTO> lundinTimesheetEventReminderDTOs;
	private String employeeName;
	private String employeeNumber;
	
	public Long getReminderEventConfigId() {
		return reminderEventConfigId;
	}
	public void setReminderEventConfigId(Long reminderEventConfigId) {
		this.reminderEventConfigId = reminderEventConfigId;
	}
	public Long getRecepientId() {
		return recepientId;
	}
	public void setRecepientId(Long recepientId) {
		this.recepientId = recepientId;
	}
	public Long getRecipeintTypeID() {
		return recipeintTypeID;
	}
	public void setRecipeintTypeID(Long recipeintTypeID) {
		this.recipeintTypeID = recipeintTypeID;
	}
	public Boolean getSendMailBefore() {
		return sendMailBefore;
	}
	public void setSendMailBefore(Boolean sendMailBefore) {
		this.sendMailBefore = sendMailBefore;
	}
	public Boolean getSendMailOn() {
		return sendMailOn;
	}
	public void setSendMailOn(Boolean sendMailOn) {
		this.sendMailOn = sendMailOn;
	}
	public Boolean getSendMailAfter() {
		return sendMailAfter;
	}
	public void setSendMailAfter(Boolean sendMailAfter) {
		this.sendMailAfter = sendMailAfter;
	}
	public List<ReminderEventDTO> getReminderEventDTOs() {
		return reminderEventDTOs;
	}
	public void setReminderEventDTOs(List<ReminderEventDTO> reminderEventDTOs) {
		this.reminderEventDTOs = reminderEventDTOs;
	}
	public List<AppCodeDTO> getRecAppCodeDTOs() {
		return recAppCodeDTOs;
	}
	public void setRecAppCodeDTOs(List<AppCodeDTO> recAppCodeDTOs) {
		this.recAppCodeDTOs = recAppCodeDTOs;
	}
	public List<MailTemplateDTO> getMailTemplateDTOs() {
		return mailTemplateDTOs;
	}
	public void setMailTemplateDTOs(List<MailTemplateDTO> mailTemplateDTOs) {
		this.mailTemplateDTOs = mailTemplateDTOs;
	}
	public String getRecepValue() {
		return recepValue;
	}
	public void setRecepValue(String recepValue) {
		this.recepValue = recepValue;
	}
	public Integer getSendMailBeforeDays() {
		return sendMailBeforeDays;
	}
	public void setSendMailBeforeDays(Integer sendMailBeforeDays) {
		this.sendMailBeforeDays = sendMailBeforeDays;
	}
	public Integer getSendMailBeforeRepeatDays() {
		return sendMailBeforeRepeatDays;
	}
	public void setSendMailBeforeRepeatDays(Integer sendMailBeforeRepeatDays) {
		this.sendMailBeforeRepeatDays = sendMailBeforeRepeatDays;
	}
	public Boolean getAllowSendMailBeforeEventDay() {
		return allowSendMailBeforeEventDay;
	}
	public void setAllowSendMailBeforeEventDay(Boolean allowSendMailBeforeEventDay) {
		this.allowSendMailBeforeEventDay = allowSendMailBeforeEventDay;
	}
	public Long getSendMailBeforeMailTemplate() {
		return sendMailBeforeMailTemplate;
	}
	public void setSendMailBeforeMailTemplate(Long sendMailBeforeMailTemplate) {
		this.sendMailBeforeMailTemplate = sendMailBeforeMailTemplate;
	}
	public Long getSendMailOnEventMailTemplate() {
		return sendMailOnEventMailTemplate;
	}
	public void setSendMailOnEventMailTemplate(Long sendMailOnEventMailTemplate) {
		this.sendMailOnEventMailTemplate = sendMailOnEventMailTemplate;
	}
	public Boolean getAllowSendMailOnEventDay() {
		return allowSendMailOnEventDay;
	}
	public void setAllowSendMailOnEventDay(Boolean allowSendMailOnEventDay) {
		this.allowSendMailOnEventDay = allowSendMailOnEventDay;
	}
	public Boolean getAllowSendMailAfterEventDay() {
		return allowSendMailAfterEventDay;
	}
	public void setAllowSendMailAfterEventDay(Boolean allowSendMailAfterEventDay) {
		this.allowSendMailAfterEventDay = allowSendMailAfterEventDay;
	}
	public Integer getSendMailAfterDays() {
		return sendMailAfterDays;
	}
	public void setSendMailAfterDays(Integer sendMailAfterDays) {
		this.sendMailAfterDays = sendMailAfterDays;
	}
	public Integer getSendMailAfterRepeatDays() {
		return sendMailAfterRepeatDays;
	}
	public void setSendMailAfterRepeatDays(Integer sendMailAfterRepeatDays) {
		this.sendMailAfterRepeatDays = sendMailAfterRepeatDays;
	}
	public Long getSendMailAfterMailTemplate() {
		return sendMailAfterMailTemplate;
	}
	public void setSendMailAfterMailTemplate(Long sendMailAfterMailTemplate) {
		this.sendMailAfterMailTemplate = sendMailAfterMailTemplate;
	}
	public Integer getSendMailAfterTillDays() {
		return sendMailAfterTillDays;
	}
	public void setSendMailAfterTillDays(Integer sendMailAfterTillDays) {
		this.sendMailAfterTillDays = sendMailAfterTillDays;
	}
	public Boolean getStatus() {
		return status;
	}
	public void setStatus(Boolean status) {
		this.status = status;
	}
	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	public String getEmployeeNumber() {
		return employeeNumber;
	}
	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}
	public Long getTimesheetEventId() {
		return timesheetEventId;
	}
	public void setTimesheetEventId(Long timesheetEventId) {
		this.timesheetEventId = timesheetEventId;
	}
	public List<LundinTimesheetEventReminderDTO> getLundinTimesheetEventReminderDTOs() {
		return lundinTimesheetEventReminderDTOs;
	}
	public void setLundinTimesheetEventReminderDTOs(
			List<LundinTimesheetEventReminderDTO> lundinTimesheetEventReminderDTOs) {
		this.lundinTimesheetEventReminderDTOs = lundinTimesheetEventReminderDTOs;
	}


}
