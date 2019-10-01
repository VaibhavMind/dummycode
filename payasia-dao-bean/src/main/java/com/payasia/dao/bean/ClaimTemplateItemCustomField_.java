package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2017-09-25T18:40:05.822+0530")
@StaticMetamodel(ClaimTemplateItemCustomField.class)
public class ClaimTemplateItemCustomField_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<ClaimTemplateItemCustomField, Long> customFieldId;
	public static volatile SingularAttribute<ClaimTemplateItemCustomField, ClaimTemplateItem> claimTemplateItem;
	public static volatile SingularAttribute<ClaimTemplateItemCustomField, String> fieldName;
	public static volatile SingularAttribute<ClaimTemplateItemCustomField, Boolean> mandatory;
	public static volatile SetAttribute<ClaimTemplateItemCustomField, ClaimApplicationItemCustomField> claimApplicationItemCustomFields;
	public static volatile SetAttribute<ClaimTemplateItemCustomField, ClaimTemplateItemCustomFieldDropDown> claimTemplateItemCustomFieldDropDowns;
	public static volatile SingularAttribute<ClaimTemplateItemCustomField, AppCodeMaster> fieldType;
}
