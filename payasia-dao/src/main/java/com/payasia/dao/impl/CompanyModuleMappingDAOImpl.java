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
import com.payasia.dao.CompanyModuleMappingDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyModuleMapping;
import com.payasia.dao.bean.CompanyModuleMapping_;
import com.payasia.dao.bean.Company_;

@Repository
public class CompanyModuleMappingDAOImpl extends BaseDAO implements
		CompanyModuleMappingDAO {

	@Override
	public CompanyModuleMapping findById(long companyModuleMappingId) {

		return super.findById(CompanyModuleMapping.class,
				companyModuleMappingId);
	}

	@Override
	public void delete(Long companyId, Long moduleId) {
		StringBuilder query = new StringBuilder();
		query.append("delete from CompanyModuleMapping cmm where cmm.company.companyId =:companyId and cmm.moduleMaster.moduleId= :moduleId");
		Query qry = entityManagerFactory.createQuery(query.toString());
		qry.setParameter("companyId", companyId);
		qry.setParameter("moduleId", moduleId);
		qry.executeUpdate();
	}

	@Override
	public CompanyModuleMapping save(CompanyModuleMapping company) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object getBaseEntity() {
		CompanyModuleMapping companyModuleMapping = new CompanyModuleMapping();
		return companyModuleMapping;
	}
	
	@Override
	public List<CompanyModuleMapping> fetchModuleByCompanyId(Long companyID) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CompanyModuleMapping> criteriaQuery = cb
				.createQuery(CompanyModuleMapping.class);
		Root<CompanyModuleMapping> moduleMasterRoot = criteriaQuery.from(CompanyModuleMapping.class);
		criteriaQuery.select(moduleMasterRoot);
		Join<CompanyModuleMapping, Company> empCompanyJoin = moduleMasterRoot.join(CompanyModuleMapping_.company);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction,cb.equal(empCompanyJoin.get(Company_.companyId), companyID));
		criteriaQuery.where(restriction);
		TypedQuery<CompanyModuleMapping> moduleMasterTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		return  moduleMasterTypedQuery.getResultList();
	
	}

	

}
