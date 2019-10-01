package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2012-10-05T13:01:33.279+0530")
@StaticMetamodel(EmpDataImportTemplateField.class)
public class EmpDataImportTemplateField_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<EmpDataImportTemplateField, Long> importFieldId;
	public static volatile SingularAttribute<EmpDataImportTemplateField, String> dataType;
	public static volatile SingularAttribute<EmpDataImportTemplateField, String> defaultValue;
	public static volatile SingularAttribute<EmpDataImportTemplateField, String> description;
	public static volatile SingularAttribute<EmpDataImportTemplateField, String> excelFieldName;
	public static volatile SingularAttribute<EmpDataImportTemplateField, Integer> length;
	public static volatile SingularAttribute<EmpDataImportTemplateField, Boolean> required;
	public static volatile SingularAttribute<EmpDataImportTemplateField, String> sampleData;
	public static volatile SingularAttribute<EmpDataImportTemplateField, DataDictionary> dataDictionary;
	public static volatile SingularAttribute<EmpDataImportTemplateField, EmpDataImportTemplate> empDataImportTemplate;
}
