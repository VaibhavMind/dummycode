package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-04-09T18:16:26.837+0530")
@StaticMetamodel(LeaveSchemeTypeCustomField.class)
public class LeaveSchemeTypeCustomField_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<LeaveSchemeTypeCustomField, Long> customFieldId;
	public static volatile SingularAttribute<LeaveSchemeTypeCustomField, String> fieldName;
	public static volatile SingularAttribute<LeaveSchemeTypeCustomField, Boolean> mandatory;
	public static volatile SingularAttribute<LeaveSchemeTypeCustomField, LeaveSchemeTypeAvailingLeave> leaveSchemeTypeAvailingLeave;
	public static volatile SetAttribute<LeaveSchemeTypeCustomField, LeaveApplicationCustomField> leaveApplicationCustomFields;
}
