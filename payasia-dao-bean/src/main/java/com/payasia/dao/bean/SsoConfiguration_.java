package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2018-05-16T10:23:12.715+0530")
@StaticMetamodel(SsoConfiguration.class)
public class SsoConfiguration_ extends BaseEntity_ {
	public static volatile SingularAttribute<SsoConfiguration, Long> ssoIntegrationId;
	public static volatile SingularAttribute<SsoConfiguration, Company> company;
	public static volatile SingularAttribute<SsoConfiguration, String> idpIssuer;
	public static volatile SingularAttribute<SsoConfiguration, String> idpssoUrl;
	public static volatile SingularAttribute<SsoConfiguration, String> idpMetadata;
	public static volatile SingularAttribute<SsoConfiguration, String> idpmetadataUrl;
	public static volatile SingularAttribute<SsoConfiguration, Boolean> isEnableSso;
}
