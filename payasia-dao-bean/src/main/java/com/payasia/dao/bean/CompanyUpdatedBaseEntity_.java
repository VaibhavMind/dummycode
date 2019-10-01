package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-04-09T18:40:55.972+0530")
@StaticMetamodel(CompanyUpdatedBaseEntity.class)
public class CompanyUpdatedBaseEntity_ {
	public static volatile SingularAttribute<CompanyUpdatedBaseEntity, Timestamp> updatedDate;
	public static volatile SingularAttribute<CompanyUpdatedBaseEntity, String> updatedBy;
	public static volatile SingularAttribute<CompanyUpdatedBaseEntity, Long> companyId;
}
