package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2015-03-03T16:30:47.456+0530")
@StaticMetamodel(ForgotPasswordToken.class)
public class ForgotPasswordToken_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<ForgotPasswordToken, Long> tokenId;
	public static volatile SingularAttribute<ForgotPasswordToken, String> token;
	public static volatile SingularAttribute<ForgotPasswordToken, Employee> employee;
	public static volatile SingularAttribute<ForgotPasswordToken, Boolean> active;
}
