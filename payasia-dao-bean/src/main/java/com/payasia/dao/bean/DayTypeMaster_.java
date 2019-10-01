package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2012-07-12T19:14:34.509+0530")
@StaticMetamodel(DayTypeMaster.class)
public class DayTypeMaster_ {
	public static volatile SingularAttribute<DayTypeMaster, Long> dayTypeId;
	public static volatile SingularAttribute<DayTypeMaster, String> dayType;
	public static volatile SingularAttribute<DayTypeMaster, String> dayTypeDesc;
	public static volatile SetAttribute<DayTypeMaster, OTApplicationItem> otApplicationItems;
	public static volatile SetAttribute<DayTypeMaster, OTApplicationItemWorkflow> otApplicationItemWorkflows;
}
