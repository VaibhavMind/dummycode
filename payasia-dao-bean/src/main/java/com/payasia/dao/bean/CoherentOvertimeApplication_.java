package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-12-02T16:06:51.472+0530")
@StaticMetamodel(CoherentOvertimeApplication.class)
public class CoherentOvertimeApplication_ extends BaseEntity_ {
	public static volatile SingularAttribute<CoherentOvertimeApplication, Long> overtimeApplicationID;
	public static volatile SingularAttribute<CoherentOvertimeApplication, Company> company;
	public static volatile SingularAttribute<CoherentOvertimeApplication, Employee> employee;
	public static volatile SingularAttribute<CoherentOvertimeApplication, TimesheetBatch> timesheetBatch;
	public static volatile SingularAttribute<CoherentOvertimeApplication, Double> totalOT10Day;
	public static volatile SingularAttribute<CoherentOvertimeApplication, Double> totalOT20Day;
	public static volatile SingularAttribute<CoherentOvertimeApplication, Double> totalOTHours;
	public static volatile SingularAttribute<CoherentOvertimeApplication, Double> totalOT15Hours;
	public static volatile SingularAttribute<CoherentOvertimeApplication, String> remarks;
	public static volatile SetAttribute<CoherentOvertimeApplication, CoherentOvertimeApplicationDetail> coherentOvertimeApplicationDetails;
	public static volatile SetAttribute<CoherentOvertimeApplication, CoherentOvertimeApplicationReviewer> coherentOvertimeApplicationReviewers;
	public static volatile SetAttribute<CoherentOvertimeApplication, CoherentOvertimeApplicationWorkflow> coherentOvertimeApplicationWorkflows;
	public static volatile SingularAttribute<CoherentOvertimeApplication, TimesheetStatusMaster> timesheetStatusMaster;
}
