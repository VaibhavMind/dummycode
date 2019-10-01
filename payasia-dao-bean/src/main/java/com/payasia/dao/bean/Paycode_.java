package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2012-09-26T13:48:27.869+0530")
@StaticMetamodel(Paycode.class)
public class Paycode_ extends BaseEntity_ {
	public static volatile SingularAttribute<Paycode, Long> paycodeID;
	public static volatile SingularAttribute<Paycode, String> paycode;
	public static volatile SetAttribute<Paycode, PayDataCollection> payDataCollections;
	public static volatile SingularAttribute<Paycode, Company> company;
}
