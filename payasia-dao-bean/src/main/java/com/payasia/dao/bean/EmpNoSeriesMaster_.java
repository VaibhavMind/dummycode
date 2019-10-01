package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2012-09-26T13:39:23.123+0530")
@StaticMetamodel(EmpNoSeriesMaster.class)
public class EmpNoSeriesMaster_ extends BaseEntity_ {
	public static volatile SingularAttribute<EmpNoSeriesMaster, Long> empNoSeriesId;
	public static volatile SingularAttribute<EmpNoSeriesMaster, Boolean> active;
	public static volatile SingularAttribute<EmpNoSeriesMaster, String> prefix;
	public static volatile SingularAttribute<EmpNoSeriesMaster, String> seriesDesc;
	public static volatile SingularAttribute<EmpNoSeriesMaster, String> suffix;
	public static volatile SingularAttribute<EmpNoSeriesMaster, Company> company;
}
