package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2012-09-26T13:15:26.008+0530")
@StaticMetamodel(CompanyExternalLink.class)
public class CompanyExternalLink_ extends BaseEntity_ {
	public static volatile SingularAttribute<CompanyExternalLink, Long> externalLinkId;
	public static volatile SingularAttribute<CompanyExternalLink, byte[]> image;
	public static volatile SingularAttribute<CompanyExternalLink, String> link;
	public static volatile SingularAttribute<CompanyExternalLink, String> imageName;
	public static volatile SingularAttribute<CompanyExternalLink, String> message;
	public static volatile SingularAttribute<CompanyExternalLink, Company> company;
}
