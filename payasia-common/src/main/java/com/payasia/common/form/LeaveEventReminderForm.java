package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

import com.payasia.common.dto.AppCodeDTO;
import com.payasia.common.dto.LeaveEventReminderDTO;
import com.payasia.common.dto.LeaveSchemeDTO;
import com.payasia.common.dto.LeaveTypeDTO;
import com.payasia.common.dto.MailTemplateDTO;
import com.payasia.common.dto.ReminderEventDTO;

public class LeaveEventReminderForm  extends PageResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1040166877448983055L;
	private Long reminderEventConfigId;
	private Long leaveEventId;
	private Long leaveSchemeId;
	private Long leaveSchemeTypeId;
	private Long recepientId;
	private Long recipeintTypeID;
	private Boolean sendMailBefore;
	private Boolean sendMailOn;
	private Boolean sendMailAfter;
	private List<ReminderEventDTO> reminderEventDTOs ;
	private List<LeaveSchemeDTO> leaveSchemeDTOs;
	private List<LeaveTypeDTO> leaveTypeDTOs;
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
	private List<LeaveEventReminderDTO> leaveEventReminderDTOs;
	private String employeeName;
	private String employeeNumber;
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
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
	public List<LeaveEventReminderDTO> getLeaveEventReminderDTOs() {
		return leaveEventReminderDTOs;
	}
	public void setLeaveEventReminderDTOs(
			List<LeaveEventReminderDTO> leaveEventReminderDTOs) {
		this.leaveEventReminderDTOs = leaveEventReminderDTOs;
	}
	public Boolean getStatus() {
		return status;
	}
	public void setStatus(Boolean status) {
		this.status = status;
	}
	public Integer getSendMailAfterTillDays() {
		return sendMailAfterTillDays;
	}
	public void setSendMailAfterTillDays(Integer sendMailAfterTillDays) {
		this.sendMailAfterTillDays = sendMailAfterTillDays;
	}
	public List<MailTemplateDTO> getMailTemplateDTOs() {
		return mailTemplateDTOs;
	}
	public void setMailTemplateDTOs(List<MailTemplateDTO> mailTemplateDTOs) {
		this.mailTemplateDTOs = mailTemplateDTOs;
	}
	public Boolean getAllowSendMailBeforeEventDay() {
		return allowSendMailBeforeEventDay;
	}
	public void setAllowSendMailBeforeEventDay(Boolean allowSendMailBeforeEventDay) {
		this.allowSendMailBeforeEventDay = allowSendMailBeforeEventDay;
	}
	public Boolean getAllowSendMailOnEventDay() {
		return allowSendMailOnEventDay;
	}
	public void setAllowSendMailOnEventDay(Boolean allowSendMailOnEventDay) {
		this.allowSendMailOnEventDay = allowSendMailOnEventDay;
	}
	public Integer getSendMailAfterDays() {
		return sendMailAfterDays;
	}
	public void setSendMailAfterDays(Integer sendMailAfterDays) {
		this.sendMailAfterDays = sendMailAfterDays;
	}
	public Boolean getAllowSendMailAfterEventDay() {
		return allowSendMailAfterEventDay;
	}
	public void setAllowSendMailAfterEventDay(Boolean allowSendMailAfterEventDay) {
		this.allowSendMailAfterEventDay = allowSendMailAfterEventDay;
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
	public Long getSendMailOnEventMailTemplate() {
		return sendMailOnEventMailTemplate;
	}
	public void setSendMailOnEventMailTemplate(Long sendMailOnEventMailTemplate) {
		this.sendMailOnEventMailTemplate = sendMailOnEventMailTemplate;
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
	public Long getSendMailBeforeMailTemplate() {
		return sendMailBeforeMailTemplate;
	}
	public void setSendMailBeforeMailTemplate(Long sendMailBeforeMailTemplate) {
		this.sendMailBeforeMailTemplate = sendMailBeforeMailTemplate;
	}
	public String getRecepValue() {
		return recepValue;
	}
	public void setRecepValue(String recepValue) {
		this.recepValue = recepValue;
	}
	public List<AppCodeDTO> getRecAppCodeDTOs() {
		return recAppCodeDTOs;
	}
	public void setRecAppCodeDTOs(List<AppCodeDTO> recAppCodeDTOs) {
		this.recAppCodeDTOs = recAppCodeDTOs;
	}
	public List<LeaveTypeDTO> getLeaveTypeDTOs() {
		return leaveTypeDTOs;
	}
	public void setLeaveTypeDTOs(List<LeaveTypeDTO> leaveTypeDTOs) {
		this.leaveTypeDTOs = leaveTypeDTOs;
	}
	public List<LeaveSchemeDTO> getLeaveSchemeDTOs() {
		return leaveSchemeDTOs;
	}
	public void setLeaveSchemeDTOs(List<LeaveSchemeDTO> leaveSchemeDTOs) {
		this.leaveSchemeDTOs = leaveSchemeDTOs;
	}
	public List<ReminderEventDTO> getReminderEventDTOs() {
		return reminderEventDTOs;
	}
	public void setReminderEventDTOs(List<ReminderEventDTO> reminderEventDTOs) {
		this.reminderEventDTOs = reminderEventDTOs;
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
	public Long getLeaveEventId() {
		return leaveEventId;
	}
	public void setLeaveEventId(Long leaveEventId) {
		this.leaveEventId = leaveEventId;
	}
	public Long getLeaveSchemeId() {
		return leaveSchemeId;
	}
	public void setLeaveSchemeId(Long leaveSchemeId) {
		this.leaveSchemeId = leaveSchemeId;
	}
	public Long getLeaveSchemeTypeId() {
		return leaveSchemeTypeId;
	}
	public void setLeaveSchemeTypeId(Long leaveSchemeTypeId) {
		this.leaveSchemeTypeId = leaveSchemeTypeId;
	}
	public Long getRecipeintTypeID() {
		return recipeintTypeID;
	}
	public void setRecipeintTypeID(Long recipeintTypeID) {
		this.recipeintTypeID = recipeintTypeID;
	}
	
	
	
	
	
	
	

}
