package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2012-10-05T12:59:15.995+0530")
@StaticMetamodel(DataImportLog.class)
public class DataImportLog_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<DataImportLog, Long> dataImportLogId;
	public static volatile SingularAttribute<DataImportLog, String> columnName;
	public static volatile SingularAttribute<DataImportLog, String> failureType;
	public static volatile SingularAttribute<DataImportLog, String> remarks;
	public static volatile SingularAttribute<DataImportLog, Long> rowNumber;
	public static volatile SingularAttribute<DataImportLog, DataImportHistory> dataImportHistory;
}
