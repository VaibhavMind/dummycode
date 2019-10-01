package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2018-07-30T12:55:30.515+0530")
@StaticMetamodel(ClaimPreference.class)
public class ClaimPreference_ extends BaseEntity_ {
	public static volatile SingularAttribute<ClaimPreference, Long> claimPreferenceId;
	public static volatile SingularAttribute<ClaimPreference, Company> company;
	public static volatile SingularAttribute<ClaimPreference, Timestamp> startDayMonth;
	public static volatile SingularAttribute<ClaimPreference, Timestamp> endDayMonth;
	public static volatile SingularAttribute<ClaimPreference, Boolean> payAsiaAdminCanApprove;
	public static volatile SingularAttribute<ClaimPreference, Boolean> useSystemMailAsFromAddress;
	public static volatile SingularAttribute<ClaimPreference, Boolean> attachmentForMail;
	public static volatile SingularAttribute<ClaimPreference, String> claimItemNameSortOrder;
	public static volatile SingularAttribute<ClaimPreference, String> claimItemDateSortOrder;
	public static volatile SingularAttribute<ClaimPreference, Boolean> defaultEmailCC;
	public static volatile SingularAttribute<ClaimPreference, Boolean> adminCanAmendDataBeforeApproval;
	public static volatile SingularAttribute<ClaimPreference, Boolean> showPaidStatusForClaimBatch;
	public static volatile SingularAttribute<ClaimPreference, String> gridCreatedDateSortOrder;
	public static volatile SingularAttribute<ClaimPreference, String> gridClaimNumberSortOrder;
	public static volatile SingularAttribute<ClaimPreference, DataDictionary> pwdForAttachmentMail;
	public static volatile SingularAttribute<ClaimPreference, Boolean> showMonthlyConsFinReportGroupingByEmp;
	public static volatile SingularAttribute<ClaimPreference, DataDictionary> bankNameDictionary;
	public static volatile SingularAttribute<ClaimPreference, DataDictionary> bankAccountNumDictionary;
	public static volatile SingularAttribute<ClaimPreference, DataDictionary> bankAccountNameDictionary;
	public static volatile SingularAttribute<ClaimPreference, Boolean> additionalBalanceFrom;
}
