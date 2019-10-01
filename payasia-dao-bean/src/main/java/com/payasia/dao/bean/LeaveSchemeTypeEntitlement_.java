package com.payasia.dao.bean;

import java.math.BigDecimal;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-10-03T09:34:45.535+0530")
@StaticMetamodel(LeaveSchemeTypeEntitlement.class)
public class LeaveSchemeTypeEntitlement_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<LeaveSchemeTypeEntitlement, Long> entitlementId;
	public static volatile SingularAttribute<LeaveSchemeTypeEntitlement, BigDecimal> value;
	public static volatile SingularAttribute<LeaveSchemeTypeEntitlement, Integer> year;
	public static volatile SingularAttribute<LeaveSchemeTypeEntitlement, LeaveSchemeTypeGranting> leaveSchemeTypeGranting;
}
