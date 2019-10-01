package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2012-07-12T19:14:34.794+0530")
@StaticMetamodel(OTTemplateItem.class)
public class OTTemplateItem_ {
	public static volatile SingularAttribute<OTTemplateItem, Long> OTTemplateItemId;
	public static volatile SingularAttribute<OTTemplateItem, Boolean> visibility;
	public static volatile SingularAttribute<OTTemplateItem, OTItemMaster> otItemMaster;
	public static volatile SingularAttribute<OTTemplateItem, OTTemplate> otTemplate;
	public static volatile SetAttribute<OTTemplateItem, OTApplicationItemDetail> otApplicationItemDetails;
}
