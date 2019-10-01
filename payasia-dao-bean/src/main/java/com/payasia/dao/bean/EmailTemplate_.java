package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-02-11T18:28:51.160+0530")
@StaticMetamodel(EmailTemplate.class)
public class EmailTemplate_ extends BaseEntity_ {
	public static volatile SingularAttribute<EmailTemplate, Long> emailTemplateId;
	public static volatile SingularAttribute<EmailTemplate, String> body;
	public static volatile SingularAttribute<EmailTemplate, String> name;
	public static volatile SingularAttribute<EmailTemplate, String> subject;
	public static volatile SingularAttribute<EmailTemplate, Company> company;
	public static volatile SetAttribute<EmailTemplate, EmailTemplateAttachment> emailTemplateAttachments;
	public static volatile SingularAttribute<EmailTemplate, EmailTemplateSubCategoryMaster> emailTemplateSubCategoryMaster;
	public static volatile SetAttribute<EmailTemplate, ReminderEventConfig> sendMailBeforeReminderEventConfigs;
	public static volatile SetAttribute<EmailTemplate, ReminderEventConfig> sendMailOnEventReminderEventConfigs;
	public static volatile SetAttribute<EmailTemplate, ReminderEventConfig> sendMailAfterReminderEventConfigs;
}
