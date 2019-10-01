package com.payasia.dao.bean;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-06-14T18:06:45.722+0530")
@StaticMetamodel(CompanyExchangeRate.class)
public class CompanyExchangeRate_ extends BaseEntity_ {
	public static volatile SingularAttribute<CompanyExchangeRate, Long> compExchangeRateId;
	public static volatile SingularAttribute<CompanyExchangeRate, BigDecimal> exchangeRate;
	public static volatile SingularAttribute<CompanyExchangeRate, Timestamp> startDate;
	public static volatile SingularAttribute<CompanyExchangeRate, Timestamp> endDate;
	public static volatile SingularAttribute<CompanyExchangeRate, Company> company;
	public static volatile SingularAttribute<CompanyExchangeRate, CurrencyMaster> currencyMaster;
}
