package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-06-14T18:06:45.769+0530")
@StaticMetamodel(CurrencyMaster.class)
public class CurrencyMaster_ {
	public static volatile SingularAttribute<CurrencyMaster, Long> currencyId;
	public static volatile SingularAttribute<CurrencyMaster, String> currencyDesc;
	public static volatile SingularAttribute<CurrencyMaster, String> currencyCode;
	public static volatile SetAttribute<CurrencyMaster, CompanyExchangeRate> companyExchangeRates;
	public static volatile SetAttribute<CurrencyMaster, ClaimTemplate> claimTemplates;
	public static volatile SetAttribute<CurrencyMaster, ClaimApplicationItem> claimApplicationItem;
	public static volatile SingularAttribute<CurrencyMaster, String> currencyName;
}
