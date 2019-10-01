package com.payasia.common.dto;

import java.io.Serializable;

public class NotificationAlertDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2202022667604088553L;
	private Long notificationId;
	private String notificationTitle;
	private String notificationText;
	public Long getNotificationId() {
		return notificationId;
	}
	public void setNotificationId(Long notificationId) {
		this.notificationId = notificationId;
	}
	public String getNotificationTitle() {
		return notificationTitle;
	}
	public void setNotificationTitle(String notificationTitle) {
		this.notificationTitle = notificationTitle;
	}
	public String getNotificationText() {
		return notificationText;
	}
	public void setNotificationText(String notificationText) {
		this.notificationText = notificationText;
	}
	
	
	
	
	
	

}
