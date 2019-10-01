package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2018-10-05T18:21:07.457+0530")
@StaticMetamodel(WorkdayPaygroupBatchData.class)
public class WorkdayPaygroupBatchData_ extends BaseEntity_ {
	public static volatile SingularAttribute<WorkdayPaygroupBatchData, Long> workdayPaygroupBatchDataId;
	public static volatile SingularAttribute<WorkdayPaygroupBatchData, WorkdayPaygroupBatch> workdayPaygroupBatch;
	public static volatile SingularAttribute<WorkdayPaygroupBatchData, Employee> employee;
	public static volatile SingularAttribute<WorkdayPaygroupBatchData, String> employeeXML;
	public static volatile SingularAttribute<WorkdayPaygroupBatchData, Boolean> isNewEmployee;
	public static volatile SingularAttribute<WorkdayPaygroupBatchData, Timestamp> effectiveDate;
	public static volatile SingularAttribute<WorkdayPaygroupBatchData, String> importType;
	public static volatile SingularAttribute<WorkdayPaygroupBatchData, Company> company;
}
