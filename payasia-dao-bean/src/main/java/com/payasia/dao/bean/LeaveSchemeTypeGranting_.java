package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-06-21T15:48:28.841+0530")
@StaticMetamodel(LeaveSchemeTypeGranting.class)
public class LeaveSchemeTypeGranting_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<LeaveSchemeTypeGranting, Long> leaveSchemeTypeGrantingId;
	public static volatile SingularAttribute<LeaveSchemeTypeGranting, AppCodeMaster> leaveCalendar;
	public static volatile SingularAttribute<LeaveSchemeTypeGranting, AppCodeMaster> distributionMethod;
	public static volatile SingularAttribute<LeaveSchemeTypeGranting, LeaveSchemeType> leaveSchemeType;
	public static volatile SetAttribute<LeaveSchemeTypeGranting, LeaveSchemeTypeEntitlement> leaveSchemeTypeEntitlements;
	public static volatile SingularAttribute<LeaveSchemeTypeGranting, Integer> grantDay;
	public static volatile SingularAttribute<LeaveSchemeTypeGranting, Boolean> expireEntitlement;
}
