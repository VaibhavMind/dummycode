package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2014-11-13T14:57:31.531+0530")
@StaticMetamodel(EmployeeClaimTemplate.class)
public class EmployeeClaimTemplate_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<EmployeeClaimTemplate, Long> employeeClaimTemplateId;
	public static volatile SingularAttribute<EmployeeClaimTemplate, Timestamp> startDate;
	public static volatile SingularAttribute<EmployeeClaimTemplate, Timestamp> endDate;
	public static volatile SingularAttribute<EmployeeClaimTemplate, ClaimTemplate> claimTemplate;
	public static volatile SingularAttribute<EmployeeClaimTemplate, Employee> employee;
	public static volatile SetAttribute<EmployeeClaimTemplate, EmployeeClaimReviewer> employeeClaimReviewers;
	public static volatile SetAttribute<EmployeeClaimTemplate, EmployeeClaimTemplateItem> employeeClaimTemplateItems;
	public static volatile SetAttribute<EmployeeClaimTemplate, ClaimApplication> claimApplications;
	public static volatile SetAttribute<EmployeeClaimTemplate, EmployeeClaimAdjustment> employeeClaimAdjustments;
}
