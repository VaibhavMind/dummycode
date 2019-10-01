package com.payasia.common.dto;

import java.io.Serializable;


 
/**
 * The Class EntityMasterDTO.
 */
public class EntityMasterDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2420612601479807743L;

	/** The entity id. */
	private long entityId;

	/** The entity desc. */
	private String entityDesc;

	/** The entity name. */
	private String entityName;

	/**
	 * Gets the entity id.
	 *
	 * @return the entity id
	 */
	public long getEntityId() {
		return entityId;
	}

	/**
	 * Sets the entity id.
	 *
	 * @param entityId the new entity id
	 */
	public void setEntityId(long entityId) {
		this.entityId = entityId;
	}

	/**
	 * Gets the entity desc.
	 *
	 * @return the entity desc
	 */
	public String getEntityDesc() {
		return entityDesc;
	}

	/**
	 * Sets the entity desc.
	 *
	 * @param entityDesc the new entity desc
	 */
	public void setEntityDesc(String entityDesc) {
		this.entityDesc = entityDesc;
	}

	/**
	 * Gets the entity name.
	 *
	 * @return the entity name
	 */
	public String getEntityName() {
		return entityName;
	}

	/**
	 * Sets the entity name.
	 *
	 * @param entityName the new entity name
	 */
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	
}
