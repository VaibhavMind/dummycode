package com.payasia.dao.bean;

import java.math.BigDecimal;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2015-05-01T11:15:18.697+0530")
@StaticMetamodel(ClaimTemplate.class)
public class ClaimTemplate_ extends BaseEntity_ {
	public static volatile SingularAttribute<ClaimTemplate, Long> claimTemplateId;
	public static volatile SingularAttribute<ClaimTemplate, String> templateName;
	public static volatile SingularAttribute<ClaimTemplate, Boolean> visibility;
	public static volatile SingularAttribute<ClaimTemplate, BigDecimal> entitlementPerClaim;
	public static volatile SingularAttribute<ClaimTemplate, BigDecimal> entitlementPerMonth;
	public static volatile SingularAttribute<ClaimTemplate, BigDecimal> entitlementPerYear;
	public static volatile SingularAttribute<ClaimTemplate, Integer> allowedTimesValue;
	public static volatile SingularAttribute<ClaimTemplate, AppCodeMaster> frontEndViewMode;
	public static volatile SingularAttribute<ClaimTemplate, Boolean> frontEndApplicationMode;
	public static volatile SingularAttribute<ClaimTemplate, AppCodeMaster> backEndViewMode;
	public static volatile SingularAttribute<ClaimTemplate, Boolean> backEndApplicationMode;
	public static volatile SingularAttribute<ClaimTemplate, Company> company;
	public static volatile SingularAttribute<ClaimTemplate, CurrencyMaster> defaultCurrency;
	public static volatile SetAttribute<ClaimTemplate, ClaimTemplateItem> claimTemplateItems;
	public static volatile SetAttribute<ClaimTemplate, ClaimTemplateWorkflow> claimTemplateWorkflows;
	public static volatile SetAttribute<ClaimTemplate, EmployeeClaimTemplate> employeeClaimTemplates;
	public static volatile SingularAttribute<ClaimTemplate, AppCodeMaster> allowedTimesField;
	public static volatile SingularAttribute<ClaimTemplate, String> defaultCC;
	public static volatile SingularAttribute<ClaimTemplate, Boolean> sendMailToDefaultCC;
	public static volatile SingularAttribute<ClaimTemplate, Integer> cutOffValue;
	public static volatile SingularAttribute<ClaimTemplate, AppCodeMaster> prorationMethod;
	public static volatile SingularAttribute<ClaimTemplate, Boolean> AllowIfAtLeastOneAttachment;
	public static volatile SingularAttribute<ClaimTemplate, Boolean> proration;
	public static volatile SingularAttribute<ClaimTemplate, AppCodeMaster> prorationBasedOn;
	public static volatile SingularAttribute<ClaimTemplate, Boolean> claimReviewersBasedOnClaimAmount;
	public static volatile SetAttribute<ClaimTemplate, ClaimAmountReviewerTemplate> claimAmountReviewerTemplates;
	public static volatile SingularAttribute<ClaimTemplate, ClaimTemplate> considerAdditionalBalanceFrom;
}
