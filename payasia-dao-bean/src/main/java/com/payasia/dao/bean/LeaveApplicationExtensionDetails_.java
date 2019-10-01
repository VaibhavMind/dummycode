package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2018-04-05T16:57:17.655+0530")
@StaticMetamodel(LeaveApplicationExtensionDetails.class)
public class LeaveApplicationExtensionDetails_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<LeaveApplicationExtensionDetails, Long> leaveApplicationExtensionDetailsId;
	public static volatile SingularAttribute<LeaveApplicationExtensionDetails, Timestamp> fromDate;
	public static volatile SingularAttribute<LeaveApplicationExtensionDetails, Timestamp> toDate;
	public static volatile SingularAttribute<LeaveApplicationExtensionDetails, LeaveSessionMaster> leaveSessionMaster1;
	public static volatile SingularAttribute<LeaveApplicationExtensionDetails, LeaveSessionMaster> leaveSessionMaster2;
	public static volatile SingularAttribute<LeaveApplicationExtensionDetails, String> remarks;
	public static volatile SingularAttribute<LeaveApplicationExtensionDetails, LeaveApplication> leaveApplication;
}
