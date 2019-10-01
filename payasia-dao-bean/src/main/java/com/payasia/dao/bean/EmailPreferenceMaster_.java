package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-06-14T18:06:45.830+0530")
@StaticMetamodel(EmailPreferenceMaster.class)
public class EmailPreferenceMaster_ extends BaseEntity_ {
	public static volatile SingularAttribute<EmailPreferenceMaster, Long> emailPrefId;
	public static volatile SingularAttribute<EmailPreferenceMaster, String> contactEmail;
	public static volatile SingularAttribute<EmailPreferenceMaster, String> logoLocation;
	public static volatile SingularAttribute<EmailPreferenceMaster, String> systemEmail;
	public static volatile SingularAttribute<EmailPreferenceMaster, String> companyUrl;
	public static volatile SingularAttribute<EmailPreferenceMaster, String> systemEmailForSendingEmails;
	public static volatile SingularAttribute<EmailPreferenceMaster, Company> company;
}
