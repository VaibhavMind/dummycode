package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2012-04-21T12:27:09.861+0530")
@StaticMetamodel(PayslipFrequency.class)
public class PayslipFrequency_ {
	public static volatile SingularAttribute<PayslipFrequency, Long> payslipFrequencyID;
	public static volatile SingularAttribute<PayslipFrequency, String> frequency;
	public static volatile SingularAttribute<PayslipFrequency, String> frequency_Desc;
	public static volatile SetAttribute<PayslipFrequency, Company> companies;
}
