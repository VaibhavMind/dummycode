package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-11-19T17:43:46.037+0530")
@StaticMetamodel(LundinTimesheetDetail.class)
public class LundinTimesheetDetail_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<LundinTimesheetDetail, Long> timesheetDetailID;
	public static volatile SingularAttribute<LundinTimesheetDetail, EmployeeTimesheetApplication> employeeTimesheetApplication;
	public static volatile SingularAttribute<LundinTimesheetDetail, Timestamp> timesheetDate;
	public static volatile SingularAttribute<LundinTimesheetDetail, LundinBlock> lundinBlock;
	public static volatile SingularAttribute<LundinTimesheetDetail, LundinAFE> lundinAFE;
	public static volatile SingularAttribute<LundinTimesheetDetail, AppCodeMaster> value;
	public static volatile SingularAttribute<LundinTimesheetDetail, String> blockCode;
	public static volatile SingularAttribute<LundinTimesheetDetail, String> blockName;
	public static volatile SingularAttribute<LundinTimesheetDetail, String> afeCode;
	public static volatile SingularAttribute<LundinTimesheetDetail, String> afeName;
}
