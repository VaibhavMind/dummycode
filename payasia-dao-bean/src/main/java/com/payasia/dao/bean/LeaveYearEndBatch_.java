package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-06-14T18:06:45.995+0530")
@StaticMetamodel(LeaveYearEndBatch.class)
public class LeaveYearEndBatch_ {
	public static volatile SingularAttribute<LeaveYearEndBatch, Long> leaveYearEndBatchId;
	public static volatile SingularAttribute<LeaveYearEndBatch, Timestamp> processedDate;
	public static volatile SingularAttribute<LeaveYearEndBatch, Timestamp> deletedDate;
	public static volatile SingularAttribute<LeaveYearEndBatch, Integer> employeeCount;
	public static volatile SingularAttribute<LeaveYearEndBatch, LeaveSchemeType> leaveSchemeType;
	public static volatile SingularAttribute<LeaveYearEndBatch, Company> company;
	public static volatile SetAttribute<LeaveYearEndBatch, LeaveYearEndEmployeeDetail> leaveYearEndEmployeeDetails;
}
