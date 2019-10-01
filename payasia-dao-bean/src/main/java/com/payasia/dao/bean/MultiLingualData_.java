package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2012-10-08T15:52:35.592+0530")
@StaticMetamodel(MultiLingualData.class)
public class MultiLingualData_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<MultiLingualData, MultiLingualDataPK> id;
	public static volatile SingularAttribute<MultiLingualData, String> label;
	public static volatile SingularAttribute<MultiLingualData, DataDictionary> dataDictionary;
	public static volatile SingularAttribute<MultiLingualData, LanguageMaster> languageMaster;
}
