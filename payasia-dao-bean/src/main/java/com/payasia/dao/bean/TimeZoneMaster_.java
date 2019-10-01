package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2012-07-27T15:32:52.874+0530")
@StaticMetamodel(TimeZoneMaster.class)
public class TimeZoneMaster_ {
	public static volatile SingularAttribute<TimeZoneMaster, Long> timeZoneId;
	public static volatile SingularAttribute<TimeZoneMaster, String> gmtOffset;
	public static volatile SingularAttribute<TimeZoneMaster, String> timeZoneName;
	public static volatile SetAttribute<TimeZoneMaster, Company> companies;
}
