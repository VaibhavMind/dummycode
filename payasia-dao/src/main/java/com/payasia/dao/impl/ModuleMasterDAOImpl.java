package com.payasia.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.ModuleMasterDAO;
import com.payasia.dao.bean.ModuleMaster;
import com.payasia.dao.bean.ModuleMaster_;

@Repository
public class ModuleMasterDAOImpl extends BaseDAO implements ModuleMasterDAO {

	@Override
	protected Object getBaseEntity() {
		ModuleMaster moduleMaster = new ModuleMaster();
		return moduleMaster;
	}

	@Override
	public void update(ModuleMaster moduleMaster) {
		super.update(moduleMaster);
	}

	@Override
	public void delete(ModuleMaster moduleMaster) {
		super.delete(moduleMaster);
	}

	@Override
	public void save(ModuleMaster moduleMaster) {
		super.save(moduleMaster);
	}

	@Override
	public ModuleMaster findByID(Long moduleMasterId) {
		return super.findById(ModuleMaster.class, moduleMasterId);
	}

	@Override
	public ModuleMaster findByName(String payasiaLeaveModuleName) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ModuleMaster> criteriaQuery = cb
				.createQuery(ModuleMaster.class);
		Root<ModuleMaster> moduleMasterRoot = criteriaQuery
				.from(ModuleMaster.class);

		criteriaQuery.select(moduleMasterRoot);

		criteriaQuery.where(cb.equal(
				cb.upper(moduleMasterRoot.get(ModuleMaster_.moduleName)),
				payasiaLeaveModuleName.toUpperCase()));

		TypedQuery<ModuleMaster> moduleMasterTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<ModuleMaster> moduleMasterList = moduleMasterTypedQuery
				.getResultList();
		if (moduleMasterList != null &&  !moduleMasterList.isEmpty()) {
			return moduleMasterList.get(0);
		}
		return null;
	}


}
