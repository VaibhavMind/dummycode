package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2012-07-12T19:14:34.707+0530")
@StaticMetamodel(OTApplication.class)
public class OTApplication_ {
	public static volatile SingularAttribute<OTApplication, Long> otApplicationId;
	public static volatile SingularAttribute<OTApplication, Timestamp> createdDate;
	public static volatile SingularAttribute<OTApplication, String> remarks;
	public static volatile SingularAttribute<OTApplication, Timestamp> updatedDate;
	public static volatile SingularAttribute<OTApplication, Company> company;
	public static volatile SingularAttribute<OTApplication, Employee> employee;
	public static volatile SingularAttribute<OTApplication, OTStatusMaster> otStatusMaster;
	public static volatile SingularAttribute<OTApplication, OTTemplate> otTemplate;
	public static volatile SetAttribute<OTApplication, OTApplicationItem> otApplicationItems;
	public static volatile SetAttribute<OTApplication, OTApplicationReviewer> otApplicationReviewers;
	public static volatile SetAttribute<OTApplication, OTApplicationWorkflow> otApplicationWorkflows;
}
