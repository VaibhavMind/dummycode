package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2012-10-05T12:58:48.407+0530")
@StaticMetamodel(CompanyDocumentShortList.class)
public class CompanyDocumentShortList_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<CompanyDocumentShortList, Long> shortListId;
	public static volatile SingularAttribute<CompanyDocumentShortList, String> closeBracket;
	public static volatile SingularAttribute<CompanyDocumentShortList, String> equalityOperator;
	public static volatile SingularAttribute<CompanyDocumentShortList, String> logicalOperator;
	public static volatile SingularAttribute<CompanyDocumentShortList, String> openBracket;
	public static volatile SingularAttribute<CompanyDocumentShortList, String> value;
	public static volatile SingularAttribute<CompanyDocumentShortList, CompanyDocument> companyDocument;
	public static volatile SingularAttribute<CompanyDocumentShortList, DataDictionary> dataDictionary;
}
