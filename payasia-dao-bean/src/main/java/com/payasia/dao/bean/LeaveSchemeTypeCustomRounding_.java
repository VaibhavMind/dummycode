package com.payasia.dao.bean;

import java.math.BigDecimal;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-04-09T18:15:35.189+0530")
@StaticMetamodel(LeaveSchemeTypeCustomRounding.class)
public class LeaveSchemeTypeCustomRounding_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<LeaveSchemeTypeCustomRounding, Long> customRoundingId;
	public static volatile SingularAttribute<LeaveSchemeTypeCustomRounding, BigDecimal> fromRange;
	public static volatile SingularAttribute<LeaveSchemeTypeCustomRounding, BigDecimal> toRange;
	public static volatile SingularAttribute<LeaveSchemeTypeCustomRounding, BigDecimal> value;
	public static volatile SingularAttribute<LeaveSchemeTypeCustomRounding, LeaveSchemeTypeProration> leaveSchemeTypeProration;
}
