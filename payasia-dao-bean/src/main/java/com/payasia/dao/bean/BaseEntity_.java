package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2012-09-20T20:09:13.924+0530")
@StaticMetamodel(BaseEntity.class)
public class BaseEntity_ {
	public static volatile SingularAttribute<BaseEntity, Timestamp> createdDate;
	public static volatile SingularAttribute<BaseEntity, String> createdBy;
	public static volatile SingularAttribute<BaseEntity, Timestamp> updatedDate;
	public static volatile SingularAttribute<BaseEntity, String> updatedBy;
}
