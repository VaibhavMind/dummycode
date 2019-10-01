package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-10-07T14:47:07.979+0530")
@StaticMetamodel(PrivilegeMaster.class)
public class PrivilegeMaster_ {
	public static volatile SingularAttribute<PrivilegeMaster, Long> privilegeId;
	public static volatile SingularAttribute<PrivilegeMaster, String> privilegeDesc;
	public static volatile SingularAttribute<PrivilegeMaster, String> privilegeName;
	public static volatile SetAttribute<PrivilegeMaster, RoleMaster> roleMasters;
	public static volatile SingularAttribute<PrivilegeMaster, ModuleMaster> moduleMaster;
	public static volatile SingularAttribute<PrivilegeMaster, String> privilegeRole;
	public static volatile SingularAttribute<PrivilegeMaster, Boolean> active;
}
