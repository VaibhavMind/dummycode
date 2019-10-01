package com.payasia.dao.bean;

import java.math.BigDecimal;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2015-03-03T16:30:47.565+0530")
@StaticMetamodel(LeaveSchemeTypeYearEnd.class)
public class LeaveSchemeTypeYearEnd_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<LeaveSchemeTypeYearEnd, Long> leaveSchemeTypeYearEndID;
	public static volatile SingularAttribute<LeaveSchemeTypeYearEnd, Boolean> allowCarryForward;
	public static volatile SingularAttribute<LeaveSchemeTypeYearEnd, BigDecimal> maxCarryForwardLimit;
	public static volatile SingularAttribute<LeaveSchemeTypeYearEnd, BigDecimal> annualCarryForwardPercentage;
	public static volatile SingularAttribute<LeaveSchemeTypeYearEnd, Integer> leaveExpiryDays;
	public static volatile SingularAttribute<LeaveSchemeTypeYearEnd, LeaveSchemeType> leaveSchemeType;
}
