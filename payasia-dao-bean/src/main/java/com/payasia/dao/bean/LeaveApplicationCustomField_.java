package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-04-09T18:32:44.314+0530")
@StaticMetamodel(LeaveApplicationCustomField.class)
public class LeaveApplicationCustomField_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<LeaveApplicationCustomField, Long> leaveApplicationCustomFieldId;
	public static volatile SingularAttribute<LeaveApplicationCustomField, String> Value;
	public static volatile SingularAttribute<LeaveApplicationCustomField, LeaveApplication> leaveApplication;
	public static volatile SingularAttribute<LeaveApplicationCustomField, LeaveSchemeTypeCustomField> leaveSchemeTypeCustomField;
}
