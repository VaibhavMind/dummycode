package com.payasia.dao.bean;

import java.math.BigDecimal;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-06-21T15:48:28.490+0530")
@StaticMetamodel(ClaimApplication.class)
public class ClaimApplication_ extends BaseEntity_ {
	public static volatile SingularAttribute<ClaimApplication, Long> claimApplicationId;
	public static volatile SingularAttribute<ClaimApplication, Long> claimNumber;
	public static volatile SingularAttribute<ClaimApplication, String> remarks;
	public static volatile SingularAttribute<ClaimApplication, BigDecimal> totalAmount;
	public static volatile SingularAttribute<ClaimApplication, Integer> totalItems;
	public static volatile SingularAttribute<ClaimApplication, String> emailCC;
	public static volatile SingularAttribute<ClaimApplication, Boolean> visibleToEmployee;
	public static volatile SingularAttribute<ClaimApplication, ClaimStatusMaster> claimStatusMaster;
	public static volatile SingularAttribute<ClaimApplication, EmployeeClaimTemplate> employeeClaimTemplate;
	public static volatile SingularAttribute<ClaimApplication, Company> company;
	public static volatile SingularAttribute<ClaimApplication, Employee> employee;
	public static volatile SetAttribute<ClaimApplication, ClaimApplicationReviewer> claimApplicationReviewers;
	public static volatile SetAttribute<ClaimApplication, ClaimApplicationWorkflow> claimApplicationWorkflows;
	public static volatile SetAttribute<ClaimApplication, ClaimApplicationItem> claimApplicationItems;
}
