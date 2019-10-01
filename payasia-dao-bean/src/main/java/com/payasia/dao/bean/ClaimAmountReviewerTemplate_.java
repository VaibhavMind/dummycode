package com.payasia.dao.bean;

import java.math.BigDecimal;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2014-11-07T09:40:20.934+0530")
@StaticMetamodel(ClaimAmountReviewerTemplate.class)
public class ClaimAmountReviewerTemplate_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<ClaimAmountReviewerTemplate, Long> claimAmountReviewerTemplateId;
	public static volatile SingularAttribute<ClaimAmountReviewerTemplate, ClaimTemplate> claimTemplate;
	public static volatile SingularAttribute<ClaimAmountReviewerTemplate, BigDecimal> fromClaimAmount;
	public static volatile SingularAttribute<ClaimAmountReviewerTemplate, BigDecimal> toClaimAmount;
	public static volatile SingularAttribute<ClaimAmountReviewerTemplate, WorkFlowRuleMaster> Level1;
	public static volatile SingularAttribute<ClaimAmountReviewerTemplate, WorkFlowRuleMaster> Level2;
	public static volatile SingularAttribute<ClaimAmountReviewerTemplate, WorkFlowRuleMaster> Level3;
}
