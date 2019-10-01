package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2012-10-30T16:34:31.683+0530")
@StaticMetamodel(EmpDataExportTemplateField.class)
public class EmpDataExportTemplateField_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<EmpDataExportTemplateField, Long> exportFieldId;
	public static volatile SingularAttribute<EmpDataExportTemplateField, String> excelFieldName;
	public static volatile SingularAttribute<EmpDataExportTemplateField, String> dataDictName;
	public static volatile SingularAttribute<EmpDataExportTemplateField, DataDictionary> dataDictionary;
	public static volatile SingularAttribute<EmpDataExportTemplateField, EmpDataExportTemplate> empDataExportTemplate;
	public static volatile SingularAttribute<EmpDataExportTemplateField, EntityMaster> entityMaster;
}
