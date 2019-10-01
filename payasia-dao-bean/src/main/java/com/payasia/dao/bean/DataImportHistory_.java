package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2012-09-26T13:16:16.698+0530")
@StaticMetamodel(DataImportHistory.class)
public class DataImportHistory_ extends BaseEntity_ {
	public static volatile SingularAttribute<DataImportHistory, Long> dataImportHistoryId;
	public static volatile SingularAttribute<DataImportHistory, String> fileName;
	public static volatile SingularAttribute<DataImportHistory, Timestamp> importDate;
	public static volatile SingularAttribute<DataImportHistory, String> status;
	public static volatile SingularAttribute<DataImportHistory, Company> company;
	public static volatile SingularAttribute<DataImportHistory, EntityMaster> entityMaster;
	public static volatile SetAttribute<DataImportHistory, DataImportLog> dataImportLogs;
}
