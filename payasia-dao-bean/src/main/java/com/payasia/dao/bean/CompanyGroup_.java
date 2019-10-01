package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-02-11T18:26:35.938+0530")
@StaticMetamodel(CompanyGroup.class)
public class CompanyGroup_ extends BaseEntity_ {
	public static volatile SingularAttribute<CompanyGroup, Long> groupId;
	public static volatile SingularAttribute<CompanyGroup, String> groupCode;
	public static volatile SingularAttribute<CompanyGroup, String> groupDesc;
	public static volatile SingularAttribute<CompanyGroup, String> groupName;
	public static volatile SetAttribute<CompanyGroup, Company> companies;
	public static volatile SetAttribute<CompanyGroup, EmpDataExportTemplate> empDataExportTemplates;
	public static volatile SetAttribute<CompanyGroup, Announcement> announcements;
}
