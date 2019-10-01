package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.common.dto.AddClaimDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.ClaimApplicationItemAttachmentDAO;
import com.payasia.dao.bean.ClaimApplication;
import com.payasia.dao.bean.ClaimApplicationItem;
import com.payasia.dao.bean.ClaimApplicationItemAttachment;
import com.payasia.dao.bean.ClaimApplicationItemAttachment_;
import com.payasia.dao.bean.ClaimApplicationItem_;
import com.payasia.dao.bean.ClaimApplication_;

@Repository
public class ClaimApplicationItemAttachmentDAOImpl extends BaseDAO implements
		ClaimApplicationItemAttachmentDAO {

	@Override
	protected Object getBaseEntity() {
		ClaimApplicationItemAttachment claimApplicationItemAttachment = new ClaimApplicationItemAttachment();
		return claimApplicationItemAttachment;
	}

	@Override
	public ClaimApplicationItemAttachment saveReturn(
			ClaimApplicationItemAttachment claimApplicationItemAttachment) {

		ClaimApplicationItemAttachment persistObj = claimApplicationItemAttachment;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (ClaimApplicationItemAttachment) getBaseEntity();
			beanUtil.copyProperties(persistObj, claimApplicationItemAttachment);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;
	}

	@Override
	public void save(
			ClaimApplicationItemAttachment claimApplicationItemAttachment) {
		super.save(claimApplicationItemAttachment);
	}

	@Override
	public void update(
			ClaimApplicationItemAttachment claimApplicationItemAttachment) {
		super.update(claimApplicationItemAttachment);
	}

	@Override
	public ClaimApplicationItemAttachment findByID(
			long claimApplicationItemAttachmentId) {
		return super.findById(ClaimApplicationItemAttachment.class,
				claimApplicationItemAttachmentId);
	}

	@Override
	public void delete(
			ClaimApplicationItemAttachment claimApplicationItemAttachment) {
		super.delete(claimApplicationItemAttachment);
	}

	@Override
	public ClaimApplicationItemAttachment findByClaimApplicationItemAttachmentID(AddClaimDTO addClaimDTO) {
		
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimApplicationItemAttachment> criteriaQuery = cb
				.createQuery(ClaimApplicationItemAttachment.class);
		Root<ClaimApplicationItemAttachment> claimApplicationItemAttachRoot = criteriaQuery
				.from(ClaimApplicationItemAttachment.class);
		criteriaQuery.select(claimApplicationItemAttachRoot);
		
		Join<ClaimApplicationItemAttachment, ClaimApplicationItem> claimItemJoin = claimApplicationItemAttachRoot
				.join(ClaimApplicationItemAttachment_.claimApplicationItem);
		
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(claimApplicationItemAttachRoot.get(ClaimApplicationItemAttachment_.claimApplicationItemAttachmentId), addClaimDTO.getClaimApplicationAttachementId()));
		Join<ClaimApplicationItem, ClaimApplication> claimAppJoin = claimItemJoin
				.join(ClaimApplicationItem_.claimApplication);
		
		if(!addClaimDTO.getAdmin()){		
		restriction = cb.and(restriction, cb.equal(claimAppJoin.get(ClaimApplication_.employee), addClaimDTO.getEmployeeId()));
		}
		
		restriction = cb.and(restriction, cb.equal(claimAppJoin.get(ClaimApplication_.company), addClaimDTO.getCompanyId()));
		criteriaQuery.where(restriction);
		
		TypedQuery<ClaimApplicationItemAttachment> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		List<ClaimApplicationItemAttachment> list = empTypedQuery.getResultList();
		return list!=null && !list.isEmpty()  ? list.get(0) :null;

	}

}
