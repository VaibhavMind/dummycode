package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2015-04-06T11:53:50.517+0530")
@StaticMetamodel(LundinAFE.class)
public class LundinAFE_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<LundinAFE, Long> afeId;
	public static volatile SingularAttribute<LundinAFE, Timestamp> effectiveDate;
	public static volatile SingularAttribute<LundinAFE, Boolean> status;
	public static volatile SingularAttribute<LundinAFE, String> afeCode;
	public static volatile SingularAttribute<LundinAFE, String> afeName;
	public static volatile ListAttribute<LundinAFE, LundinBlock> lundinBlocks;
}
