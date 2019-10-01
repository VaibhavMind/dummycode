package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-11-22T15:57:57.921+0530")
@StaticMetamodel(EmployeeDocumentHistory.class)
public class EmployeeDocumentHistory_ {
	public static volatile SingularAttribute<EmployeeDocumentHistory, Long> employeeDocumentHistoryId;
	public static volatile SingularAttribute<EmployeeDocumentHistory, DynamicFormTableRecord> dynamicFormTableRecord;
	public static volatile SingularAttribute<EmployeeDocumentHistory, String> fieldChanged;
	public static volatile SingularAttribute<EmployeeDocumentHistory, String> oldValue;
	public static volatile SingularAttribute<EmployeeDocumentHistory, String> newValue;
	public static volatile SingularAttribute<EmployeeDocumentHistory, Timestamp> changedDate;
	public static volatile SingularAttribute<EmployeeDocumentHistory, String> changedBy;
}
