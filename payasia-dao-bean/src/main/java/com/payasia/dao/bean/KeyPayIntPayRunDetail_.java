package com.payasia.dao.bean;

import java.math.BigDecimal;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2015-10-08T15:33:36.393+0530")
@StaticMetamodel(KeyPayIntPayRunDetail.class)
public class KeyPayIntPayRunDetail_ extends BaseEntity_ {
	public static volatile SingularAttribute<KeyPayIntPayRunDetail, Long> detailId;
	public static volatile SingularAttribute<KeyPayIntPayRunDetail, KeyPayIntPayRun> keyPayIntPayRun;
	public static volatile SingularAttribute<KeyPayIntPayRunDetail, Employee> employee;
	public static volatile SingularAttribute<KeyPayIntPayRunDetail, String> externalId;
	public static volatile SingularAttribute<KeyPayIntPayRunDetail, Long> leaveCategoryId;
	public static volatile SingularAttribute<KeyPayIntPayRunDetail, String> leaveTypeName;
	public static volatile SingularAttribute<KeyPayIntPayRunDetail, BigDecimal> leaveEntitlement;
	public static volatile SingularAttribute<KeyPayIntPayRunDetail, String> remarks;
	public static volatile SingularAttribute<KeyPayIntPayRunDetail, Long> employeeLeaveSchemeTypeHistoryId;
}
