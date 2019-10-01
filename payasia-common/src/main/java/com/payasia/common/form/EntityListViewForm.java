package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

/**
 * The Class EntityListViewForm.
 */
public class EntityListViewForm implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4718317147902110507L;

	/** The company id. */
	private Integer companyId;
	
	/** The entity id. */
	private Long entityId;
	
	/** The entity. */
	private String entity;
	
	/** The view name id. */
	private Long viewNameId;
	
	/** The view name. */
	private String viewName;
	
	/** The records per page. */
	private String recordsPerPage;
	
	/** The entity list view field. */
	private List<EntityListViewFieldForm> entityListViewField;
	
	/**
	 * Gets the entity id.
	 *
	 * @return the entity id
	 */
	public Long getEntityId() {
		return entityId;
	}

	/**
	 * Sets the entity id.
	 *
	 * @param entityId the new entity id
	 */
	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

	/**
	 * Gets the view name id.
	 *
	 * @return the view name id
	 */
	public Long getViewNameId() {
		return viewNameId;
	}

	/**
	 * Sets the view name id.
	 *
	 * @param viewNameId the new view name id
	 */
	public void setViewNameId(Long viewNameId) {
		this.viewNameId = viewNameId;
	}


	/**
	 * Gets the company id.
	 *
	 * @return the company id
	 */
	public Integer getCompanyId() {
		return companyId;
	}

	/**
	 * Sets the company id.
	 *
	 * @param companyId the new company id
	 */
	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	/**
	 * Gets the entity.
	 *
	 * @return the entity
	 */
	public String getEntity() {
		return entity;
	}

	/**
	 * Sets the entity.
	 *
	 * @param entity the new entity
	 */
	public void setEntity(String entity) {
		this.entity = entity;
	}

	/**
	 * Gets the view name.
	 *
	 * @return the view name
	 */
	public String getViewName() {
		return viewName;
	}

	/**
	 * Sets the view name.
	 *
	 * @param viewName the new view name
	 */
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	/**
	 * Gets the records per page.
	 *
	 * @return the records per page
	 */
	public String getRecordsPerPage() {
		return recordsPerPage;
	}

	/**
	 * Sets the records per page.
	 *
	 * @param recordsPerPage the new records per page
	 */
	public void setRecordsPerPage(String recordsPerPage) {
		this.recordsPerPage = recordsPerPage;
	}

	/**
	 * Sets the entity list view field.
	 *
	 * @param entityListViewField the new entity list view field
	 */
	public void setEntityListViewField(
			List<EntityListViewFieldForm> entityListViewField) {
		this.entityListViewField = entityListViewField;
	}

	/**
	 * Gets the entity list view field.
	 *
	 * @return the entity list view field
	 */
	public List<EntityListViewFieldForm> getEntityListViewField() {
		return entityListViewField;
	}

	

}
