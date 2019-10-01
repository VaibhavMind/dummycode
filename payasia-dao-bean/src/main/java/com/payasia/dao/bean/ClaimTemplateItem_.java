package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-06-07T14:42:35.607+0530")
@StaticMetamodel(ClaimTemplateItem.class)
public class ClaimTemplateItem_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<ClaimTemplateItem, Long> claimTemplateItemId;
	public static volatile SingularAttribute<ClaimTemplateItem, Boolean> visibility;
	public static volatile SingularAttribute<ClaimTemplateItem, Boolean> workflowChanged;
	public static volatile SingularAttribute<ClaimTemplateItem, ClaimItemMaster> claimItemMaster;
	public static volatile SingularAttribute<ClaimTemplateItem, ClaimTemplate> claimTemplate;
	public static volatile SetAttribute<ClaimTemplateItem, ClaimTemplateItemWorkflow> claimTemplateItemWorkflows;
	public static volatile SetAttribute<ClaimTemplateItem, ClaimTemplateItemShortlist> claimTemplateItemShortlists;
	public static volatile SetAttribute<ClaimTemplateItem, ClaimTemplateItemClaimType> claimTemplateItemClaimTypes;
	public static volatile SetAttribute<ClaimTemplateItem, ClaimTemplateItemGeneral> claimTemplateItemGenerals;
	public static volatile SetAttribute<ClaimTemplateItem, ClaimTemplateItemCustomField> claimTemplateItemCustomFields;
	public static volatile SetAttribute<ClaimTemplateItem, EmployeeClaimTemplateItem> employeeClaimTemplateItems;
}
