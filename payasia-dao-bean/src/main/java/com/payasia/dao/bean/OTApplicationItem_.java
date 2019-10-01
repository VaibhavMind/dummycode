package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2012-07-12T19:14:34.710+0530")
@StaticMetamodel(OTApplicationItem.class)
public class OTApplicationItem_ {
	public static volatile SingularAttribute<OTApplicationItem, Long> otApplicationItemId;
	public static volatile SingularAttribute<OTApplicationItem, Timestamp> otDate;
	public static volatile SingularAttribute<OTApplicationItem, String> remarks;
	public static volatile SingularAttribute<OTApplicationItem, DayTypeMaster> dayTypeMaster;
	public static volatile SingularAttribute<OTApplicationItem, OTApplication> otApplication;
	public static volatile SetAttribute<OTApplicationItem, OTApplicationItemDetail> otApplicationItemDetails;
	public static volatile SetAttribute<OTApplicationItem, OTApplicationItemWorkflow> otApplicationItemWorkflows;
}
