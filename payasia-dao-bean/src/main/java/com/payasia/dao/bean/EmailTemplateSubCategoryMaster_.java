package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2014-07-29T11:13:39.824+0530")
@StaticMetamodel(EmailTemplateSubCategoryMaster.class)
public class EmailTemplateSubCategoryMaster_ extends BaseEntity_ {
	public static volatile SingularAttribute<EmailTemplateSubCategoryMaster, Long> emailTemplateSubCategoryId;
	public static volatile SingularAttribute<EmailTemplateSubCategoryMaster, String> subCategoryDesc;
	public static volatile SingularAttribute<EmailTemplateSubCategoryMaster, String> subCategoryName;
	public static volatile SingularAttribute<EmailTemplateSubCategoryMaster, String> labelKey;
	public static volatile SetAttribute<EmailTemplateSubCategoryMaster, EmailTemplate> emailTemplates;
	public static volatile SingularAttribute<EmailTemplateSubCategoryMaster, EmailTemplateCategoryMaster> emailTemplateCategoryMaster;
}
