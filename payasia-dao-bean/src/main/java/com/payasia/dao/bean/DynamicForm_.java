package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2012-09-20T20:23:23.137+0530")
@StaticMetamodel(DynamicForm.class)
public class DynamicForm_ extends BaseEntity_ {
	public static volatile SingularAttribute<DynamicForm, DynamicFormPK> id;
	public static volatile SingularAttribute<DynamicForm, Boolean> active;
	public static volatile SingularAttribute<DynamicForm, String> metaData;
	public static volatile SingularAttribute<DynamicForm, String> tabName;
	public static volatile SingularAttribute<DynamicForm, Integer> effectivePart;
	public static volatile SingularAttribute<DynamicForm, Integer> effectiveYear;
	public static volatile SingularAttribute<DynamicForm, MonthMaster> monthMaster;
	public static volatile SingularAttribute<DynamicForm, Company> company;
	public static volatile SingularAttribute<DynamicForm, EntityMaster> entityMaster;
}
