package com.payasia.dao.bean;

import java.math.BigDecimal;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-04-09T18:21:28.337+0530")
@StaticMetamodel(LeaveSchemeTypeCustomProration.class)
public class LeaveSchemeTypeCustomProration_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<LeaveSchemeTypeCustomProration, Long> customProrationId;
	public static volatile SingularAttribute<LeaveSchemeTypeCustomProration, Integer> fromRange;
	public static volatile SingularAttribute<LeaveSchemeTypeCustomProration, Integer> toRange;
	public static volatile SingularAttribute<LeaveSchemeTypeCustomProration, BigDecimal> value;
	public static volatile SingularAttribute<LeaveSchemeTypeCustomProration, LeaveSchemeTypeProration> leaveSchemeTypeProration;
}
