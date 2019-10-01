package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2012-10-05T13:00:13.902+0530")
@StaticMetamodel(EmailTemplateAttachment.class)
public class EmailTemplateAttachment_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<EmailTemplateAttachment, Long> emailAttachmentId;
	public static volatile SingularAttribute<EmailTemplateAttachment, byte[]> attachment;
	public static volatile SingularAttribute<EmailTemplateAttachment, String> fileName;
	public static volatile SingularAttribute<EmailTemplateAttachment, EmailTemplate> emailTemplate;
}
