package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-06-14T18:06:45.755+0530")
@StaticMetamodel(CompanyPayslipRelease.class)
public class CompanyPayslipRelease_ extends BaseEntity_ {
	public static volatile SingularAttribute<CompanyPayslipRelease, Long> companyPayslipReleaseId;
	public static volatile SingularAttribute<CompanyPayslipRelease, Company> company;
	public static volatile SingularAttribute<CompanyPayslipRelease, String> name;
	public static volatile SingularAttribute<CompanyPayslipRelease, Integer> year;
	public static volatile SingularAttribute<CompanyPayslipRelease, MonthMaster> monthMaster;
	public static volatile SingularAttribute<CompanyPayslipRelease, Integer> part;
	public static volatile SingularAttribute<CompanyPayslipRelease, Boolean> released;
	public static volatile SingularAttribute<CompanyPayslipRelease, Timestamp> releaseDateTime;
	public static volatile SingularAttribute<CompanyPayslipRelease, String> emailTo;
	public static volatile SingularAttribute<CompanyPayslipRelease, Boolean> sendEmail;
}
