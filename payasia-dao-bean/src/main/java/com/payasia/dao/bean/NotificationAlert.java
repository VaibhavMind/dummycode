package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The persistent class for the Leave_Application database table.
 * 
 */
@Entity
@Table(name = "Notification_Alert")
public class NotificationAlert extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "Notification_Alert_ID")
	private long notificationAlertId;

	 
	@ManyToOne
	@JoinColumn(name = "Employee_ID")
	private Employee employee;

	 
	@ManyToOne
	@JoinColumn(name = "Module_ID")
	private ModuleMaster moduleMaster;

	@Column(name = "Message")
	private String message;

	@Column(name = "Shown_Status")
	private Boolean shownStatus;

	public long getNotificationAlertId() {
		return notificationAlertId;
	}

	public void setNotificationAlertId(long notificationAlertId) {
		this.notificationAlertId = notificationAlertId;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public ModuleMaster getModuleMaster() {
		return moduleMaster;
	}

	public void setModuleMaster(ModuleMaster moduleMaster) {
		this.moduleMaster = moduleMaster;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Boolean getShownStatus() {
		return shownStatus;
	}

	public void setShownStatus(Boolean shownStatus) {
		this.shownStatus = shownStatus;
	}

}