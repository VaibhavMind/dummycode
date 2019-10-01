package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2015-05-01T11:15:18.869+0530")
@StaticMetamodel(LundinDepartment.class)
public class LundinDepartment_ extends BaseEntity_ {
	public static volatile SingularAttribute<LundinDepartment, Long> departmentId;
	public static volatile SingularAttribute<LundinDepartment, DynamicFormFieldRefValue> dynamicFormFieldRefValue;
	public static volatile SingularAttribute<LundinDepartment, Company> company;
	public static volatile SingularAttribute<LundinDepartment, Timestamp> effectiveDate;
	public static volatile SingularAttribute<LundinDepartment, Boolean> status;
	public static volatile SingularAttribute<LundinDepartment, AppCodeMaster> departmentType;
	public static volatile SingularAttribute<LundinDepartment, Integer> order;
	public static volatile SingularAttribute<LundinDepartment, LundinBlock> defaultBlock;
	public static volatile SingularAttribute<LundinDepartment, LundinAFE> defaultAFE;
}
