package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2018-07-30T12:55:30.740+0530")
@StaticMetamodel(HRISPreference.class)
public class HRISPreference_ extends BaseEntity_ {
	public static volatile SingularAttribute<HRISPreference, Long> hrisPreferenceId;
	public static volatile SingularAttribute<HRISPreference, Company> company;
	public static volatile SingularAttribute<HRISPreference, Boolean> saveHrLetterInDocumentCenter;
	public static volatile SingularAttribute<HRISPreference, Boolean> enableEmployeeChangeWorkflow;
	public static volatile SingularAttribute<HRISPreference, Boolean> allowEmployeeUploadDoc;
	public static volatile SingularAttribute<HRISPreference, Boolean> passwordProtect;
	public static volatile SingularAttribute<HRISPreference, Boolean> hideGetPassword;
	public static volatile SingularAttribute<HRISPreference, Boolean> clientAdminEditDeleteEmployee;
	public static volatile SingularAttribute<HRISPreference, Boolean> useSystemMailAsFromAddress;
	public static volatile SingularAttribute<HRISPreference, Boolean> showPasswordAsPlainText;
	public static volatile SingularAttribute<HRISPreference, Boolean> UseEmailAndEmployeeNumberForLogin;
	public static volatile SingularAttribute<HRISPreference, Boolean> sendPayslipReleaseMail;
	public static volatile SingularAttribute<HRISPreference, String> discussionBoardDefaultEmailTo;
	public static volatile SingularAttribute<HRISPreference, String> discussionBoardDefaultEmailCC;
	public static volatile SingularAttribute<HRISPreference, String> discussionBoardPayAsiaUsers;
	public static volatile SingularAttribute<HRISPreference, Boolean> enableVisibility;
	public static volatile SingularAttribute<HRISPreference, DataDictionary> externalId;
}
