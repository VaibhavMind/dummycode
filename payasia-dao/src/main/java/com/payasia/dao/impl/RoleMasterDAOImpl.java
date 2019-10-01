package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.RoleMasterDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.RoleMaster;
import com.payasia.dao.bean.RoleMaster_;

/**
 * The Class RoleMasterDAOImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Repository
public class RoleMasterDAOImpl extends BaseDAO implements RoleMasterDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.RoleMasterDAO#findAll(com.payasia.common.form.SortCondition
	 * , java.lang.Long)
	 */
	@Override
	public List<RoleMaster> findAll(SortCondition sortDTO, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<RoleMaster> criteriaQuery = cb
				.createQuery(RoleMaster.class);
		Root<RoleMaster> roleMasterRoot = criteriaQuery.from(RoleMaster.class);

		criteriaQuery.select(roleMasterRoot);

		Join<RoleMaster, Company> empCompanyJoin = roleMasterRoot
				.join(RoleMaster_.company);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction,
				cb.equal(empCompanyJoin.get(Company_.companyId), companyId));
		criteriaQuery.where(restriction);

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForAllRole(sortDTO,
					roleMasterRoot);
			if (sortPath != null) {
				if (SortConstants.DB_ORDER_BY_ASC.equalsIgnoreCase(sortDTO
						.getOrderType())) {
					criteriaQuery.orderBy(cb.asc(sortPath));
				}
				if (SortConstants.DB_ORDER_BY_DESC.equalsIgnoreCase(sortDTO
						.getOrderType())) {
					criteriaQuery.orderBy(cb.desc(sortPath));
				}
			}

		}

		 

		TypedQuery<RoleMaster> roleMasterTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return roleMasterTypedQuery.getResultList();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.RoleMasterDAO#getSortPathForAllRole(com.payasia.common
	 * .form.SortCondition, javax.persistence.criteria.Root)
	 */
	@Override
	public Path<String> getSortPathForAllRole(SortCondition sortDTO,
			Root<RoleMaster> roleMasterRoot) {

		List<String> roleIsIdList = new ArrayList<String>();
		roleIsIdList.add(SortConstants.MANAGE_ROLES_ROLE_ID);

		List<String> roleIsColList = new ArrayList<String>();
		roleIsColList.add(SortConstants.MANAGE_ROLES_ROLE_NAME);

		Path<String> sortPath = null;

		if (roleIsColList.contains(sortDTO.getColumnName())) {
			sortPath = roleMasterRoot.get(colMap.get(RoleMaster.class
					+ sortDTO.getColumnName()));
		}
		return sortPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.BaseDAO#getBaseEntity()
	 */
	@Override
	protected Object getBaseEntity() {
		RoleMaster roleMaster = new RoleMaster();
		return roleMaster;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.RoleMasterDAO#save(com.payasia.dao.bean.RoleMaster)
	 */
	@Override
	public void save(RoleMaster roleMaster) {
		super.save(roleMaster);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.RoleMasterDAO#saveRole(com.payasia.dao.bean.RoleMaster)
	 */
	@Override
	public RoleMaster saveRole(RoleMaster roleMaster) {

		RoleMaster persistObj = roleMaster;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (RoleMaster) getBaseEntity();
			beanUtil.copyProperties(persistObj, roleMaster);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.RoleMasterDAO#findByID(long)
	 */
	@Override
	public RoleMaster findByID(long roleId) {
		return super.findById(RoleMaster.class, roleId);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.RoleMasterDAO#update(com.payasia.dao.bean.RoleMaster)
	 */
	@Override
	public void update(RoleMaster roleMaster) {
		super.update(roleMaster);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.RoleMasterDAO#delete(com.payasia.dao.bean.RoleMaster)
	 */
	@Override
	public void delete(RoleMaster roleMaster) {
		super.delete(roleMaster);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.RoleMasterDAO#getCountForAll(java.lang.Long)
	 */
	@Override
	public int getCountForAll(Long companyId) {

		return findAll(null, companyId).size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.RoleMasterDAO#findByRoleName(java.lang.String,
	 * java.lang.Long)
	 */
	@Override
	public RoleMaster findByRoleName(String roleName, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<RoleMaster> criteriaQuery = cb
				.createQuery(RoleMaster.class);
		Root<RoleMaster> roleMasterRoot = criteriaQuery.from(RoleMaster.class);

		Join<RoleMaster, Company> companyJoin = roleMasterRoot
				.join(RoleMaster_.company);

		criteriaQuery.select(roleMasterRoot);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(roleName.trim())) {

			restriction = cb.and(
					restriction,
					cb.like(roleMasterRoot.get(RoleMaster_.roleName),
							roleName.trim()));

		}

		restriction = cb.and(restriction,
				cb.equal(companyJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<RoleMaster> roleMasterTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<RoleMaster> roleMasterList = roleMasterTypedQuery.getResultList();
		if (roleMasterList != null &&  !roleMasterList.isEmpty()) {
			return roleMasterList.get(0);
		}
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.RoleMasterDAO#deleteByCondition(java.lang.Long)
	 */
	@Override
	public void deleteByCondition(Long companyId) {

		String queryString = "DELETE FROM RoleMaster r WHERE r.company.companyId = :company ";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("company", companyId);

		q.executeUpdate();
	}
	@Override
	public RoleMaster findByRoleId(long roleId,Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<RoleMaster> criteriaQuery = cb
				.createQuery(RoleMaster.class);
		Root<RoleMaster> roleMasterRoot = criteriaQuery.from(RoleMaster.class);

		Join<RoleMaster, Company> companyJoin = roleMasterRoot
				.join(RoleMaster_.company);

		criteriaQuery.select(roleMasterRoot);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
					cb.equal(roleMasterRoot.get(RoleMaster_.roleId),
							roleId));

		restriction = cb.and(restriction,
				cb.equal(companyJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<RoleMaster> roleMasterTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<RoleMaster> roleMasterList = roleMasterTypedQuery.getResultList();
		if (roleMasterList != null &&  !roleMasterList.isEmpty()) {
			return roleMasterList.get(0);
		}
		return null;

	}
}
