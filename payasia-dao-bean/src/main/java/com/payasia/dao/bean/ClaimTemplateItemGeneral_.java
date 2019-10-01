package com.payasia.dao.bean;

import java.math.BigDecimal;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2018-01-09T09:35:48.806+0530")
@StaticMetamodel(ClaimTemplateItemGeneral.class)
public class ClaimTemplateItemGeneral_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<ClaimTemplateItemGeneral, Long> generalId;
	public static volatile SingularAttribute<ClaimTemplateItemGeneral, Boolean> openToDependents;
	public static volatile SingularAttribute<ClaimTemplateItemGeneral, ClaimTemplateItem> claimTemplateItem;
	public static volatile SingularAttribute<ClaimTemplateItemGeneral, Boolean> receiptNoMandatory;
	public static volatile SingularAttribute<ClaimTemplateItemGeneral, Boolean> claimDateMandatory;
	public static volatile SingularAttribute<ClaimTemplateItemGeneral, Boolean> remarksMandatory;
	public static volatile SingularAttribute<ClaimTemplateItemGeneral, Boolean> attachmentMandatory;
	public static volatile SingularAttribute<ClaimTemplateItemGeneral, Boolean> allowOverrideTaxAmt;
	public static volatile SingularAttribute<ClaimTemplateItemGeneral, Integer> taxPercentage;
	public static volatile SingularAttribute<ClaimTemplateItemGeneral, Integer> claimsAllowedPerMonth;
	public static volatile SingularAttribute<ClaimTemplateItemGeneral, Integer> backdatedClaimDaysLimit;
	public static volatile SingularAttribute<ClaimTemplateItemGeneral, BigDecimal> entitlementPerDay;
	public static volatile SingularAttribute<ClaimTemplateItemGeneral, BigDecimal> entitlementPerMonth;
	public static volatile SingularAttribute<ClaimTemplateItemGeneral, BigDecimal> entitlementPerYear;
	public static volatile SingularAttribute<ClaimTemplateItemGeneral, Boolean> allowExceedEntitlement;
	public static volatile SingularAttribute<ClaimTemplateItemGeneral, String> remarks;
	public static volatile SingularAttribute<ClaimTemplateItemGeneral, String> helpText;
	public static volatile SingularAttribute<ClaimTemplateItemGeneral, Boolean> amountBeforeTaxVisible;
	public static volatile SingularAttribute<ClaimTemplateItemGeneral, Boolean> overrideReceiptAmountForQuantityBased;
}
