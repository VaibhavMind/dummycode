package com.payasia.dao.bean;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-06-14T18:06:46.005+0530")
@StaticMetamodel(LeaveYearEndEmployeeDetail.class)
public class LeaveYearEndEmployeeDetail_ {
	public static volatile SingularAttribute<LeaveYearEndEmployeeDetail, Long> leaveYearEndEmployeeDetailId;
	public static volatile SingularAttribute<LeaveYearEndEmployeeDetail, LeaveYearEndBatch> leaveYearEndBatch;
	public static volatile SingularAttribute<LeaveYearEndEmployeeDetail, Employee> employee;
	public static volatile SingularAttribute<LeaveYearEndEmployeeDetail, BigDecimal> balance;
	public static volatile SingularAttribute<LeaveYearEndEmployeeDetail, BigDecimal> encashed;
	public static volatile SingularAttribute<LeaveYearEndEmployeeDetail, BigDecimal> lapsed;
	public static volatile SingularAttribute<LeaveYearEndEmployeeDetail, BigDecimal> closingBalance;
	public static volatile SingularAttribute<LeaveYearEndEmployeeDetail, Timestamp> deletedDate;
}
