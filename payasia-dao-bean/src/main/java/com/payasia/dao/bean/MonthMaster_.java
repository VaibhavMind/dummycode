package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2015-05-01T11:15:18.885+0530")
@StaticMetamodel(MonthMaster.class)
public class MonthMaster_ {
	public static volatile SingularAttribute<MonthMaster, Long> monthId;
	public static volatile SingularAttribute<MonthMaster, String> monthAbbr;
	public static volatile SingularAttribute<MonthMaster, String> monthName;
	public static volatile SingularAttribute<MonthMaster, String> labelKey;
	public static volatile SetAttribute<MonthMaster, DynamicForm> dynamicForms;
	public static volatile SetAttribute<MonthMaster, FinancialYearMaster> financialYearMasters1;
	public static volatile SetAttribute<MonthMaster, FinancialYearMaster> financialYearMasters2;
	public static volatile SetAttribute<MonthMaster, Payslip> payslips;
	public static volatile SetAttribute<MonthMaster, PayslipUploadHistory> payslipUploadHistories;
	public static volatile SetAttribute<MonthMaster, CompanyPayslipRelease> companyPayslipReleases;
}
