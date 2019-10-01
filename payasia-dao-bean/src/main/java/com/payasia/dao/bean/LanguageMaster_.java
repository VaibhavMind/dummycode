package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2012-07-17T15:53:42.927+0530")
@StaticMetamodel(LanguageMaster.class)
public class LanguageMaster_ {
	public static volatile SingularAttribute<LanguageMaster, Long> languageId;
	public static volatile SingularAttribute<LanguageMaster, Boolean> defaultLang;
	public static volatile SingularAttribute<LanguageMaster, String> language;
	public static volatile SingularAttribute<LanguageMaster, String> languageCode;
	public static volatile SingularAttribute<LanguageMaster, String> languageDesc;
	public static volatile SetAttribute<LanguageMaster, MultiLingualData> multiLingualData;
	public static volatile SingularAttribute<LanguageMaster, Boolean> languageActive;
}
