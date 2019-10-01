package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2018-05-07T18:52:50.086+0530")
@StaticMetamodel(WorkdayFieldMappingDataTransformation.class)
public class WorkdayFieldMappingDataTransformation_ extends BaseEntity_ {
	public static volatile SingularAttribute<WorkdayFieldMappingDataTransformation, Long> fieldMappingDataTransformationId;
	public static volatile SingularAttribute<WorkdayFieldMappingDataTransformation, WorkdayFtpFieldMapping> workdayFtpFieldMapping;
	public static volatile SingularAttribute<WorkdayFieldMappingDataTransformation, String> workdayFieldValue;
	public static volatile SingularAttribute<WorkdayFieldMappingDataTransformation, String> hroFieldValue;
	public static volatile SingularAttribute<WorkdayFieldMappingDataTransformation, Company> company;
}
