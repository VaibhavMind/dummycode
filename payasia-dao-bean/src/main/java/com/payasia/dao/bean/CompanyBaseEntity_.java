package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2012-10-05T12:55:30.500+0530")
@StaticMetamodel(CompanyBaseEntity.class)
public class CompanyBaseEntity_ {
	public static volatile SingularAttribute<CompanyBaseEntity, Timestamp> createdDate;
	public static volatile SingularAttribute<CompanyBaseEntity, String> createdBy;
	public static volatile SingularAttribute<CompanyBaseEntity, Timestamp> updatedDate;
	public static volatile SingularAttribute<CompanyBaseEntity, String> updatedBy;
	public static volatile SingularAttribute<CompanyBaseEntity, Long> companyId;
}
