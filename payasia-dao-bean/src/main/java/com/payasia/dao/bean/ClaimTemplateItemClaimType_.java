package com.payasia.dao.bean;

import java.math.BigDecimal;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-06-07T14:40:19.458+0530")
@StaticMetamodel(ClaimTemplateItemClaimType.class)
public class ClaimTemplateItemClaimType_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<ClaimTemplateItemClaimType, Long> claimTypeId;
	public static volatile SingularAttribute<ClaimTemplateItemClaimType, ClaimTemplateItem> claimTemplateItem;
	public static volatile SingularAttribute<ClaimTemplateItemClaimType, AppCodeMaster> claimType;
	public static volatile SingularAttribute<ClaimTemplateItemClaimType, Integer> minLimit;
	public static volatile SingularAttribute<ClaimTemplateItemClaimType, Integer> maxLimit;
	public static volatile SingularAttribute<ClaimTemplateItemClaimType, Integer> defaultUnit;
	public static volatile SingularAttribute<ClaimTemplateItemClaimType, Boolean> showDefaultUnit;
	public static volatile SingularAttribute<ClaimTemplateItemClaimType, BigDecimal> defaultUnitPrice;
	public static volatile SingularAttribute<ClaimTemplateItemClaimType, Boolean> allowChangeDefaultPrice;
	public static volatile SingularAttribute<ClaimTemplateItemClaimType, Integer> receiptAmtPercentApplicable;
	public static volatile SingularAttribute<ClaimTemplateItemClaimType, Boolean> allowChangeForexRate;
	public static volatile SingularAttribute<ClaimTemplateItemClaimType, Boolean> allowOverrideConvertedAmt;
}
