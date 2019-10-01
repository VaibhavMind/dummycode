package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-01-29T12:56:37.119+0530")
@StaticMetamodel(PasswordSecurityQuestionMaster.class)
public class PasswordSecurityQuestionMaster_ extends BaseEntity_ {
	public static volatile SingularAttribute<PasswordSecurityQuestionMaster, Long> pwdSecurityQuestionId;
	public static volatile SingularAttribute<PasswordSecurityQuestionMaster, String> secretQuestion;
	public static volatile SingularAttribute<PasswordSecurityQuestionMaster, Company> company;
}
