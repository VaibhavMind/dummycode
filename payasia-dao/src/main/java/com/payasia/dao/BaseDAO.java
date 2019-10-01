/*******************************************************************************
 * Copyright (c) 2010 *All rights reserved.
 * The information contained here in is confidential.
 * 
 * Created by:	gurmeetsinghvirdi
 * Created on:	July 20, 2011
 * Description:	
 * 
 * @author gurmeetsinghvirdi
 ******************************************************************************/
package com.payasia.dao;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;

import org.hibernate.Session;

import com.payasia.common.dto.SortConditionDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.PageRequest;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.util.TableUtils;

/**
 * The Class BaseDAO.
 */
public abstract class BaseDAO extends BasePACore {

	@PersistenceContext
	protected EntityManager entityManagerFactory;

	protected final Map<String, String> colMap = TableUtils.colMap;

	/**
	 * Save entity.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param saveObj
	 *            the save obj
	 */
	
	public <T> void save(T saveObj) {
		T persistObj = saveObj;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (T) getBaseEntity();
			beanUtil.copyProperties(persistObj, saveObj);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
	}

	/**
	 * Update entity.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param updateObj
	 *            the update obj
	 */
	public <T> void update(T updateObj) {
		this.entityManagerFactory.merge(updateObj);
	}

	/**
	 * Delete entity.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param delObj
	 *            the del obj
	 */
	public <T> void delete(T delObj) {
		this.entityManagerFactory.remove(delObj);
	}

	/**
	 * Find by id/primary key.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param klass
	 *            the entity class
	 * @param id
	 *            the primary key object
	 * 
	 * @return the T
	 */
	public <T> T findById(Class<T> klass, Serializable id) {
		T entityObj = null;
		try {
			entityObj = klass.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		entityObj = this.entityManagerFactory.find(klass, id);

		return entityObj;
	}

	/**
	 * Finds entity by id/primay key and detaches it from JPA session.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param klass
	 *            the entity class
	 * @param id
	 *            the primary key object
	 * @return the detached entity
	 */
	public <T> T getDetachedEntity(Class<T> klass, Serializable id) {
		T entityObj = null;
		try {
			entityObj = klass.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		entityObj = this.entityManagerFactory.find(klass, id);
		this.entityManagerFactory.detach(entityObj);

		return entityObj;
	}

	/**
	 * Gets the base entity with default values set for not null columns as per
	 * db.
	 * 
	 * @return the base entity
	 */
	protected abstract Object getBaseEntity();

	/**
	 * Gets the start position.
	 * 
	 * @param pageRequestDTO
	 *            the page request dto
	 * @return the start position
	 */
	protected int getStartPosition(PageRequest pageRequestDTO) {
		int firstRow = ((pageRequestDTO.getPageNumber() - 1) * pageRequestDTO
				.getPageSize());

		return firstRow;
	}

	/**
	 * Adds order to orderList.
	 * 
	 * @param cb
	 *            the criteriaBuilder
	 * @param orderList
	 *            the order list
	 * @param sortDTO
	 *            the sort dto
	 * @param sortPath
	 *            the sort path
	 */
	public void addOrder(CriteriaBuilder cb, List<Order> orderList,
			SortConditionDTO sortDTO, Path<String> sortPath) {
		Order order;
		if (SortConstants.DB_ORDER_BY_ASC.equals(sortDTO.getOrderType())) {
			order = cb.asc(cb.upper(cb.trim(sortPath)));
		} else {
			order = cb.desc(cb.upper(cb.trim(sortPath)));
		}
		orderList.add(order);
	}

	protected Session getSession() {
		Session session = (Session) entityManagerFactory.getDelegate();
		return session;
	}

}
