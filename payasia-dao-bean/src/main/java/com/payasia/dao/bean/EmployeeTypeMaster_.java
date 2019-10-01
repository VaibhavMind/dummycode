package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2012-05-14T15:11:32.669+0530")
@StaticMetamodel(EmployeeTypeMaster.class)
public class EmployeeTypeMaster_ {
	public static volatile SingularAttribute<EmployeeTypeMaster, Long> empTypeId;
	public static volatile SingularAttribute<EmployeeTypeMaster, String> empType;
	public static volatile SingularAttribute<EmployeeTypeMaster, String> empTypeDesc;
	public static volatile SetAttribute<EmployeeTypeMaster, EmployeePreferenceMaster> employeePreferenceMasters;
	public static volatile SingularAttribute<EmployeeTypeMaster, Company> company;
}
