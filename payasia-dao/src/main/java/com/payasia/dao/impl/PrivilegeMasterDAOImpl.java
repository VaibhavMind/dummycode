package com.payasia.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.payasia.common.dto.ManageRolesConditionDTO;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.PrivilegeMasterDAO;
import com.payasia.dao.bean.ModuleMaster;
import com.payasia.dao.bean.ModuleMaster_;
import com.payasia.dao.bean.PrivilegeMaster;
import com.payasia.dao.bean.PrivilegeMaster_;
import com.payasia.dao.bean.RoleMaster;
import com.payasia.dao.bean.RoleMaster_;

/**
 * The Class PrivilegeMasterDAOImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Repository
public class PrivilegeMasterDAOImpl extends BaseDAO implements
		PrivilegeMasterDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.PrivilegeMasterDAO#findAll(com.payasia.common.form.
	 * SortCondition)
	 */
	@Override
	public List<PrivilegeMaster> findAll(SortCondition sortDTO,
			ManageRolesConditionDTO conditionDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<PrivilegeMaster> criteriaQuery = cb
				.createQuery(PrivilegeMaster.class);
		Root<PrivilegeMaster> privilegeMasterRoot = criteriaQuery
				.from(PrivilegeMaster.class);
		Join<PrivilegeMaster, ModuleMaster> moduleMasterJoin = privilegeMasterRoot
				.join(PrivilegeMaster_.moduleMaster);
		criteriaQuery.select(privilegeMasterRoot);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(conditionDTO.getPrivilege())) {
			restriction = cb.and(restriction, cb.like(
					privilegeMasterRoot.get(PrivilegeMaster_.privilegeDesc),
					conditionDTO.getPrivilege() + '%'));
		}

		if (StringUtils.isNotBlank(conditionDTO.getRole())) {

			restriction = cb.and(restriction, cb.like(
					privilegeMasterRoot.get(PrivilegeMaster_.privilegeRole),
					conditionDTO.getRole() + '%'));
		}
		if (StringUtils.isNotBlank(conditionDTO.getModule())) {
			restriction = cb.and(restriction, cb.like(
					moduleMasterJoin.get(ModuleMaster_.moduleName),
					conditionDTO.getModule() + '%'));

		}

		restriction = cb.and(restriction, cb.equal(
				privilegeMasterRoot.get(PrivilegeMaster_.active), true));
		criteriaQuery.where(restriction);
		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForAllPrivilege(sortDTO,
					privilegeMasterRoot);
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
		List<Order> orderByList = new ArrayList<>();
		orderByList.add(cb.desc(moduleMasterJoin.get(ModuleMaster_.moduleId)));
		orderByList.add(cb.asc(privilegeMasterRoot
				.get(PrivilegeMaster_.privilegeRole)));

		criteriaQuery.orderBy(orderByList);
		TypedQuery<PrivilegeMaster> privilegeMasterTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		return privilegeMasterTypedQuery.getResultList();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.PrivilegeMasterDAO#getSortPathForAllPrivilege(com.payasia
	 * .common.form.SortCondition, javax.persistence.criteria.Root)
	 */
	@Override
	public Path<String> getSortPathForAllPrivilege(SortCondition sortDTO,
			Root<PrivilegeMaster> privilegeMasterRoot) {

		List<String> privilegeIsIdList = new ArrayList<String>();
		privilegeIsIdList.add(SortConstants.MANAGE_ROLES_PRIVILEGE_ID);

		List<String> privilegeIsColList = new ArrayList<String>();
		privilegeIsColList.add(SortConstants.MANAGE_PRIVILEGE_ROLE_NAME);

		Path<String> sortPath = null;

		if (privilegeIsColList.contains(sortDTO.getColumnName())) {
			sortPath = privilegeMasterRoot.get(colMap.get(PrivilegeMaster.class
					+ sortDTO.getColumnName()));
		}
		return sortPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.PrivilegeMasterDAO#findByRole(java.lang.Long)
	 */
	@Override
	public List<PrivilegeMaster> findByRole(Long roleId,
			ManageRolesConditionDTO conditionDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<PrivilegeMaster> criteriaQuery = cb
				.createQuery(PrivilegeMaster.class);
		Root<PrivilegeMaster> privilegeMasterRoot = criteriaQuery
				.from(PrivilegeMaster.class);

		criteriaQuery.select(privilegeMasterRoot);

		Join<PrivilegeMaster, RoleMaster> privilegeMasterRootJoin = privilegeMasterRoot
				.join(PrivilegeMaster_.roleMasters);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(conditionDTO.getPrivilege())) {
			restriction = cb.and(restriction, cb.like(
					privilegeMasterRoot.get(PrivilegeMaster_.privilegeDesc),
					conditionDTO.getPrivilege() + '%'));
		}

		if (StringUtils.isNotBlank(conditionDTO.getRole())) {
			restriction = cb.and(restriction, cb.like(
					privilegeMasterRoot.get(PrivilegeMaster_.privilegeRole),
					conditionDTO.getRole() + '%'));
		}
		if (StringUtils.isNotBlank(conditionDTO.getModule())) {
			Join<PrivilegeMaster, ModuleMaster> moduleMasterRootJoin = privilegeMasterRoot
					.join(PrivilegeMaster_.moduleMaster);
			restriction = cb.and(restriction, cb.like(
					moduleMasterRootJoin.get(ModuleMaster_.moduleName),
					conditionDTO.getModule() + '%'));

		}

		restriction = cb.and(restriction, cb.equal(
				privilegeMasterRootJoin.get(RoleMaster_.roleId), roleId));

		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.asc(privilegeMasterRoot
				.get(PrivilegeMaster_.privilegeId)));

		TypedQuery<PrivilegeMaster> privilegeMasterTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		return privilegeMasterTypedQuery.getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.BaseDAO#getBaseEntity()
	 */
	@Override
	protected Object getBaseEntity() {
		PrivilegeMaster privilegeMaster = new PrivilegeMaster();
		return privilegeMaster;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.PrivilegeMasterDAO#findByID(java.lang.Long)
	 */
	@Override
	public PrivilegeMaster findByID(Long privilegeId) {
		return super.findById(PrivilegeMaster.class, privilegeId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.PrivilegeMasterDAO#getCountForAll()
	 */
	@Override
	public int getCountForAll(ManageRolesConditionDTO conditionDTO) {

		return findAll(null, conditionDTO).size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.PrivilegeMasterDAO#getCountForRole(java.lang.Long)
	 */
	@Override
	public int getCountForRole(Long roleId, ManageRolesConditionDTO conditionDTO) {
		return findByRole(roleId, conditionDTO).size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.PrivilegeMasterDAO#getPrivilegesByRole(java.lang.Long)
	 */
	@Override
	public List<PrivilegeMaster> getPrivilegesByRole(Long roleId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<PrivilegeMaster> criteriaQuery = cb
				.createQuery(PrivilegeMaster.class);
		Root<PrivilegeMaster> privilegeMasterRoot = criteriaQuery
				.from(PrivilegeMaster.class);

		criteriaQuery.select(privilegeMasterRoot);

		Join<PrivilegeMaster, RoleMaster> privilegeMasterRootJoin = privilegeMasterRoot
				.join(PrivilegeMaster_.roleMasters);
		Path<Long> roleID = privilegeMasterRootJoin.get(RoleMaster_.roleId);
		criteriaQuery.where(cb.equal(roleID, roleId));

		criteriaQuery.orderBy(cb.asc(privilegeMasterRoot
				.get(PrivilegeMaster_.privilegeId)));

		TypedQuery<PrivilegeMaster> privilegeMasterTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<PrivilegeMaster> privilegeMasterList = privilegeMasterTypedQuery
				.getResultList();
		return privilegeMasterList;
	}

	@Override
	public Boolean getTimesheetPrivilegesByRole(Long roleId) {

		boolean isAssigned = false;
		try {
			CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
			CriteriaQuery<PrivilegeMaster> criteriaQuery = cb
					.createQuery(PrivilegeMaster.class);
			Root<PrivilegeMaster> privilegeMasterRoot = criteriaQuery
					.from(PrivilegeMaster.class);

			criteriaQuery.select(privilegeMasterRoot);

			Predicate restriction = cb.conjunction();

			Join<PrivilegeMaster, RoleMaster> privilegeMasterRootJoin = privilegeMasterRoot
					.join(PrivilegeMaster_.roleMasters);
			Path<Long> roleID = privilegeMasterRootJoin.get(RoleMaster_.roleId);

			restriction = cb.and(restriction, cb.equal(roleID, roleId));

			restriction = cb.and(restriction, cb.equal(
					privilegeMasterRoot.get(PrivilegeMaster_.privilegeName),
					PayAsiaConstants.LUNDIN_PENDING_TIMESHEET));
			criteriaQuery.where(restriction);
			TypedQuery<PrivilegeMaster> privilegeMasterTypedQuery = entityManagerFactory
					.createQuery(criteriaQuery);

			isAssigned = privilegeMasterTypedQuery.getResultList().isEmpty();

			return !isAssigned;
		} catch (Exception e) {
			return isAssigned;
		}
	}

	
	public List<PrivilegeMaster> getPrivilegesByRole(Long roleId,Long moduleId,String privilegeRole) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<PrivilegeMaster> criteriaQuery = cb
				.createQuery(PrivilegeMaster.class);
		Root<PrivilegeMaster> privilegeMasterRoot = criteriaQuery
				.from(PrivilegeMaster.class);

		criteriaQuery.select(privilegeMasterRoot);

		Predicate restriction = cb.conjunction();
	
		
		Join<PrivilegeMaster, RoleMaster> privilegeMasterRootJoin = privilegeMasterRoot
				.join(PrivilegeMaster_.roleMasters);
		
		Join<PrivilegeMaster, ModuleMaster> moduleMasterRootJoin = privilegeMasterRoot
				.join(PrivilegeMaster_.moduleMaster);
		
		Path<Long> roleID = privilegeMasterRootJoin.get(RoleMaster_.roleId);
		
		restriction = cb.and(restriction, cb.equal(roleID, roleId));
		
		Path<Long> moduleID = moduleMasterRootJoin.get(ModuleMaster_.moduleId);
		restriction = cb.and(restriction, cb.equal(moduleID, moduleId));

		restriction = cb.and(restriction, cb.like(
				privilegeMasterRoot.get(PrivilegeMaster_.privilegeRole),
				privilegeRole+ '%'));
		
		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.asc(privilegeMasterRoot
				.get(PrivilegeMaster_.privilegeId)));

		TypedQuery<PrivilegeMaster> privilegeMasterTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<PrivilegeMaster> privilegeMasterList = privilegeMasterTypedQuery
				.getResultList();
		return privilegeMasterList;
	}

	
}
