package com.payasia.dao.bean;

import java.math.BigDecimal;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2018-10-06T16:48:19.126+0530")
@StaticMetamodel(WorkdayPaygroupEmployeeTimeOff.class)
public class WorkdayPaygroupEmployeeTimeOff_ extends BaseEntity_ {
	public static volatile SingularAttribute<WorkdayPaygroupEmployeeTimeOff, Long> workdayPaygroupEmployeeTimeOffId;
	public static volatile SingularAttribute<WorkdayPaygroupEmployeeTimeOff, Employee> employee;
	public static volatile SingularAttribute<WorkdayPaygroupEmployeeTimeOff, Company> company;
	public static volatile SingularAttribute<WorkdayPaygroupEmployeeTimeOff, String> code;
	public static volatile SingularAttribute<WorkdayPaygroupEmployeeTimeOff, String> timeOffType;
	public static volatile SingularAttribute<WorkdayPaygroupEmployeeTimeOff, BigDecimal> quantity;
	public static volatile SingularAttribute<WorkdayPaygroupEmployeeTimeOff, String> unitOfTime;
}
