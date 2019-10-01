package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-02-11T18:08:58.785+0530")
@StaticMetamodel(ReminderEventMaster.class)
public class ReminderEventMaster_ {
	public static volatile SingularAttribute<ReminderEventMaster, Long> reminderEventId;
	public static volatile SingularAttribute<ReminderEventMaster, String> event;
	public static volatile SingularAttribute<ReminderEventMaster, String> eventDesc;
	public static volatile SingularAttribute<ReminderEventMaster, ModuleMaster> moduleMaster;
	public static volatile SetAttribute<ReminderEventMaster, ReminderEventConfig> reminderEventConfigs;
}
