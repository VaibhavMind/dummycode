package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2012-10-05T14:08:03.077+0530")
@StaticMetamodel(HolidayConfigMaster.class)
public class HolidayConfigMaster_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<HolidayConfigMaster, Long> holidayConfigMasterId;
	public static volatile SingularAttribute<HolidayConfigMaster, Timestamp> holidayDate;
	public static volatile SingularAttribute<HolidayConfigMaster, String> holidayDesc;
	public static volatile SingularAttribute<HolidayConfigMaster, CountryMaster> countryMaster;
	public static volatile SingularAttribute<HolidayConfigMaster, StateMaster> stateMaster;
}
