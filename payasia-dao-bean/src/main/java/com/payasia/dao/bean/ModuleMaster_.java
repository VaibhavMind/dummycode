package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-12-11T13:34:32.099+0530")
@StaticMetamodel(ModuleMaster.class)
public class ModuleMaster_ {
	public static volatile SingularAttribute<ModuleMaster, Long> moduleId;
	public static volatile SingularAttribute<ModuleMaster, String> moduleName;
	public static volatile SetAttribute<ModuleMaster, CompanyModuleMapping> companyModuleMappings;
	public static volatile SetAttribute<ModuleMaster, PrivilegeMaster> privilegeMasters;
	public static volatile SetAttribute<ModuleMaster, CalendarCodeMaster> calendarCodeMasters;
	public static volatile SetAttribute<ModuleMaster, ReminderEventMaster> reminderEventMasters;
	public static volatile SetAttribute<ModuleMaster, SchedulerMaster> schedulerMasters;
	public static volatile SetAttribute<ModuleMaster, NotificationAlert> notificationAlerts;
}
