package com.payasia.dao.bean;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-12-27T10:10:56.872+0530")
@StaticMetamodel(ClaimApplicationItem.class)
public class ClaimApplicationItem_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<ClaimApplicationItem, Long> claimApplicationItemId;
	public static volatile SingularAttribute<ClaimApplicationItem, BigDecimal> amountBeforeTax;
	public static volatile SingularAttribute<ClaimApplicationItem, BigDecimal> claimAmount;
	public static volatile SingularAttribute<ClaimApplicationItem, BigDecimal> applicableClaimAmount;
	public static volatile SingularAttribute<ClaimApplicationItem, Timestamp> claimDate;
	public static volatile SingularAttribute<ClaimApplicationItem, Float> quantity;
	public static volatile SingularAttribute<ClaimApplicationItem, BigDecimal> unitPrice;
	public static volatile SingularAttribute<ClaimApplicationItem, String> receiptNumber;
	public static volatile SingularAttribute<ClaimApplicationItem, String> remarks;
	public static volatile SingularAttribute<ClaimApplicationItem, BigDecimal> taxAmount;
	public static volatile SingularAttribute<ClaimApplicationItem, Boolean> active;
	public static volatile SingularAttribute<ClaimApplicationItem, String> claimantName;
	public static volatile SingularAttribute<ClaimApplicationItem, ClaimApplication> claimApplication;
	public static volatile SingularAttribute<ClaimApplicationItem, EmployeeClaimTemplateItem> employeeClaimTemplateItem;
	public static volatile SetAttribute<ClaimApplicationItem, ClaimApplicationItemAttachment> claimApplicationItemAttachments;
	public static volatile SetAttribute<ClaimApplicationItem, ClaimApplicationItemWorkflow> claimApplicationItemWorkflows;
	public static volatile SetAttribute<ClaimApplicationItem, ClaimApplicationItemCustomField> claimApplicationItemCustomFields;
	public static volatile SetAttribute<ClaimApplicationItem, ClaimApplicationItemLundinDetail> claimApplicationItemLundinDetails;
	public static volatile SingularAttribute<ClaimApplicationItem, CurrencyMaster> currencyMaster;
	public static volatile SingularAttribute<ClaimApplicationItem, BigDecimal> exchangeRate;
	public static volatile SingularAttribute<ClaimApplicationItem, BigDecimal> forexReceiptAmount;
}
