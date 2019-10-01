package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2018-07-30T12:55:30.755+0530")
@StaticMetamodel(IntegrationMaster.class)
public class IntegrationMaster_ {
	public static volatile SingularAttribute<IntegrationMaster, Long> integrationId;
	public static volatile SingularAttribute<IntegrationMaster, String> integrationName;
	public static volatile SingularAttribute<IntegrationMaster, String> username;
	public static volatile SingularAttribute<IntegrationMaster, String> password;
	public static volatile SingularAttribute<IntegrationMaster, String> baseURL;
	public static volatile SingularAttribute<IntegrationMaster, String> apiKey;
	public static volatile SingularAttribute<IntegrationMaster, Long> companyId;
	public static volatile SingularAttribute<IntegrationMaster, Long> externalCompanyId;
	public static volatile SingularAttribute<IntegrationMaster, Boolean> active;
}
