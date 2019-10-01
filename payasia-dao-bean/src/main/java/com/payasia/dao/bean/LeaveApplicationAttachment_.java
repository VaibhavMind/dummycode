package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2012-07-05T11:53:06.559+0530")
@StaticMetamodel(LeaveApplicationAttachment.class)
public class LeaveApplicationAttachment_ {
	public static volatile SingularAttribute<LeaveApplicationAttachment, Long> leaveApplicationAttachmentId;
	public static volatile SingularAttribute<LeaveApplicationAttachment, byte[]> attachment;
	public static volatile SingularAttribute<LeaveApplicationAttachment, String> fileName;
	public static volatile SingularAttribute<LeaveApplicationAttachment, String> fileType;
	public static volatile SingularAttribute<LeaveApplicationAttachment, Timestamp> uploadedDate;
	public static volatile SingularAttribute<LeaveApplicationAttachment, LeaveApplication> leaveApplication;
}
