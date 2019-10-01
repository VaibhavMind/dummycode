package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-02-11T18:08:58.818+0530")
@StaticMetamodel(SchedulerMaster.class)
public class SchedulerMaster_ {
	public static volatile SingularAttribute<SchedulerMaster, Long> schedulerId;
	public static volatile SingularAttribute<SchedulerMaster, String> schedulerName;
	public static volatile SingularAttribute<SchedulerMaster, String> schedulerDesc;
	public static volatile SingularAttribute<SchedulerMaster, Timestamp> SchedulerTime;
	public static volatile SingularAttribute<SchedulerMaster, ModuleMaster> moduleMaster;
	public static volatile SetAttribute<SchedulerMaster, SchedulerStatus> schedulerStatuses;
}
