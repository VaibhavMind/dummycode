package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-04-09T17:56:01.800+0530")
@StaticMetamodel(LeaveSchemeTypeGrantCondition.class)
public class LeaveSchemeTypeGrantCondition_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<LeaveSchemeTypeGrantCondition, Long> leaveSchemeTypeGrantConditionId;
	public static volatile SingularAttribute<LeaveSchemeTypeGrantCondition, LeaveSchemeTypeAvailingLeave> leaveSchemeTypeAvailingLeave;
	public static volatile SingularAttribute<LeaveSchemeTypeGrantCondition, AppCodeMaster> grantCondition;
	public static volatile SingularAttribute<LeaveSchemeTypeGrantCondition, LeaveSchemeType> grantConditionValue;
}
