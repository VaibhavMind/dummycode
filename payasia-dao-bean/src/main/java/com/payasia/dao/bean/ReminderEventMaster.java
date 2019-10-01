package com.payasia.dao.bean;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * The persistent class for the App_Code_Master database table.
 * 
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "Reminder_Event_Master")
public class ReminderEventMaster implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Reminder_Event_ID")
	private long reminderEventId;

	@Column(name = "Event")
	private String event;

	@Column(name = "Event_Desc")
	private String eventDesc;

	 
	@ManyToOne
	@JoinColumn(name = "Module_ID")
	private ModuleMaster moduleMaster;

	 
	@OneToMany(mappedBy = "reminderEventMaster")
	private Set<ReminderEventConfig> reminderEventConfigs;

	public ReminderEventMaster() {
	}

	public long getReminderEventId() {
		return reminderEventId;
	}

	public void setReminderEventId(long reminderEventId) {
		this.reminderEventId = reminderEventId;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getEventDesc() {
		return eventDesc;
	}

	public void setEventDesc(String eventDesc) {
		this.eventDesc = eventDesc;
	}

	public ModuleMaster getModuleMaster() {
		return moduleMaster;
	}

	public void setModuleMaster(ModuleMaster moduleMaster) {
		this.moduleMaster = moduleMaster;
	}

	public Set<ReminderEventConfig> getReminderEventConfigs() {
		return reminderEventConfigs;
	}

	public void setReminderEventConfigs(
			Set<ReminderEventConfig> reminderEventConfigs) {
		this.reminderEventConfigs = reminderEventConfigs;
	}

}