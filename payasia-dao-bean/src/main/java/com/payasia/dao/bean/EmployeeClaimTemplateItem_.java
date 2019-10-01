package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-06-07T14:32:41.629+0530")
@StaticMetamodel(EmployeeClaimTemplateItem.class)
public class EmployeeClaimTemplateItem_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<EmployeeClaimTemplateItem, Long> employeeClaimTemplateItemId;
	public static volatile SingularAttribute<EmployeeClaimTemplateItem, EmployeeClaimTemplate> employeeClaimTemplate;
	public static volatile SingularAttribute<EmployeeClaimTemplateItem, ClaimTemplateItem> claimTemplateItem;
	public static volatile SetAttribute<EmployeeClaimTemplateItem, ClaimApplicationItem> claimApplicationItems;
	public static volatile SingularAttribute<EmployeeClaimTemplateItem, Boolean> active;
}
