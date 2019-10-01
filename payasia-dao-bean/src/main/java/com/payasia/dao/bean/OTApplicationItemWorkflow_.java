package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2012-07-12T19:14:34.735+0530")
@StaticMetamodel(OTApplicationItemWorkflow.class)
public class OTApplicationItemWorkflow_ {
	public static volatile SingularAttribute<OTApplicationItemWorkflow, Long> otApplicationItemWorkflowId;
	public static volatile SingularAttribute<OTApplicationItemWorkflow, Timestamp> otDate;
	public static volatile SingularAttribute<OTApplicationItemWorkflow, String> remarks;
	public static volatile SingularAttribute<OTApplicationItemWorkflow, DayTypeMaster> dayTypeMaster;
	public static volatile SingularAttribute<OTApplicationItemWorkflow, OTApplicationItem> otApplicationItem;
	public static volatile SingularAttribute<OTApplicationItemWorkflow, OTApplicationWorkflow> otApplicationWorkflow;
}
