package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-02-11T17:40:45.393+0530")
@StaticMetamodel(PasswordPolicyConfigMaster.class)
public class PasswordPolicyConfigMaster_ extends BaseEntity_ {
	public static volatile SingularAttribute<PasswordPolicyConfigMaster, Long> pwdPolicyConfigId;
	public static volatile SingularAttribute<PasswordPolicyConfigMaster, Integer> allowedInvalidAttempts;
	public static volatile SingularAttribute<PasswordPolicyConfigMaster, Boolean> enablePasswordComplexity;
	public static volatile SingularAttribute<PasswordPolicyConfigMaster, Boolean> enablePwdPolicy;
	public static volatile SingularAttribute<PasswordPolicyConfigMaster, Integer> expiryReminderDays;
	public static volatile SingularAttribute<PasswordPolicyConfigMaster, Boolean> includeSpecialCharacter;
	public static volatile SingularAttribute<PasswordPolicyConfigMaster, Integer> maxExpiryDaysLimit;
	public static volatile SingularAttribute<PasswordPolicyConfigMaster, Integer> memoryListSize;
	public static volatile SingularAttribute<PasswordPolicyConfigMaster, Integer> minPwdLength;
	public static volatile SingularAttribute<PasswordPolicyConfigMaster, Integer> maxPwdLength;
	public static volatile SingularAttribute<PasswordPolicyConfigMaster, Boolean> includeNumericCharacter;
	public static volatile SingularAttribute<PasswordPolicyConfigMaster, Boolean> includeUpperLowerCase;
	public static volatile SingularAttribute<PasswordPolicyConfigMaster, Company> company;
}
