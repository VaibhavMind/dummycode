package com.payasia.dao.bean;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-06-07T14:28:37.244+0530")
@StaticMetamodel(ClaimApplicationItemWorkflow.class)
public class ClaimApplicationItemWorkflow_ extends CompanyUpdatedBaseEntity_ {
	public static volatile SingularAttribute<ClaimApplicationItemWorkflow, Long> claimApplicationItemWorkflowId;
	public static volatile SingularAttribute<ClaimApplicationItemWorkflow, BigDecimal> overriddenAmount;
	public static volatile SingularAttribute<ClaimApplicationItemWorkflow, BigDecimal> overriddenTaxAmount;
	public static volatile SingularAttribute<ClaimApplicationItemWorkflow, Timestamp> createdDate;
	public static volatile SingularAttribute<ClaimApplicationItemWorkflow, String> remarks;
	public static volatile SingularAttribute<ClaimApplicationItemWorkflow, ClaimApplicationItem> claimApplicationItem;
	public static volatile SingularAttribute<ClaimApplicationItemWorkflow, Employee> createdBy;
	public static volatile SingularAttribute<ClaimApplicationItemWorkflow, AppCodeMaster> claimItemWorkflowAction;
	public static volatile SingularAttribute<ClaimApplicationItemWorkflow, AppCodeMaster> claimItemWorkflowStatus;
}
