package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2018-01-01T18:38:16.273+0530")
@StaticMetamodel(ClaimItemEntertainment.class)
public class ClaimItemEntertainment_ extends BaseEntity_ {
	public static volatile SingularAttribute<ClaimItemEntertainment, Long> ClaimItemEntertainmentId;
	public static volatile SingularAttribute<ClaimItemEntertainment, Company> company;
	public static volatile SingularAttribute<ClaimItemEntertainment, Employee> employee;
	public static volatile SingularAttribute<ClaimItemEntertainment, ClaimTemplateItem> claimTemplateItem;
	public static volatile SingularAttribute<ClaimItemEntertainment, Timestamp> startDate;
	public static volatile SingularAttribute<ClaimItemEntertainment, Timestamp> endDate;
	public static volatile SingularAttribute<ClaimItemEntertainment, String> Amount;
	public static volatile SingularAttribute<ClaimItemEntertainment, String> Reason;
	public static volatile SingularAttribute<ClaimItemEntertainment, Boolean> forfeitAtEndDate;
}
