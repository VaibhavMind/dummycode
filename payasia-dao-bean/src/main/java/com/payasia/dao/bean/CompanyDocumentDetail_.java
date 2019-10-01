package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2012-10-05T12:56:52.530+0530")
@StaticMetamodel(CompanyDocumentDetail.class)
public class CompanyDocumentDetail_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<CompanyDocumentDetail, Long> companyDocumentDetailID;
	public static volatile SingularAttribute<CompanyDocumentDetail, String> fileName;
	public static volatile SingularAttribute<CompanyDocumentDetail, String> fileType;
	public static volatile SingularAttribute<CompanyDocumentDetail, CompanyDocument> companyDocument;
}
