package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2012-10-05T14:46:33.953+0530")
@StaticMetamodel(EntityListView.class)
public class EntityListView_ extends BaseEntity_ {
	public static volatile SingularAttribute<EntityListView, Long> entityListViewId;
	public static volatile SingularAttribute<EntityListView, Integer> recordsPerPage;
	public static volatile SingularAttribute<EntityListView, String> viewName;
	public static volatile SingularAttribute<EntityListView, Company> company;
	public static volatile SingularAttribute<EntityListView, EntityMaster> entityMaster;
	public static volatile SetAttribute<EntityListView, EntityListViewField> entityListViewFields;
}
