package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2017-09-25T18:40:06.027+0530")
@StaticMetamodel(ForumTopic.class)
public class ForumTopic_ extends UpdatedBaseEntity_ {
	public static volatile SingularAttribute<ForumTopic, Long> topicId;
	public static volatile SingularAttribute<ForumTopic, Company> company;
	public static volatile SingularAttribute<ForumTopic, String> topicName;
	public static volatile SingularAttribute<ForumTopic, String> topicDesc;
	public static volatile SingularAttribute<ForumTopic, Boolean> status;
	public static volatile SingularAttribute<ForumTopic, String> emailTo;
	public static volatile SingularAttribute<ForumTopic, String> emailCC;
	public static volatile SingularAttribute<ForumTopic, String> topicEmployeeIds;
	public static volatile SingularAttribute<ForumTopic, Employee> createdBy;
	public static volatile SingularAttribute<ForumTopic, Timestamp> createdDate;
	public static volatile SetAttribute<ForumTopic, ForumTopicComment> forumTopicComments;
	public static volatile SetAttribute<ForumTopic, ForumTopicStatusChangeHistory> forumTopicStatusChangeHistorys;
}
