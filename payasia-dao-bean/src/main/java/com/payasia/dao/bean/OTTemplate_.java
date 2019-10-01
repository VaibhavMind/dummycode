package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2012-07-12T19:14:34.781+0530")
@StaticMetamodel(OTTemplate.class)
public class OTTemplate_ {
	public static volatile SingularAttribute<OTTemplate, Long> otTemplateId;
	public static volatile SingularAttribute<OTTemplate, String> accountCode;
	public static volatile SingularAttribute<OTTemplate, String> templateName;
	public static volatile SingularAttribute<OTTemplate, Boolean> visibility;
	public static volatile SetAttribute<OTTemplate, EmployeeOTReviewer> employeeOtReviewers;
	public static volatile SingularAttribute<OTTemplate, Company> company;
	public static volatile SetAttribute<OTTemplate, OTTemplateItem> otTemplateItems;
	public static volatile SetAttribute<OTTemplate, OTTemplateWorkflow> otTemplateWorkflows;
	public static volatile SetAttribute<OTTemplate, OTApplication> otApplications;
}
