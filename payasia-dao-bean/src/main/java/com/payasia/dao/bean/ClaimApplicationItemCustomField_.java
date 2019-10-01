package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-06-07T14:29:47.537+0530")
@StaticMetamodel(ClaimApplicationItemCustomField.class)
public class ClaimApplicationItemCustomField_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<ClaimApplicationItemCustomField, Long> claimItemCustomFieldId;
	public static volatile SingularAttribute<ClaimApplicationItemCustomField, ClaimApplicationItem> claimApplicationItem;
	public static volatile SingularAttribute<ClaimApplicationItemCustomField, ClaimTemplateItemCustomField> claimTemplateItemCustomField;
	public static volatile SingularAttribute<ClaimApplicationItemCustomField, String> value;
}
