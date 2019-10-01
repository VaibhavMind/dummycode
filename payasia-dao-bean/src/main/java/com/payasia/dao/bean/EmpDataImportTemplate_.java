package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2012-09-26T13:34:18.667+0530")
@StaticMetamodel(EmpDataImportTemplate.class)
public class EmpDataImportTemplate_ extends BaseEntity_ {
	public static volatile SingularAttribute<EmpDataImportTemplate, Long> importTemplateId;
	public static volatile SingularAttribute<EmpDataImportTemplate, Integer> custom_Table_Position;
	public static volatile SingularAttribute<EmpDataImportTemplate, String> description;
	public static volatile SingularAttribute<EmpDataImportTemplate, Long> formID;
	public static volatile SingularAttribute<EmpDataImportTemplate, String> templateName;
	public static volatile SingularAttribute<EmpDataImportTemplate, String> transaction_Type;
	public static volatile SingularAttribute<EmpDataImportTemplate, String> upload_Type;
	public static volatile SingularAttribute<EmpDataImportTemplate, Company> company;
	public static volatile SingularAttribute<EmpDataImportTemplate, EntityMaster> entityMaster;
	public static volatile SetAttribute<EmpDataImportTemplate, EmpDataImportTemplateField> empDataImportTemplateFields;
}
