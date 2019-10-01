package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2012-04-21T12:27:09.878+0530")
@StaticMetamodel(ReportMaster.class)
public class ReportMaster_ {
	public static volatile SingularAttribute<ReportMaster, Long> reportId;
	public static volatile SingularAttribute<ReportMaster, String> reportDesc;
	public static volatile SingularAttribute<ReportMaster, String> reportName;
	public static volatile SingularAttribute<ReportMaster, Company> company;
	public static volatile SingularAttribute<ReportMaster, ReportOutputFormatMapping> reportOutputFormatMapping;
	public static volatile SetAttribute<ReportMaster, ReportOutputFormatMapping> reportOutputFormatMappings;
}
