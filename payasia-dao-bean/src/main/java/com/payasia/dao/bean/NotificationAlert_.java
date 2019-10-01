package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2014-11-10T14:41:42.377+0530")
@StaticMetamodel(NotificationAlert.class)
public class NotificationAlert_ extends BaseEntity_ {
	public static volatile SingularAttribute<NotificationAlert, Long> notificationAlertId;
	public static volatile SingularAttribute<NotificationAlert, Employee> employee;
	public static volatile SingularAttribute<NotificationAlert, ModuleMaster> moduleMaster;
	public static volatile SingularAttribute<NotificationAlert, String> message;
	public static volatile SingularAttribute<NotificationAlert, Boolean> shownStatus;
}
