package com.payasia.dao.bean;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-02-09T15:10:10.487+0530")
@StaticMetamodel(KeyPayIntLeaveApplication.class)
public class KeyPayIntLeaveApplication_ extends BaseEntity_ {
	public static volatile SingularAttribute<KeyPayIntLeaveApplication, Long> id;
	public static volatile SingularAttribute<KeyPayIntLeaveApplication, LeaveApplication> leaveApplication;
	public static volatile SingularAttribute<KeyPayIntLeaveApplication, Company> company;
	public static volatile SingularAttribute<KeyPayIntLeaveApplication, String> employeeNumber;
	public static volatile SingularAttribute<KeyPayIntLeaveApplication, Timestamp> startDate;
	public static volatile SingularAttribute<KeyPayIntLeaveApplication, Timestamp> endDate;
	public static volatile SingularAttribute<KeyPayIntLeaveApplication, BigDecimal> Hours;
	public static volatile SingularAttribute<KeyPayIntLeaveApplication, String> leaveTypeName;
	public static volatile SingularAttribute<KeyPayIntLeaveApplication, String> remarks;
	public static volatile SingularAttribute<KeyPayIntLeaveApplication, String> leaveStatus;
	public static volatile SingularAttribute<KeyPayIntLeaveApplication, Integer> syncStatus;
	public static volatile SingularAttribute<KeyPayIntLeaveApplication, Long> externalLeaveRequestId;
	public static volatile SingularAttribute<KeyPayIntLeaveApplication, Long> cancelLeaveApplicationId;
}
