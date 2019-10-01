package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-02-22T17:34:33.496+0530")
@StaticMetamodel(ForumTopicCommentAttachment.class)
public class ForumTopicCommentAttachment_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<ForumTopicCommentAttachment, Long> topicAttachmentId;
	public static volatile SingularAttribute<ForumTopicCommentAttachment, ForumTopicComment> forumTopicComment;
	public static volatile SingularAttribute<ForumTopicCommentAttachment, String> fileName;
	public static volatile SingularAttribute<ForumTopicCommentAttachment, String> fileType;
}
