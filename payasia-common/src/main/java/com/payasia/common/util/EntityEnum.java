package com.payasia.common.util;


public enum EntityEnum {
	EMPLOYEE(1, PayAsiaConstants.EMPLOYEE_ENTITY_NAME), PAYSLIP_FORM(2,
			PayAsiaConstants.PAY_SLIP_FORM_ENTITY_NAME), COMPANY(3,
			PayAsiaConstants.COMPANY_ENTITY_NAME), PAYSLIP(4,
			PayAsiaConstants.PAY_SLIP_ENTITY_NAME), PAYDATA_COLLECTION(5,
			PayAsiaConstants.PAY_DATA_COLLECTION_ENTITY_NAME), COMPANY_AND_EMPLOYEE(
			6, PayAsiaConstants.COMPANY_AND_EMPLOYEE_ENTITY_NAME);

	private final long entityId;
	private final String entityName;

	EntityEnum(long entityId, String entityName) {
		this.entityId = entityId;
		this.entityName = entityName;
	}

	public static EntityEnum getFromName(String entityName) {
		for (EntityEnum entity : EntityEnum.values()) {
			if (entityName.equals(entity.entityName)) {
				return entity;
			}
		}
		return null;
	}

	public long getEntityId() {
		return entityId;
	}

	public String getEntityName() {
		return entityName;
	}

}

