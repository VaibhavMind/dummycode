package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-11-19T17:43:45.812+0530")
@StaticMetamodel(CompanyModuleMapping.class)
public class CompanyModuleMapping_ extends BaseEntity_ {
	public static volatile SingularAttribute<CompanyModuleMapping, Long> companyModuleMappingId;
	public static volatile SingularAttribute<CompanyModuleMapping, Company> company;
	public static volatile SingularAttribute<CompanyModuleMapping, ModuleMaster> moduleMaster;
}
