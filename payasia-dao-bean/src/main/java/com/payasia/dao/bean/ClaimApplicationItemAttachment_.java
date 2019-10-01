package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2012-07-11T15:19:24.213+0530")
@StaticMetamodel(ClaimApplicationItemAttachment.class)
public class ClaimApplicationItemAttachment_ {
	public static volatile SingularAttribute<ClaimApplicationItemAttachment, Long> claimApplicationItemAttachmentId;
	public static volatile SingularAttribute<ClaimApplicationItemAttachment, byte[]> attachment;
	public static volatile SingularAttribute<ClaimApplicationItemAttachment, String> fileName;
	public static volatile SingularAttribute<ClaimApplicationItemAttachment, String> fileType;
	public static volatile SingularAttribute<ClaimApplicationItemAttachment, Timestamp> uploadedDate;
	public static volatile SingularAttribute<ClaimApplicationItemAttachment, ClaimApplicationItem> claimApplicationItem;
}
