package com.payasia.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.common.util.EntityEnum;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.EntityMasterDAO;
import com.payasia.dao.bean.EntityMaster;

/**
 * The Class EntityMasterDAOImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Repository
public class EntityMasterDAOImpl extends BaseDAO implements EntityMasterDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EntityMasterDAO#findAll()
	 */
	@Override
	public List<EntityMaster> findAll() {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EntityMaster> criteriaQuery = cb
				.createQuery(EntityMaster.class);
		Root<EntityMaster> entityMasterRoot = criteriaQuery
				.from(EntityMaster.class);

		criteriaQuery.select(entityMasterRoot);

		TypedQuery<EntityMaster> entityMasterTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<EntityMaster> allEntityMasterList = entityMasterTypedQuery
				.getResultList();
		return allEntityMasterList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EntityMasterDAO#findById(long)
	 */
	@Override
	public EntityMaster findById(long entityMasterId) {
		return super.findById(EntityMaster.class, entityMasterId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EntityMasterDAO#save(com.payasia.dao.bean.EntityMaster)
	 */
	@Override
	public void save(EntityMaster entityMaster) {
		super.save(entityMaster);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.BaseDAO#getBaseEntity()
	 */
	@Override
	protected Object getBaseEntity() {
		EntityMaster entityMaster = new EntityMaster();
		return entityMaster;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EntityMasterDAO#findByEntityName(java.lang.String)
	 */
	@Override
	public EntityMaster findByEntityName(String entityName) {
		EntityEnum entityEnum = EntityEnum.getFromName(entityName);
		if (entityEnum == null) {
			return null;
		}
		return findById(entityEnum.getEntityId());
	}

}
