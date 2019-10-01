package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2012-09-26T13:44:35.863+0530")
@StaticMetamodel(EmployeeRoleMapping.class)
public class EmployeeRoleMapping_ extends BaseEntity_ {
	public static volatile SingularAttribute<EmployeeRoleMapping, EmployeeRoleMappingPK> id;
	public static volatile SingularAttribute<EmployeeRoleMapping, Company> company;
	public static volatile SingularAttribute<EmployeeRoleMapping, Employee> employee;
	public static volatile SingularAttribute<EmployeeRoleMapping, RoleMaster> roleMaster;
	public static volatile SetAttribute<EmployeeRoleMapping, CompanyEmployeeShortList> companyEmployeeShortLists;
}
