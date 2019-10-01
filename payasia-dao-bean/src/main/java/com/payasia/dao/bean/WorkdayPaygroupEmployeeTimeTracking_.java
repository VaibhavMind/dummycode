package com.payasia.dao.bean;

import java.math.BigDecimal;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2018-10-06T16:48:05.109+0530")
@StaticMetamodel(WorkdayPaygroupEmployeeTimeTracking.class)
public class WorkdayPaygroupEmployeeTimeTracking_ extends BaseEntity_ {
	public static volatile SingularAttribute<WorkdayPaygroupEmployeeTimeTracking, Long> workdayPaygroupEmployeeTimeTrackingId;
	public static volatile SingularAttribute<WorkdayPaygroupEmployeeTimeTracking, Employee> employee;
	public static volatile SingularAttribute<WorkdayPaygroupEmployeeTimeTracking, Company> company;
	public static volatile SingularAttribute<WorkdayPaygroupEmployeeTimeTracking, String> code;
	public static volatile SingularAttribute<WorkdayPaygroupEmployeeTimeTracking, BigDecimal> quantity;
	public static volatile SingularAttribute<WorkdayPaygroupEmployeeTimeTracking, String> unitOfTime;
}
