package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-04-09T18:19:45.245+0530")
@StaticMetamodel(LeaveSchemeTypeProration.class)
public class LeaveSchemeTypeProration_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<LeaveSchemeTypeProration, Long> leaveSchemeTypeProrationId;
	public static volatile SingularAttribute<LeaveSchemeTypeProration, AppCodeMaster> prorationBasedOn;
	public static volatile SingularAttribute<LeaveSchemeTypeProration, AppCodeMaster> prorationMethod;
	public static volatile SingularAttribute<LeaveSchemeTypeProration, Boolean> prorationFirstYearOnly;
	public static volatile SingularAttribute<LeaveSchemeTypeProration, AppCodeMaster> roundingMethod;
	public static volatile SingularAttribute<LeaveSchemeTypeProration, LeaveSchemeType> leaveSchemeType;
	public static volatile SingularAttribute<LeaveSchemeTypeProration, Integer> cutOffDay;
	public static volatile SingularAttribute<LeaveSchemeTypeProration, Boolean> noProration;
	public static volatile SetAttribute<LeaveSchemeTypeProration, LeaveSchemeTypeCustomRounding> leaveSchemeTypeCustomRoundings;
	public static volatile SetAttribute<LeaveSchemeTypeProration, LeaveSchemeTypeCustomProration> leaveSchemeTypeCustomProrations;
}
