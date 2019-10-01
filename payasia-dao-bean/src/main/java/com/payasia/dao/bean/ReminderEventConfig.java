package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * The persistent class for the App_Code_Master database table.
 * 
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "Reminder_Event_Config")
public class ReminderEventConfig extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Reminder_Event_Config_ID")
	private long reminderEventConfigId;

	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	@ManyToOne
	@JoinColumn(name = "Reminder_Event_ID")
	private ReminderEventMaster reminderEventMaster;

	@ManyToOne
	@JoinColumn(name = "Leave_Scheme_ID")
	private LeaveScheme leaveScheme;

	@ManyToOne
	@JoinColumn(name = "Leave_Type_ID")
	private LeaveTypeMaster leaveTypeMaster;

	@ManyToOne
	@JoinColumn(name = "Recipient_Type")
	private AppCodeMaster recipientType;

	@Column(name = "Recipient_Value")
	private String recipientValue;

	@Column(name = "Allow_Send_Mail_Before_Event_Day")
	private boolean allowSendMailBeforeEventDay;

	@Column(name = "Send_Mail_Before_Days")
	private Integer sendMailBeforeDays;

	@Column(name = "Send_Mail_Before_Repeat_Days")
	private Integer sendMailBeforeRepeatDays;

	 
	@ManyToOne
	@JoinColumn(name = "Send_Mail_Before_Mail_Template")
	private EmailTemplate sendMailBeforeMailTemplate;

	@Column(name = "Allow_Send_Mail_On_Event_Day")
	private boolean allowSendMailOnEventDay;

	 
	@ManyToOne
	@JoinColumn(name = "Send_Mail_On_Event_Mail_Template")
	private EmailTemplate sendMailOnEventMailTemplate;

	@Column(name = "Allow_Send_Mail_After_Event_Day")
	private boolean allowSendMailAfterEventDay;

	@Column(name = "Send_Mail_After_Days")
	private Integer sendMailAfterDays;

	@Column(name = "Send_Mail_After_Till_Days")
	private Integer sendMailAfterTillDays;

	@Column(name = "Send_Mail_After_Repeat_Days")
	private Integer sendMailAfterRepeatDays;

	 
	@ManyToOne
	@JoinColumn(name = "Send_Mail_After_Mail_Template")
	private EmailTemplate sendMailAfterMailTemplate;

	public ReminderEventConfig() {
	}

	public long getReminderEventConfigId() {
		return reminderEventConfigId;
	}

	public void setReminderEventConfigId(long reminderEventConfigId) {
		this.reminderEventConfigId = reminderEventConfigId;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public ReminderEventMaster getReminderEventMaster() {
		return reminderEventMaster;
	}

	public void setReminderEventMaster(ReminderEventMaster reminderEventMaster) {
		this.reminderEventMaster = reminderEventMaster;
	}

	public LeaveScheme getLeaveScheme() {
		return leaveScheme;
	}

	public void setLeaveScheme(LeaveScheme leaveScheme) {
		this.leaveScheme = leaveScheme;
	}

	public LeaveTypeMaster getLeaveTypeMaster() {
		return leaveTypeMaster;
	}

	public void setLeaveTypeMaster(LeaveTypeMaster leaveTypeMaster) {
		this.leaveTypeMaster = leaveTypeMaster;
	}

	public AppCodeMaster getRecipientType() {
		return recipientType;
	}

	public void setRecipientType(AppCodeMaster recipientType) {
		this.recipientType = recipientType;
	}

	public String getRecipientValue() {
		return recipientValue;
	}

	public void setRecipientValue(String recipientValue) {
		this.recipientValue = recipientValue;
	}

	public boolean isAllowSendMailBeforeEventDay() {
		return allowSendMailBeforeEventDay;
	}

	public void setAllowSendMailBeforeEventDay(
			boolean allowSendMailBeforeEventDay) {
		this.allowSendMailBeforeEventDay = allowSendMailBeforeEventDay;
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

	public EmailTemplate getSendMailBeforeMailTemplate() {
		return sendMailBeforeMailTemplate;
	}

	public void setSendMailBeforeMailTemplate(
			EmailTemplate sendMailBeforeMailTemplate) {
		this.sendMailBeforeMailTemplate = sendMailBeforeMailTemplate;
	}

	public boolean isAllowSendMailOnEventDay() {
		return allowSendMailOnEventDay;
	}

	public void setAllowSendMailOnEventDay(boolean allowSendMailOnEventDay) {
		this.allowSendMailOnEventDay = allowSendMailOnEventDay;
	}

	public EmailTemplate getSendMailOnEventMailTemplate() {
		return sendMailOnEventMailTemplate;
	}

	public void setSendMailOnEventMailTemplate(
			EmailTemplate sendMailOnEventMailTemplate) {
		this.sendMailOnEventMailTemplate = sendMailOnEventMailTemplate;
	}

	public boolean isAllowSendMailAfterEventDay() {
		return allowSendMailAfterEventDay;
	}

	public void setAllowSendMailAfterEventDay(boolean allowSendMailAfterEventDay) {
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

	public EmailTemplate getSendMailAfterMailTemplate() {
		return sendMailAfterMailTemplate;
	}

	public void setSendMailAfterMailTemplate(
			EmailTemplate sendMailAfterMailTemplate) {
		this.sendMailAfterMailTemplate = sendMailAfterMailTemplate;
	}

	public Integer getSendMailAfterTillDays() {
		return sendMailAfterTillDays;
	}

	public void setSendMailAfterTillDays(Integer sendMailAfterTillDays) {
		this.sendMailAfterTillDays = sendMailAfterTillDays;
	}

}