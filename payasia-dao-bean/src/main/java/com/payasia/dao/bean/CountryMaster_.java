package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-09-06T11:16:01.270+0530")
@StaticMetamodel(CountryMaster.class)
public class CountryMaster_ {
	public static volatile SingularAttribute<CountryMaster, Long> countryId;
	public static volatile SingularAttribute<CountryMaster, String> countryName;
	public static volatile SingularAttribute<CountryMaster, String> nationality;
	public static volatile SetAttribute<CountryMaster, Company> companies;
	public static volatile SetAttribute<CountryMaster, HolidayConfigMaster> holidayConfigMasters;
	public static volatile SetAttribute<CountryMaster, StateMaster> stateMasters;
	public static volatile SetAttribute<CountryMaster, CompanyHolidayCalendarDetail> companyHolidayCalendarDetails;
}
