package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-02-27T11:02:59.995+0800")
@StaticMetamodel(StateMaster.class)
public class StateMaster_ {
	public static volatile SingularAttribute<StateMaster, Long> stateId;
	public static volatile SingularAttribute<StateMaster, String> stateName;
	public static volatile SetAttribute<StateMaster, HolidayConfigMaster> holidayConfigMasters;
	public static volatile SingularAttribute<StateMaster, CountryMaster> countryMaster;
	public static volatile SetAttribute<StateMaster, CompanyHolidayCalendarDetail> companyHolidayCalendarDetails;
}
