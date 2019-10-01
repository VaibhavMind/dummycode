package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2017-03-02T20:26:35.411+0530")
@StaticMetamodel(CoherentShiftApplication.class)
public class CoherentShiftApplication_ extends BaseEntity_ {
	public static volatile SingularAttribute<CoherentShiftApplication, Long> shiftApplicationID;
	public static volatile SingularAttribute<CoherentShiftApplication, Company> company;
	public static volatile SingularAttribute<CoherentShiftApplication, Employee> employee;
	public static volatile SingularAttribute<CoherentShiftApplication, TimesheetBatch> timesheetBatch;
	public static volatile SingularAttribute<CoherentShiftApplication, Integer> totalShifts;
	public static volatile SingularAttribute<CoherentShiftApplication, TimesheetStatusMaster> timesheetStatusMaster;
	public static volatile SetAttribute<CoherentShiftApplication, CoherentShiftApplicationReviewer> coherentShiftApplicationReviewer;
	public static volatile SetAttribute<CoherentShiftApplication, CoherentShiftApplicationWorkflow> coherentShiftApplicationWorkflows;
}
