package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2015-03-23T09:56:51.767+0530")
@StaticMetamodel(LundinBlock.class)
public class LundinBlock_ extends BaseEntity_ {
	public static volatile SingularAttribute<LundinBlock, Long> blockId;
	public static volatile SingularAttribute<LundinBlock, Company> company;
	public static volatile SingularAttribute<LundinBlock, Timestamp> effectiveDate;
	public static volatile SingularAttribute<LundinBlock, Boolean> status;
	public static volatile SingularAttribute<LundinBlock, String> blockCode;
	public static volatile SingularAttribute<LundinBlock, String> blockName;
	public static volatile SingularAttribute<LundinBlock, Boolean> effectiveAllocation;
	public static volatile ListAttribute<LundinBlock, LundinAFE> lundinAfes;
}
