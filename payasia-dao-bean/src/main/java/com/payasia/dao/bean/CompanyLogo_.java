package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2012-09-20T20:22:36.050+0530")
@StaticMetamodel(CompanyLogo.class)
public class CompanyLogo_ extends BaseEntity_ {
	public static volatile SingularAttribute<CompanyLogo, Long> companyLogoId;
	public static volatile SingularAttribute<CompanyLogo, byte[]> image;
	public static volatile SingularAttribute<CompanyLogo, String> imageName;
	public static volatile SingularAttribute<CompanyLogo, String> logoDesc;
	public static volatile SingularAttribute<CompanyLogo, Timestamp> uploadedDate;
	public static volatile SingularAttribute<CompanyLogo, Company> company;
}
