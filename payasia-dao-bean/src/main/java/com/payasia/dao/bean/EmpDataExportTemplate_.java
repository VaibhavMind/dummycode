package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-06-14T18:06:45.846+0530")
@StaticMetamodel(EmpDataExportTemplate.class)
public class EmpDataExportTemplate_ extends BaseEntity_ {
	public static volatile SingularAttribute<EmpDataExportTemplate, Long> exportTemplateId;
	public static volatile SingularAttribute<EmpDataExportTemplate, String> category;
	public static volatile SingularAttribute<EmpDataExportTemplate, Integer> custom_Table_Position;
	public static volatile SingularAttribute<EmpDataExportTemplate, String> description;
	public static volatile SingularAttribute<EmpDataExportTemplate, Long> formID;
	public static volatile SingularAttribute<EmpDataExportTemplate, String> scope;
	public static volatile SingularAttribute<EmpDataExportTemplate, Boolean> includePrefix;
	public static volatile SingularAttribute<EmpDataExportTemplate, Boolean> includeSuffix;
	public static volatile SingularAttribute<EmpDataExportTemplate, Boolean> includeTemplateNameAsPrefix;
	public static volatile SingularAttribute<EmpDataExportTemplate, Boolean> includeTimestampAsSuffix;
	public static volatile SingularAttribute<EmpDataExportTemplate, String> prefix;
	public static volatile SingularAttribute<EmpDataExportTemplate, String> suffix;
	public static volatile SingularAttribute<EmpDataExportTemplate, String> templateName;
	public static volatile SingularAttribute<EmpDataExportTemplate, Boolean> multipleSection;
	public static volatile SingularAttribute<EmpDataExportTemplate, Company> company;
	public static volatile SingularAttribute<EmpDataExportTemplate, CompanyGroup> companyGroup;
	public static volatile SingularAttribute<EmpDataExportTemplate, EntityMaster> entityMaster;
	public static volatile SetAttribute<EmpDataExportTemplate, EmpDataExportTemplateField> empDataExportTemplateFields;
	public static volatile SetAttribute<EmpDataExportTemplate, EmpDataExportTemplateFilter> empDataExportTemplateFilters;
}
