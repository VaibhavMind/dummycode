package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-06-07T14:35:07.855+0530")
@StaticMetamodel(ClaimCategoryMaster.class)
public class ClaimCategoryMaster_ extends BaseEntity_ {
	public static volatile SingularAttribute<ClaimCategoryMaster, Long> claimCategoryId;
	public static volatile SingularAttribute<ClaimCategoryMaster, String> claimCategoryDesc;
	public static volatile SingularAttribute<ClaimCategoryMaster, String> claimCategoryName;
	public static volatile SingularAttribute<ClaimCategoryMaster, String> code;
	public static volatile SingularAttribute<ClaimCategoryMaster, Company> company;
	public static volatile SetAttribute<ClaimCategoryMaster, ClaimItemMaster> claimItemMasters;
}
