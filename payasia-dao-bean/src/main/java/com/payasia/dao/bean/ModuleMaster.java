package com.payasia.dao.bean;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * The persistent class for the Country_Master database table.
 * 
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "Module_Master")
public class ModuleMaster implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "Module_ID")
	private long moduleId;

	@Column(name = "Module_Name")
	private String moduleName;

	@OneToMany(mappedBy = "moduleMaster")
	private Set<CompanyModuleMapping> companyModuleMappings;

	@OneToMany(mappedBy = "moduleMaster")
	private Set<PrivilegeMaster> privilegeMasters;

	@OneToMany(mappedBy = "moduleMaster")
	private Set<CalendarCodeMaster> calendarCodeMasters;

	@OneToMany(mappedBy = "moduleMaster")
	private Set<ReminderEventMaster> reminderEventMasters;

	@OneToMany(mappedBy = "moduleMaster")
	private Set<SchedulerMaster> schedulerMasters;

	@OneToMany(mappedBy = "moduleMaster")
	private Set<NotificationAlert> notificationAlerts;

	public long getModuleId() {
		return moduleId;
	}

	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public Set<CompanyModuleMapping> getCompanyModuleMappings() {
		return companyModuleMappings;
	}

	public void setCompanyModuleMappings(
			Set<CompanyModuleMapping> companyModuleMappings) {
		this.companyModuleMappings = companyModuleMappings;
	}

	public Set<PrivilegeMaster> getPrivilegeMasters() {
		return privilegeMasters;
	}

	public void setPrivilegeMasters(Set<PrivilegeMaster> privilegeMasters) {
		this.privilegeMasters = privilegeMasters;
	}

	public Set<CalendarCodeMaster> getCalendarCodeMasters() {
		return calendarCodeMasters;
	}

	public void setCalendarCodeMasters(
			Set<CalendarCodeMaster> calendarCodeMasters) {
		this.calendarCodeMasters = calendarCodeMasters;
	}

	public Set<ReminderEventMaster> getReminderEventMasters() {
		return reminderEventMasters;
	}

	public void setReminderEventMasters(
			Set<ReminderEventMaster> reminderEventMasters) {
		this.reminderEventMasters = reminderEventMasters;
	}

	public Set<SchedulerMaster> getSchedulerMasters() {
		return schedulerMasters;
	}

	public void setSchedulerMasters(Set<SchedulerMaster> schedulerMasters) {
		this.schedulerMasters = schedulerMasters;
	}

	public Set<NotificationAlert> getNotificationAlerts() {
		return notificationAlerts;
	}

	public void setNotificationAlerts(Set<NotificationAlert> notificationAlerts) {
		this.notificationAlerts = notificationAlerts;
	}

}