package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-02-11T17:40:44.990+0530")
@StaticMetamodel(LeaveBatchMaster.class)
public class LeaveBatchMaster_ {
	public static volatile SingularAttribute<LeaveBatchMaster, Long> leaveBatchId;
	public static volatile SingularAttribute<LeaveBatchMaster, Timestamp> endDate;
	public static volatile SingularAttribute<LeaveBatchMaster, String> leaveBatchDesc;
	public static volatile SingularAttribute<LeaveBatchMaster, Timestamp> startDate;
	public static volatile SingularAttribute<LeaveBatchMaster, Company> company;
}
