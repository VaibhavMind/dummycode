package com.payasia.dao.bean;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2012-09-26T13:48:07.788+0530")
@StaticMetamodel(PayDataCollection.class)
public class PayDataCollection_ extends BaseEntity_ {
	public static volatile SingularAttribute<PayDataCollection, Long> payDataCollectionId;
	public static volatile SingularAttribute<PayDataCollection, BigDecimal> amount;
	public static volatile SingularAttribute<PayDataCollection, Timestamp> endDate;
	public static volatile SingularAttribute<PayDataCollection, Timestamp> startDate;
	public static volatile SingularAttribute<PayDataCollection, Company> company;
	public static volatile SingularAttribute<PayDataCollection, Employee> employee;
	public static volatile SingularAttribute<PayDataCollection, Paycode> paycode;
}
