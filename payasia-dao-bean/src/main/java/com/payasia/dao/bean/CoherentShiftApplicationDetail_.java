package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2017-07-21T16:21:35.112+0530")
@StaticMetamodel(CoherentShiftApplicationDetail.class)
public class CoherentShiftApplicationDetail_ extends CompanyBaseEntity_ {
	public static volatile SingularAttribute<CoherentShiftApplicationDetail, Long> shiftApplicationDetailID;
	public static volatile SingularAttribute<CoherentShiftApplicationDetail, CoherentShiftApplication> coherentShiftApplication;
	public static volatile SingularAttribute<CoherentShiftApplicationDetail, Timestamp> shiftDate;
	public static volatile SingularAttribute<CoherentShiftApplicationDetail, AppCodeMaster> shiftType;
	public static volatile SingularAttribute<CoherentShiftApplicationDetail, Boolean> shift;
	public static volatile SingularAttribute<CoherentShiftApplicationDetail, String> remarks;
	public static volatile SingularAttribute<CoherentShiftApplicationDetail, Boolean> shiftChanged;
	public static volatile SingularAttribute<CoherentShiftApplicationDetail, Boolean> shiftTypeChanged;
}
