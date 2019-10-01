package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-06-07T14:45:20.579+0530")
@StaticMetamodel(ClaimTemplateItemShortlist.class)
public class ClaimTemplateItemShortlist_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<ClaimTemplateItemShortlist, Long> short_List_ID;
	public static volatile SingularAttribute<ClaimTemplateItemShortlist, ClaimTemplateItem> claimTemplateItem;
	public static volatile SingularAttribute<ClaimTemplateItemShortlist, String> closeBracket;
	public static volatile SingularAttribute<ClaimTemplateItemShortlist, String> equalityOperator;
	public static volatile SingularAttribute<ClaimTemplateItemShortlist, String> logicalOperator;
	public static volatile SingularAttribute<ClaimTemplateItemShortlist, String> openBracket;
	public static volatile SingularAttribute<ClaimTemplateItemShortlist, String> value;
	public static volatile SingularAttribute<ClaimTemplateItemShortlist, DataDictionary> dataDictionary;
}
