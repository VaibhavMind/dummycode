package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2014-07-29T11:13:39.720+0530")
@StaticMetamodel(DocumentCategoryMaster.class)
public class DocumentCategoryMaster_ {
	public static volatile SingularAttribute<DocumentCategoryMaster, Long> documentCategoryId;
	public static volatile SingularAttribute<DocumentCategoryMaster, String> categoryDesc;
	public static volatile SingularAttribute<DocumentCategoryMaster, String> categoryName;
	public static volatile SingularAttribute<DocumentCategoryMaster, String> labelKey;
	public static volatile SetAttribute<DocumentCategoryMaster, CompanyDocument> companyDocuments;
}
