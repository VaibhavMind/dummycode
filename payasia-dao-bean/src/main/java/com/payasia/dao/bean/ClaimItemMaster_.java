package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2015-07-02T11:08:51.349+0530")
@StaticMetamodel(ClaimItemMaster.class)
public class ClaimItemMaster_ extends BaseEntity_ {
	public static volatile SingularAttribute<ClaimItemMaster, Long> claimItemId;
	public static volatile SingularAttribute<ClaimItemMaster, String> accountCode;
	public static volatile SingularAttribute<ClaimItemMaster, String> claimItemDesc;
	public static volatile SingularAttribute<ClaimItemMaster, String> claimItemName;
	public static volatile SingularAttribute<ClaimItemMaster, String> code;
	public static volatile SingularAttribute<ClaimItemMaster, Boolean> visibility;
	public static volatile SingularAttribute<ClaimItemMaster, Integer> sortOrder;
	public static volatile SingularAttribute<ClaimItemMaster, ClaimCategoryMaster> claimCategoryMaster;
	public static volatile SingularAttribute<ClaimItemMaster, Company> company;
	public static volatile SetAttribute<ClaimItemMaster, ClaimTemplateItem> claimTemplateItems;
}
