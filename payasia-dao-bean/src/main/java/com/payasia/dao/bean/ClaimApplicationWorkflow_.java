package com.payasia.dao.bean;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-02-13T16:01:20.137+0530")
@StaticMetamodel(ClaimApplicationWorkflow.class)
public class ClaimApplicationWorkflow_ {
	public static volatile SingularAttribute<ClaimApplicationWorkflow, Long> claimApplicationWorkflowId;
	public static volatile SingularAttribute<ClaimApplicationWorkflow, Timestamp> createdDate;
	public static volatile SingularAttribute<ClaimApplicationWorkflow, String> emailCC;
	public static volatile SingularAttribute<ClaimApplicationWorkflow, String> forwardTo;
	public static volatile SingularAttribute<ClaimApplicationWorkflow, String> remarks;
	public static volatile SingularAttribute<ClaimApplicationWorkflow, BigDecimal> totalAmount;
	public static volatile SingularAttribute<ClaimApplicationWorkflow, ClaimApplication> claimApplication;
	public static volatile SingularAttribute<ClaimApplicationWorkflow, ClaimStatusMaster> claimStatusMaster;
	public static volatile SingularAttribute<ClaimApplicationWorkflow, Employee> employee;
}
