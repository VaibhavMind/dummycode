package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

import com.payasia.common.dto.NotificationAlertDTO;

public class NotificationForm  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1513591240749199983L;
	List<NotificationAlertDTO> notificationAlertDTOs;
	public List<NotificationAlertDTO> getNotificationAlertDTOs() {
		return notificationAlertDTOs;
	}
	public void setNotificationAlertDTOs(
			List<NotificationAlertDTO> notificationAlertDTOs) {
		this.notificationAlertDTOs = notificationAlertDTOs;
	}
	
	

}
