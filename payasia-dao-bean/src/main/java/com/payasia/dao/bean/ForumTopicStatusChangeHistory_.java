package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-11-19T17:43:45.924+0530")
@StaticMetamodel(ForumTopicStatusChangeHistory.class)
public class ForumTopicStatusChangeHistory_ extends CompanyUpdatedBaseEntity_ {
	public static volatile SingularAttribute<ForumTopicStatusChangeHistory, Long> topicStatusChangeHistoryId;
	public static volatile SingularAttribute<ForumTopicStatusChangeHistory, ForumTopic> forumTopic;
	public static volatile SingularAttribute<ForumTopicStatusChangeHistory, String> remarks;
	public static volatile SingularAttribute<ForumTopicStatusChangeHistory, Boolean> changedStatus;
	public static volatile SingularAttribute<ForumTopicStatusChangeHistory, Employee> createdBy;
	public static volatile SingularAttribute<ForumTopicStatusChangeHistory, Timestamp> createdDate;
}
