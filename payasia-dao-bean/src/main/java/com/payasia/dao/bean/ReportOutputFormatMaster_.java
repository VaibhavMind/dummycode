package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2012-04-21T12:27:09.888+0530")
@StaticMetamodel(ReportOutputFormatMaster.class)
public class ReportOutputFormatMaster_ {
	public static volatile SingularAttribute<ReportOutputFormatMaster, Long> reportOutputFormatID;
	public static volatile SingularAttribute<ReportOutputFormatMaster, String> reportOutputFormatDesc;
	public static volatile SingularAttribute<ReportOutputFormatMaster, String> reportOutputFormatType;
	public static volatile SetAttribute<ReportOutputFormatMaster, ReportOutputFormatMapping> reportOutputFormatMappings;
}
