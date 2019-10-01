package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2018-09-10T17:15:49.388+0530")
@StaticMetamodel(WorkdayFtpImportHistory.class)
public class WorkdayFtpImportHistory_ extends UpdatedBaseEntity_ {
	public static volatile SingularAttribute<WorkdayFtpImportHistory, Long> ftpImportHistoryId;
	public static volatile SingularAttribute<WorkdayFtpImportHistory, Timestamp> createdDate;
	public static volatile SingularAttribute<WorkdayFtpImportHistory, String> importFileName;
	public static volatile SingularAttribute<WorkdayFtpImportHistory, Boolean> importStatus;
	public static volatile SingularAttribute<WorkdayFtpImportHistory, String> importType;
	public static volatile SingularAttribute<WorkdayFtpImportHistory, Integer> totalEmpRecords;
	public static volatile SingularAttribute<WorkdayFtpImportHistory, Integer> existingEmpRecordsUpdated;
	public static volatile SingularAttribute<WorkdayFtpImportHistory, Integer> newEmpRecordsUpdated;
	public static volatile SingularAttribute<WorkdayFtpImportHistory, String> failedRemarks;
	public static volatile SingularAttribute<WorkdayFtpImportHistory, Company> company;
	public static volatile SingularAttribute<WorkdayFtpImportHistory, Employee> createdBy;
}
