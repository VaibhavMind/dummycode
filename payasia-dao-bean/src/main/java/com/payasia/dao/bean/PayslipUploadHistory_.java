package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-06-07T14:44:16.075+0530")
@StaticMetamodel(PayslipUploadHistory.class)
public class PayslipUploadHistory_ extends BaseEntity_ {
	public static volatile SingularAttribute<PayslipUploadHistory, Long> payslipUploadHistoryId;
	public static volatile SingularAttribute<PayslipUploadHistory, Timestamp> payslip_Upload_Date;
	public static volatile SingularAttribute<PayslipUploadHistory, Integer> year;
	public static volatile SingularAttribute<PayslipUploadHistory, Company> company;
	public static volatile SingularAttribute<PayslipUploadHistory, MonthMaster> monthMaster;
}
