package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2017-09-25T18:40:06.035+0530")
@StaticMetamodel(ForumTopicComment.class)
public class ForumTopicComment_ extends CompanyUpdatedBaseEntity_ {
	public static volatile SingularAttribute<ForumTopicComment, Long> topicCommentId;
	public static volatile SingularAttribute<ForumTopicComment, ForumTopic> forumTopic;
	public static volatile SingularAttribute<ForumTopicComment, Employee> createdBy;
	public static volatile SingularAttribute<ForumTopicComment, Timestamp> publishedDate;
	public static volatile SingularAttribute<ForumTopicComment, AppCodeMaster> publishedStatus;
	public static volatile SingularAttribute<ForumTopicComment, Timestamp> createdDate;
	public static volatile SingularAttribute<ForumTopicComment, String> comment;
	public static volatile SingularAttribute<ForumTopicComment, Boolean> sendMail;
	public static volatile SingularAttribute<ForumTopicComment, String> emailTo;
	public static volatile SingularAttribute<ForumTopicComment, String> emailCC;
	public static volatile SingularAttribute<ForumTopicComment, String> topicCommentEmployeeIds;
	public static volatile SetAttribute<ForumTopicComment, ForumTopicCommentAttachment> forumTopicAttachments;
}
