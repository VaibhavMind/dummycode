package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-06-07T14:31:35.633+0530")
@StaticMetamodel(ReminderEventConfig.class)
public class ReminderEventConfig_ extends BaseEntity_ {
	public static volatile SingularAttribute<ReminderEventConfig, Long> reminderEventConfigId;
	public static volatile SingularAttribute<ReminderEventConfig, Company> company;
	public static volatile SingularAttribute<ReminderEventConfig, ReminderEventMaster> reminderEventMaster;
	public static volatile SingularAttribute<ReminderEventConfig, LeaveScheme> leaveScheme;
	public static volatile SingularAttribute<ReminderEventConfig, LeaveTypeMaster> leaveTypeMaster;
	public static volatile SingularAttribute<ReminderEventConfig, AppCodeMaster> recipientType;
	public static volatile SingularAttribute<ReminderEventConfig, String> recipientValue;
	public static volatile SingularAttribute<ReminderEventConfig, Boolean> allowSendMailBeforeEventDay;
	public static volatile SingularAttribute<ReminderEventConfig, Integer> sendMailBeforeDays;
	public static volatile SingularAttribute<ReminderEventConfig, Integer> sendMailBeforeRepeatDays;
	public static volatile SingularAttribute<ReminderEventConfig, EmailTemplate> sendMailBeforeMailTemplate;
	public static volatile SingularAttribute<ReminderEventConfig, Boolean> allowSendMailOnEventDay;
	public static volatile SingularAttribute<ReminderEventConfig, EmailTemplate> sendMailOnEventMailTemplate;
	public static volatile SingularAttribute<ReminderEventConfig, Boolean> allowSendMailAfterEventDay;
	public static volatile SingularAttribute<ReminderEventConfig, Integer> sendMailAfterDays;
	public static volatile SingularAttribute<ReminderEventConfig, Integer> sendMailAfterTillDays;
	public static volatile SingularAttribute<ReminderEventConfig, Integer> sendMailAfterRepeatDays;
	public static volatile SingularAttribute<ReminderEventConfig, EmailTemplate> sendMailAfterMailTemplate;
}
