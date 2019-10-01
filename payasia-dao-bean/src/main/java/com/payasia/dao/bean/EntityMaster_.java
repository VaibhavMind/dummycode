package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2014-07-29T11:13:40.014+0530")
@StaticMetamodel(EntityMaster.class)
public class EntityMaster_ {
	public static volatile SingularAttribute<EntityMaster, Long> entityId;
	public static volatile SingularAttribute<EntityMaster, String> entityDesc;
	public static volatile SingularAttribute<EntityMaster, String> entityName;
	public static volatile SingularAttribute<EntityMaster, String> labelKey;
	public static volatile SetAttribute<EntityMaster, DataDictionary> dataDictionaries;
	public static volatile SetAttribute<EntityMaster, DataImportHistory> dataImportHistories;
	public static volatile SetAttribute<EntityMaster, DynamicForm> dynamicForms;
	public static volatile SetAttribute<EntityMaster, EmpDataExportTemplate> empDataExportTemplates;
	public static volatile SetAttribute<EntityMaster, EmpDataImportTemplate> empDataImportTemplates;
	public static volatile SetAttribute<EntityMaster, EntityListView> entityListViews;
	public static volatile SetAttribute<EntityMaster, EntityTableMapping> entityTableMappings;
	public static volatile SetAttribute<EntityMaster, EmpDataExportTemplateField> empDataExportTemplateFields;
}
