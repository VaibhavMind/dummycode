package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-09-27T15:58:32.834+0530")
@StaticMetamodel(HRISChangeRequest.class)
public class HRISChangeRequest_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<HRISChangeRequest, Long> hrisChangeRequestId;
	public static volatile SingularAttribute<HRISChangeRequest, Employee> employee;
	public static volatile SingularAttribute<HRISChangeRequest, DataDictionary> dataDictionary;
	public static volatile SingularAttribute<HRISChangeRequest, String> oldValue;
	public static volatile SingularAttribute<HRISChangeRequest, String> newValue;
	public static volatile SingularAttribute<HRISChangeRequest, HRISStatusMaster> hrisStatusMaster;
	public static volatile SetAttribute<HRISChangeRequest, HRISChangeRequestWorkflow> changeRequestWorkflows;
	public static volatile SetAttribute<HRISChangeRequest, HRISChangeRequestReviewer> hrisChangeRequestReviewers;
	public static volatile SingularAttribute<HRISChangeRequest, Integer> tableRecordSequence;
}
