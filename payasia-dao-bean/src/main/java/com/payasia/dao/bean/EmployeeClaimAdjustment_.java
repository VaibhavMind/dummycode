package com.payasia.dao.bean;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2015-03-03T16:30:47.393+0530")
@StaticMetamodel(EmployeeClaimAdjustment.class)
public class EmployeeClaimAdjustment_ extends BaseEntity_ {
	public static volatile SingularAttribute<EmployeeClaimAdjustment, Long> employeeClaimAdjustmentID;
	public static volatile SingularAttribute<EmployeeClaimAdjustment, Timestamp> effectiveDate;
	public static volatile SingularAttribute<EmployeeClaimAdjustment, String> remarks;
	public static volatile SingularAttribute<EmployeeClaimAdjustment, BigDecimal> amount;
	public static volatile SingularAttribute<EmployeeClaimAdjustment, Boolean> resignationRollback;
	public static volatile SingularAttribute<EmployeeClaimAdjustment, Company> company;
	public static volatile SingularAttribute<EmployeeClaimAdjustment, EmployeeClaimTemplate> employeeClaimTemplate;
}
