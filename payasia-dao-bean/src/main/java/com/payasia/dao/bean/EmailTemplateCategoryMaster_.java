package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2014-07-29T11:13:39.789+0530")
@StaticMetamodel(EmailTemplateCategoryMaster.class)
public class EmailTemplateCategoryMaster_ extends BaseEntity_ {
	public static volatile SingularAttribute<EmailTemplateCategoryMaster, Long> emailTemplateCategoryId;
	public static volatile SingularAttribute<EmailTemplateCategoryMaster, String> categoryDesc;
	public static volatile SingularAttribute<EmailTemplateCategoryMaster, String> categoryName;
	public static volatile SingularAttribute<EmailTemplateCategoryMaster, String> labelKey;
	public static volatile SetAttribute<EmailTemplateCategoryMaster, EmailTemplateSubCategoryMaster> emailTemplateSubCategoryMasters;
}
