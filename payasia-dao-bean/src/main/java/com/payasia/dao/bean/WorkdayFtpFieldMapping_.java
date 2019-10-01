package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2018-05-07T18:53:28.783+0530")
@StaticMetamodel(WorkdayFtpFieldMapping.class)
public class WorkdayFtpFieldMapping_ extends BaseEntity_ {
	public static volatile SingularAttribute<WorkdayFtpFieldMapping, Long> workdayFtpFieldMappingId;
	public static volatile SingularAttribute<WorkdayFtpFieldMapping, Company> company;
	public static volatile SingularAttribute<WorkdayFtpFieldMapping, WorkdayFieldMaster> workdayField;
	public static volatile SingularAttribute<WorkdayFtpFieldMapping, DataDictionary> hroField;
}
