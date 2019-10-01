package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-02-13T18:11:17.030+0530")
@StaticMetamodel(LeaveGrantBatchDetail.class)
public class LeaveGrantBatchDetail_ {
	public static volatile SingularAttribute<LeaveGrantBatchDetail, Long> leaveGrantBatchDetailId;
	public static volatile SingularAttribute<LeaveGrantBatchDetail, LeaveGrantBatch> leaveGrantBatch;
	public static volatile SingularAttribute<LeaveGrantBatchDetail, LeaveSchemeType> leaveSchemeType;
	public static volatile SingularAttribute<LeaveGrantBatchDetail, Timestamp> fromPeriod;
	public static volatile SingularAttribute<LeaveGrantBatchDetail, Timestamp> toPeriod;
	public static volatile SingularAttribute<LeaveGrantBatchDetail, Long> employeesCount;
	public static volatile SingularAttribute<LeaveGrantBatchDetail, Timestamp> deletedDate;
	public static volatile SetAttribute<LeaveGrantBatchDetail, LeaveGrantBatchEmployeeDetail> leaveGrantBatchEmployeeDetails;
}
