package com.payasia.dao.impl;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.RoleSectionMappingDAO;
import com.payasia.dao.bean.RoleMaster;
import com.payasia.dao.bean.RoleMaster_;
import com.payasia.dao.bean.RoleSectionMapping;
import com.payasia.dao.bean.RoleSectionMapping_;

@Repository
public class RoleSectionMappingDAOImpl extends BaseDAO implements
		RoleSectionMappingDAO {

	@Override
	protected Object getBaseEntity() {
		RoleSectionMapping roleSectionMapping = new RoleSectionMapping();
		return roleSectionMapping;
	}

	@Override
	public void update(RoleSectionMapping roleSectionMapping) {
		this.entityManagerFactory.merge(roleSectionMapping);
	}

	@Override
	public void delete(RoleSectionMapping roleSectionMapping) {
		this.entityManagerFactory.remove(roleSectionMapping);
	}

	@Override
	public void save(RoleSectionMapping roleSectionMapping) {
		super.save(roleSectionMapping);
	}

	@Override
	public RoleSectionMapping findById(Long roleSectionMappingId) {
		return super.findById(RoleSectionMapping.class, roleSectionMappingId);

	}

	@Override
	public void deleteByCondition(long roleId) {

		String queryString = "DELETE FROM RoleSectionMapping e WHERE e.roleMaster.roleId = :roleId";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("roleId", roleId);
		q.executeUpdate();

	}

	@Override
	public List<RoleSectionMapping> findByRoleId(Long roleId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<RoleSectionMapping> criteriaQuery = cb
				.createQuery(RoleSectionMapping.class);
		Root<RoleSectionMapping> root = criteriaQuery
				.from(RoleSectionMapping.class);
		Join<RoleSectionMapping, RoleMaster> roleMasterJoin = root
				.join(RoleSectionMapping_.roleMaster);
		criteriaQuery.select(root);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction,
				cb.equal(roleMasterJoin.get(RoleMaster_.roleId), roleId));
		criteriaQuery.where(restriction);

		TypedQuery<RoleSectionMapping> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		return typedQuery.getResultList();

	}

	@Override
	public List<Long> findByRoleIds(List<Long> roleIdList) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<RoleSectionMapping> root = criteriaQuery
				.from(RoleSectionMapping.class);
		Join<RoleSectionMapping, RoleMaster> roleMasterJoin = root
				.join(RoleSectionMapping_.roleMaster);
		criteriaQuery.select(
				root.get(RoleSectionMapping_.formId).as(Long.class)).distinct(
				true);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, roleMasterJoin
				.get(RoleMaster_.roleId).in(roleIdList));
		criteriaQuery.where(restriction);

		TypedQuery<Long> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		return typedQuery.getResultList();

	}

	@Override
	public void deleteByFormIdAndCompanyId(Long formId, Long companyId) {

		String queryString = "DELETE FROM RoleSectionMapping e WHERE e.formId = :formId AND e.companyId = :companyId";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("formId", formId);
		q.setParameter("companyId", companyId);
		q.executeUpdate();

	}
	
	@Override
	public void deleteByCondition(long roleId,Long companyId) {

		String queryString = "DELETE FROM RoleSectionMapping e WHERE e.roleMaster.roleId = :roleId AND e.companyId = :companyId";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("roleId", roleId);
		q.setParameter("companyId", companyId);
		q.executeUpdate();

	}

}
