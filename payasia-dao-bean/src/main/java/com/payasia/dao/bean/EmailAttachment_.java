package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-02-11T18:08:58.553+0530")
@StaticMetamodel(EmailAttachment.class)
public class EmailAttachment_ {
	public static volatile SingularAttribute<EmailAttachment, Long> emailAttachmentId;
	public static volatile SingularAttribute<EmailAttachment, String> fileName;
	public static volatile SingularAttribute<EmailAttachment, byte[]> attachment;
	public static volatile SingularAttribute<EmailAttachment, Email> email;
}
