package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2012-09-20T20:22:57.930+0530")
@StaticMetamodel(Payslip.class)
public class Payslip_ extends BaseEntity_ {
	public static volatile SingularAttribute<Payslip, Long> payslipId;
	public static volatile SingularAttribute<Payslip, Integer> part;
	public static volatile SingularAttribute<Payslip, Integer> year;
	public static volatile SingularAttribute<Payslip, Company> company;
	public static volatile SingularAttribute<Payslip, Employee> employee;
	public static volatile SingularAttribute<Payslip, MonthMaster> monthMaster;
	public static volatile SingularAttribute<Payslip, PayslipFrequency> payslipFrequency;
}
