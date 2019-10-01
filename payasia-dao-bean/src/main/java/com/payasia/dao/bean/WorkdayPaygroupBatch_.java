package com.payasia.dao.bean;

import java.sql.Date;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2018-10-30T13:07:28.684+0530")
@StaticMetamodel(WorkdayPaygroupBatch.class)
public class WorkdayPaygroupBatch_ extends BaseEntity_ {
	public static volatile SingularAttribute<WorkdayPaygroupBatch, Long> workdayPaygroupBatchId;
	public static volatile SingularAttribute<WorkdayPaygroupBatch, Date> payPeriodStartDate;
	public static volatile SingularAttribute<WorkdayPaygroupBatch, Date> payPeriodEndDate;
	public static volatile SingularAttribute<WorkdayPaygroupBatch, Boolean> isEmployeeData;
	public static volatile SingularAttribute<WorkdayPaygroupBatch, Boolean> isLatest;
	public static volatile SingularAttribute<WorkdayPaygroupBatch, Company> company;
	public static volatile SingularAttribute<WorkdayPaygroupBatch, WorkdayAppCodeMaster> workdayAppCode;
	public static volatile SingularAttribute<WorkdayPaygroupBatch, WorkdayAppCodeMaster> batchTypeAppCode;
}
