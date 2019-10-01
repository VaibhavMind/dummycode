package com.payasia.dao.bean;

import java.sql.Timestamp;
import java.util.Date;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2017-02-15T16:29:35.045+0530")
@StaticMetamodel(ClaimBatchMaster.class)
public class ClaimBatchMaster_ extends BaseEntity_ {
	public static volatile SingularAttribute<ClaimBatchMaster, Long> claimBatchID;
	public static volatile SingularAttribute<ClaimBatchMaster, String> claimBatchDesc;
	public static volatile SingularAttribute<ClaimBatchMaster, Timestamp> endDate;
	public static volatile SingularAttribute<ClaimBatchMaster, Timestamp> startDate;
	public static volatile SingularAttribute<ClaimBatchMaster, Boolean> paid;
	public static volatile SingularAttribute<ClaimBatchMaster, Date> paidDate;
	public static volatile SingularAttribute<ClaimBatchMaster, Company> company;
}
