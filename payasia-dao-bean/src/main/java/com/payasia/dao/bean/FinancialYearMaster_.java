package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2012-04-21T12:27:09.766+0530")
@StaticMetamodel(FinancialYearMaster.class)
public class FinancialYearMaster_ {
	public static volatile SingularAttribute<FinancialYearMaster, Long> finYearId;
	public static volatile SetAttribute<FinancialYearMaster, Company> companies;
	public static volatile SingularAttribute<FinancialYearMaster, MonthMaster> monthMaster1;
	public static volatile SingularAttribute<FinancialYearMaster, MonthMaster> monthMaster2;
}
