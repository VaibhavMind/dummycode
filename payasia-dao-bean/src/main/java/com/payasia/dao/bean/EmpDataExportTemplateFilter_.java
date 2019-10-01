package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2012-10-05T13:01:13.547+0530")
@StaticMetamodel(EmpDataExportTemplateFilter.class)
public class EmpDataExportTemplateFilter_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<EmpDataExportTemplateFilter, Long> exportFilterId;
	public static volatile SingularAttribute<EmpDataExportTemplateFilter, String> closeBracket;
	public static volatile SingularAttribute<EmpDataExportTemplateFilter, String> equalityOperator;
	public static volatile SingularAttribute<EmpDataExportTemplateFilter, String> logicalOperator;
	public static volatile SingularAttribute<EmpDataExportTemplateFilter, String> openBracket;
	public static volatile SingularAttribute<EmpDataExportTemplateFilter, String> value;
	public static volatile SingularAttribute<EmpDataExportTemplateFilter, DataDictionary> dataDictionary;
	public static volatile SingularAttribute<EmpDataExportTemplateFilter, EmpDataExportTemplate> empDataExportTemplate;
}
