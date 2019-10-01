package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-11-19T17:43:45.846+0530")
@StaticMetamodel(Email.class)
public class Email_ {
	public static volatile SingularAttribute<Email, Long> emailId;
	public static volatile SingularAttribute<Email, String> emailFrom;
	public static volatile SingularAttribute<Email, String> emailTo;
	public static volatile SingularAttribute<Email, String> emailCC;
	public static volatile SingularAttribute<Email, String> emailBCC;
	public static volatile SingularAttribute<Email, String> subject;
	public static volatile SingularAttribute<Email, String> body;
	public static volatile SingularAttribute<Email, Timestamp> createdDate;
	public static volatile SingularAttribute<Email, Timestamp> sentDate;
	public static volatile SingularAttribute<Email, Company> company;
	public static volatile SetAttribute<Email, EmailAttachment> emailAttachments;
}
