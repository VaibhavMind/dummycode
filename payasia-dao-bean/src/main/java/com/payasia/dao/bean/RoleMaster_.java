package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-10-23T10:51:11.170+0530")
@StaticMetamodel(RoleMaster.class)
public class RoleMaster_ extends BaseEntity_ {
	public static volatile SingularAttribute<RoleMaster, Long> roleId;
	public static volatile SingularAttribute<RoleMaster, Boolean> deletable;
	public static volatile SingularAttribute<RoleMaster, String> roleDesc;
	public static volatile SingularAttribute<RoleMaster, String> roleName;
	public static volatile SetAttribute<RoleMaster, EmployeeRoleMapping> employeeRoleMappings;
	public static volatile SingularAttribute<RoleMaster, Company> company;
	public static volatile SetAttribute<RoleMaster, PrivilegeMaster> privilegeMasters;
	public static volatile SetAttribute<RoleMaster, RoleSectionMapping> roleSectionMappings;
	public static volatile SetAttribute<RoleMaster, EmployeeRoleSectionMapping> employeeRoleSectionMappings;
}
