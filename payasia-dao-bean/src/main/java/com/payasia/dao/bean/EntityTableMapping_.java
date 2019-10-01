package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2012-04-21T12:27:09.759+0530")
@StaticMetamodel(EntityTableMapping.class)
public class EntityTableMapping_ {
	public static volatile SingularAttribute<EntityTableMapping, Long> entityTableMappingId;
	public static volatile SingularAttribute<EntityTableMapping, String> tableName;
	public static volatile SingularAttribute<EntityTableMapping, Integer> tableOrder;
	public static volatile SingularAttribute<EntityTableMapping, EntityMaster> entityMaster;
}
