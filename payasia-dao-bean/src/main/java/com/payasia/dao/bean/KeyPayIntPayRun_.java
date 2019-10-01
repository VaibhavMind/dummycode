package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2015-10-20T12:25:11.718+0530")
@StaticMetamodel(KeyPayIntPayRun.class)
public class KeyPayIntPayRun_ extends BaseEntity_ {
	public static volatile SingularAttribute<KeyPayIntPayRun, Long> keyPayIntPayRunId;
	public static volatile SingularAttribute<KeyPayIntPayRun, Long> payRunId;
	public static volatile SingularAttribute<KeyPayIntPayRun, Company> company;
	public static volatile SingularAttribute<KeyPayIntPayRun, Integer> processStatus;
	public static volatile SingularAttribute<KeyPayIntPayRun, Timestamp> payRunFinalizedDate;
	public static volatile SingularAttribute<KeyPayIntPayRun, Timestamp> payRunParameterDate;
	public static volatile SetAttribute<KeyPayIntPayRun, KeyPayIntPayRunDetail> keyPayIntPayRunDetails;
}
