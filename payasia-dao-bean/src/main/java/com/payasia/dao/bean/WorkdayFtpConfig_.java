package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2018-04-25T15:56:37.024+0530")
@StaticMetamodel(WorkdayFtpConfig.class)
public class WorkdayFtpConfig_ extends BaseEntity_ {
	public static volatile SingularAttribute<WorkdayFtpConfig, Long> workdayFtpConfigId;
	public static volatile SingularAttribute<WorkdayFtpConfig, Company> company;
	public static volatile SingularAttribute<WorkdayFtpConfig, String> ftpServerAddress;
	public static volatile SingularAttribute<WorkdayFtpConfig, Integer> ftpPort;
	public static volatile SingularAttribute<WorkdayFtpConfig, String> userName;
	public static volatile SingularAttribute<WorkdayFtpConfig, String> password;
	public static volatile SingularAttribute<WorkdayFtpConfig, String> dateFormat;
	public static volatile SingularAttribute<WorkdayFtpConfig, AppCodeMaster> protocol;
	public static volatile SingularAttribute<WorkdayFtpConfig, String> encryptionType;
	public static volatile SingularAttribute<WorkdayFtpConfig, String> pgpPassword;
	public static volatile SingularAttribute<WorkdayFtpConfig, String> employeeDataRemotePath;
	public static volatile SingularAttribute<WorkdayFtpConfig, String> employeeDataMoveToFolderPath;
	public static volatile SingularAttribute<WorkdayFtpConfig, String> employeeDataFrequency;
	public static volatile SingularAttribute<WorkdayFtpConfig, Boolean> employeeDataIsActive;
	public static volatile SingularAttribute<WorkdayFtpConfig, String> payrollDataRemotePath;
	public static volatile SingularAttribute<WorkdayFtpConfig, String> payrollDataMoveToFolderPath;
	public static volatile SingularAttribute<WorkdayFtpConfig, String> payrollDataFrequency;
	public static volatile SingularAttribute<WorkdayFtpConfig, Boolean> payrollDataIsActive;
}
