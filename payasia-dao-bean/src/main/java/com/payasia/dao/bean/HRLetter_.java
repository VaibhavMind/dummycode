package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2012-09-26T13:46:29.593+0530")
@StaticMetamodel(HRLetter.class)
public class HRLetter_ extends BaseEntity_ {
	public static volatile SingularAttribute<HRLetter, Long> hrLetterId;
	public static volatile SingularAttribute<HRLetter, Boolean> active;
	public static volatile SingularAttribute<HRLetter, String> body;
	public static volatile SingularAttribute<HRLetter, String> letterDesc;
	public static volatile SingularAttribute<HRLetter, String> letterName;
	public static volatile SingularAttribute<HRLetter, String> subject;
	public static volatile SingularAttribute<HRLetter, Company> company;
}
