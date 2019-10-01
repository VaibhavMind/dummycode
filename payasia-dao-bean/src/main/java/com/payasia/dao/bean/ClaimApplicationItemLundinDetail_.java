package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2015-07-02T11:08:19.683+0530")
@StaticMetamodel(ClaimApplicationItemLundinDetail.class)
public class ClaimApplicationItemLundinDetail_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<ClaimApplicationItemLundinDetail, Long> lundinDetailId;
	public static volatile SingularAttribute<ClaimApplicationItemLundinDetail, ClaimApplicationItem> claimApplicationItem;
	public static volatile SingularAttribute<ClaimApplicationItemLundinDetail, LundinBlock> lundinBlock;
	public static volatile SingularAttribute<ClaimApplicationItemLundinDetail, LundinAFE> lundinAFE;
}
