package com.payasia.dao;

import java.util.List;

import com.payasia.common.dto.NotificationAlertConditionDTO;
import com.payasia.dao.bean.NotificationAlert;

public interface NotificationAlertDAO {

	void update(NotificationAlert notificationAlert);

	void save(NotificationAlert notificationAlert);

	void delete(NotificationAlert notificationAlert);

	NotificationAlert findByID(long notificationAlertId);

	List<NotificationAlert> findByCondtion(
			NotificationAlertConditionDTO notificationAlertConditionDTO);

	void updateById(List<Long> notificationAlertIds);

	List<NotificationAlert> getIOSDeviceNotificationsAlerts();

}
