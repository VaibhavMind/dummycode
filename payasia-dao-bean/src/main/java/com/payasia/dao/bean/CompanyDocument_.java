package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-05-02T19:53:14.461+0530")
@StaticMetamodel(CompanyDocument.class)
public class CompanyDocument_ extends BaseEntity_ {
	public static volatile SingularAttribute<CompanyDocument, Long> documentId;
	public static volatile SingularAttribute<CompanyDocument, String> description;
	public static volatile SingularAttribute<CompanyDocument, Timestamp> uploadedDate;
	public static volatile SingularAttribute<CompanyDocument, Integer> year;
	public static volatile SingularAttribute<CompanyDocument, String> source;
	public static volatile SingularAttribute<CompanyDocument, Integer> part;
	public static volatile SingularAttribute<CompanyDocument, Boolean> released;
	public static volatile SingularAttribute<CompanyDocument, MonthMaster> month;
	public static volatile SingularAttribute<CompanyDocument, Company> company;
	public static volatile SingularAttribute<CompanyDocument, DocumentCategoryMaster> documentCategoryMaster;
	public static volatile SetAttribute<CompanyDocument, CompanyDocumentDetail> companyDocumentDetails;
	public static volatile SetAttribute<CompanyDocument, CompanyDocumentShortList> companyDocumentShortLists;
}
