package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2012-10-05T14:49:47.316+0530")
@StaticMetamodel(ReportOutputFormatMapping.class)
public class ReportOutputFormatMapping_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<ReportOutputFormatMapping, Long> reportOutputFormatMappingID;
	public static volatile SetAttribute<ReportOutputFormatMapping, ReportMaster> reportMasters;
	public static volatile SingularAttribute<ReportOutputFormatMapping, ReportMaster> reportMaster;
	public static volatile SingularAttribute<ReportOutputFormatMapping, ReportOutputFormatMaster> reportOutputFormatMaster;
}
