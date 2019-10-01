package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2012-07-13T18:29:09.377+0530")
@StaticMetamodel(OTApplicationItemDetailWorkflow.class)
public class OTApplicationItemDetailWorkflow_ {
	public static volatile SingularAttribute<OTApplicationItemDetailWorkflow, Long> otApplicationItemDetailWorkflowId;
	public static volatile SingularAttribute<OTApplicationItemDetailWorkflow, String> otTemplateItemValue;
	public static volatile SingularAttribute<OTApplicationItemDetailWorkflow, OTApplicationItemWorkflow> otApplicationItemWorkflow;
	public static volatile SingularAttribute<OTApplicationItemDetailWorkflow, OTApplicationItemDetail> otApplicationItemDetail;
	public static volatile SingularAttribute<OTApplicationItemDetailWorkflow, OTTemplateItem> otTemplateItem;
}
