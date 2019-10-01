package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-02-11T17:40:45.177+0530")
@StaticMetamodel(OTBatchMaster.class)
public class OTBatchMaster_ {
	public static volatile SingularAttribute<OTBatchMaster, Long> OTBatchId;
	public static volatile SingularAttribute<OTBatchMaster, Timestamp> endDate;
	public static volatile SingularAttribute<OTBatchMaster, String> OTBatchDesc;
	public static volatile SingularAttribute<OTBatchMaster, Timestamp> startDate;
	public static volatile SingularAttribute<OTBatchMaster, Company> company;
}
