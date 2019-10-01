package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2015-03-03T16:30:47.628+0530")
@StaticMetamodel(SchedulerStatus.class)
public class SchedulerStatus_ {
	public static volatile SingularAttribute<SchedulerStatus, Long> schedulerStatusId;
	public static volatile SingularAttribute<SchedulerStatus, Timestamp> lastRunDate;
	public static volatile SingularAttribute<SchedulerStatus, Boolean> lastRunStatus;
	public static volatile SingularAttribute<SchedulerStatus, String> failureMessage;
	public static volatile SingularAttribute<SchedulerStatus, Company> company;
	public static volatile SingularAttribute<SchedulerStatus, SchedulerMaster> schedulerMaster;
}
