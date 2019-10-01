package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-02-11T17:40:45.206+0530")
@StaticMetamodel(OTItemMaster.class)
public class OTItemMaster_ {
	public static volatile SingularAttribute<OTItemMaster, Long> otItemId;
	public static volatile SingularAttribute<OTItemMaster, String> code;
	public static volatile SingularAttribute<OTItemMaster, String> otItemDesc;
	public static volatile SingularAttribute<OTItemMaster, String> otItemName;
	public static volatile SingularAttribute<OTItemMaster, Boolean> visibility;
	public static volatile SingularAttribute<OTItemMaster, Company> company;
	public static volatile SetAttribute<OTItemMaster, OTTemplateItem> otTemplateItems;
}
