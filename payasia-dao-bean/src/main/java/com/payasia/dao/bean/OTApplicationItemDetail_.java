package com.payasia.dao.bean;

import java.math.BigDecimal;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2012-07-12T19:14:34.713+0530")
@StaticMetamodel(OTApplicationItemDetail.class)
public class OTApplicationItemDetail_ {
	public static volatile SingularAttribute<OTApplicationItemDetail, Long> otApplicationItemDetailId;
	public static volatile SingularAttribute<OTApplicationItemDetail, BigDecimal> otTemplateItemValue;
	public static volatile SingularAttribute<OTApplicationItemDetail, OTApplicationItem> otApplicationItem;
	public static volatile SingularAttribute<OTApplicationItemDetail, OTTemplateItem> otTemplateItem;
}
