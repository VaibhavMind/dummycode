package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2017-02-08T19:02:17.027+0530")
@StaticMetamodel(ClaimStatusMaster.class)
public class ClaimStatusMaster_ {
	public static volatile SingularAttribute<ClaimStatusMaster, Long> claimStatusId;
	public static volatile SingularAttribute<ClaimStatusMaster, String> claimStatusDesc;
	public static volatile SingularAttribute<ClaimStatusMaster, String> claimStatusName;
	public static volatile SingularAttribute<ClaimStatusMaster, String> labelKey;
	public static volatile SetAttribute<ClaimStatusMaster, ClaimApplication> claimApplications;
	public static volatile SetAttribute<ClaimStatusMaster, ClaimApplicationWorkflow> claimApplicationWorkflows;
}
