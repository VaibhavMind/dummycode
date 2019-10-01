package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2012-07-12T19:14:34.772+0530")
@StaticMetamodel(OTStatusMaster.class)
public class OTStatusMaster_ {
	public static volatile SingularAttribute<OTStatusMaster, Long> otStatusId;
	public static volatile SingularAttribute<OTStatusMaster, String> otStatusDesc;
	public static volatile SingularAttribute<OTStatusMaster, String> otStatusName;
	public static volatile SetAttribute<OTStatusMaster, OTApplication> otApplications;
	public static volatile SetAttribute<OTStatusMaster, OTApplicationWorkflow> otApplicationWorkflows;
}
