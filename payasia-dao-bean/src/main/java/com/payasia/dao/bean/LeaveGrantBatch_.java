package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-06-07T14:23:19.904+0530")
@StaticMetamodel(LeaveGrantBatch.class)
public class LeaveGrantBatch_ extends BaseEntity_ {
	public static volatile SingularAttribute<LeaveGrantBatch, Long> leaveGrantBatchId;
	public static volatile SingularAttribute<LeaveGrantBatch, Long> batchNumber;
	public static volatile SingularAttribute<LeaveGrantBatch, String> batchDesc;
	public static volatile SingularAttribute<LeaveGrantBatch, Timestamp> batchDate;
	public static volatile SingularAttribute<LeaveGrantBatch, Timestamp> deletedDate;
	public static volatile SingularAttribute<LeaveGrantBatch, Company> company;
	public static volatile SetAttribute<LeaveGrantBatch, LeaveGrantBatchDetail> leaveGrantBatchDetails;
}
