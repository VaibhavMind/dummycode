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
import com.payasia.dao.CompanyAddressMappingDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyAddressMapping;
import com.payasia.dao.bean.CompanyAddressMapping_;
import com.payasia.dao.bean.Company_;

@Repository
public class CompanyAddressMappingDAOImpl extends BaseDAO implements
		CompanyAddressMappingDAO {

	@Override
	protected Object getBaseEntity() {
		CompanyAddressMapping companyAddressMapping = new CompanyAddressMapping();
		return companyAddressMapping;
	}

	@Override
	public void update(CompanyAddressMapping companyAddressMapping) {
		this.entityManagerFactory.merge(companyAddressMapping);
	}

	@Override
	public void delete(CompanyAddressMapping companyAddressMapping) {
		this.entityManagerFactory.remove(companyAddressMapping);
	}

	@Override
	public void save(CompanyAddressMapping companyAddressMapping) {
		super.save(companyAddressMapping);
	}

	@Override
	public CompanyAddressMapping findById(Long companyAddressMappingId) {
		return super.findById(CompanyAddressMapping.class,
				companyAddressMappingId);

	}

	@Override
	public List<CompanyAddressMapping> findByCondition(Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CompanyAddressMapping> criteriaQuery = cb
				.createQuery(CompanyAddressMapping.class);
		Root<CompanyAddressMapping> shortListRoot = criteriaQuery
				.from(CompanyAddressMapping.class);
		criteriaQuery.select(shortListRoot);
		Join<CompanyAddressMapping, Company> companyJoin = shortListRoot
				.join(CompanyAddressMapping_.company);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(companyJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.asc(shortListRoot
				.get(CompanyAddressMapping_.companyAddressId)));

		TypedQuery<CompanyAddressMapping> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<CompanyAddressMapping> mappingList = typedQuery.getResultList();
		return mappingList;

	}

	@Override
	public void deleteByCondition(Long companyId) {

		String queryString = "DELETE FROM CompanyAddressMapping cam WHERE cam.company.companyId = :companyId";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("companyId", companyId);

		q.executeUpdate();
	}

}
