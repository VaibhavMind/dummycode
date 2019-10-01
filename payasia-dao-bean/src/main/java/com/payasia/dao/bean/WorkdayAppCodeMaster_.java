package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2018-10-30T13:05:08.865+0530")
@StaticMetamodel(WorkdayAppCodeMaster.class)
public class WorkdayAppCodeMaster_ {
	public static volatile SingularAttribute<WorkdayAppCodeMaster, Long> workdayAppCodeId;
	public static volatile SingularAttribute<WorkdayAppCodeMaster, String> category;
	public static volatile SingularAttribute<WorkdayAppCodeMaster, String> codeDesc;
	public static volatile SingularAttribute<WorkdayAppCodeMaster, String> codeValue;
	public static volatile SingularAttribute<WorkdayAppCodeMaster, CountryMaster> country;
	public static volatile SingularAttribute<WorkdayAppCodeMaster, String> type;
}
