package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-05-18T12:25:02.429+0530")
@StaticMetamodel(DataDictionary.class)
public class DataDictionary_ extends BaseEntity_ {
	public static volatile SingularAttribute<DataDictionary, Long> dataDictionaryId;
	public static volatile SingularAttribute<DataDictionary, String> columnName;
	public static volatile SingularAttribute<DataDictionary, String> dataDictName;
	public static volatile SingularAttribute<DataDictionary, String> description;
	public static volatile SingularAttribute<DataDictionary, String> fieldType;
	public static volatile SingularAttribute<DataDictionary, Long> formID;
	public static volatile SingularAttribute<DataDictionary, Boolean> importable;
	public static volatile SingularAttribute<DataDictionary, String> label;
	public static volatile SingularAttribute<DataDictionary, String> tableName;
	public static volatile SetAttribute<DataDictionary, CompanyDocumentShortList> companyDocumentShortLists;
	public static volatile SetAttribute<DataDictionary, CompanyEmployeeShortList> companyEmployeeShortLists;
	public static volatile SingularAttribute<DataDictionary, Company> company;
	public static volatile SingularAttribute<DataDictionary, EntityMaster> entityMaster;
	public static volatile SetAttribute<DataDictionary, DynamicFormFieldRefValue> dynamicFormFieldRefValues;
	public static volatile SetAttribute<DataDictionary, EmpDataExportTemplateField> empDataExportTemplateFields;
	public static volatile SetAttribute<DataDictionary, EmpDataExportTemplateFilter> empDataExportTemplateFilters;
	public static volatile SetAttribute<DataDictionary, EmpDataImportTemplateField> empDataImportTemplateFields;
	public static volatile SetAttribute<DataDictionary, EntityListViewField> entityListViewFields;
	public static volatile SetAttribute<DataDictionary, MultiLingualData> multiLingualData;
	public static volatile SetAttribute<DataDictionary, LeaveSchemeTypeShortList> leaveSchemeTypeShortLists;
	public static volatile SetAttribute<DataDictionary, ClaimTemplateItemShortlist> claimTemplateItemShortlists;
	public static volatile SingularAttribute<DataDictionary, String> dataType;
}
