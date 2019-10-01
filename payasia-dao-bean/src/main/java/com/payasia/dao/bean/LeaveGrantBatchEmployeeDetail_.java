package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-06-07T14:22:24.029+0530")
@StaticMetamodel(LeaveGrantBatchEmployeeDetail.class)
public class LeaveGrantBatchEmployeeDetail_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<LeaveGrantBatchEmployeeDetail, Long> leaveGrantBatchEmployeeDetailId;
	public static volatile SingularAttribute<LeaveGrantBatchEmployeeDetail, LeaveGrantBatchDetail> leaveGrantBatchDetail;
	public static volatile SingularAttribute<LeaveGrantBatchEmployeeDetail, Employee> employee;
	public static volatile SingularAttribute<LeaveGrantBatchEmployeeDetail, Float> grantedDays;
	public static volatile SingularAttribute<LeaveGrantBatchEmployeeDetail, Timestamp> deletedDate;
}
