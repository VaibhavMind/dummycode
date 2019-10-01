package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.ForumTopicCommentAttachmentDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.ForumTopic;
import com.payasia.dao.bean.ForumTopicComment;
import com.payasia.dao.bean.ForumTopicCommentAttachment;
import com.payasia.dao.bean.ForumTopicCommentAttachment_;
import com.payasia.dao.bean.ForumTopicComment_;
import com.payasia.dao.bean.ForumTopic_;

@Repository
public class ForumTopicCommentAttachmentDAOImpl extends BaseDAO implements
		ForumTopicCommentAttachmentDAO {

	@Override
	protected Object getBaseEntity() {
		ForumTopicCommentAttachment forumTopicCommentAttachment = new ForumTopicCommentAttachment();
		return forumTopicCommentAttachment;
	}

	@Override
	public void save(ForumTopicCommentAttachment forumTopicCommentAttachment) {
		super.save(forumTopicCommentAttachment);

	}

	@Override
	public ForumTopicCommentAttachment findById(
			Long forumTopicCommentAttachmentId) {
		return super.findById(ForumTopicCommentAttachment.class,
				forumTopicCommentAttachmentId);
	}

	@Override
	public void update(ForumTopicCommentAttachment forumTopicCommentAttachment) {
		super.update(forumTopicCommentAttachment);

	}

	@Override
	public void delete(ForumTopicCommentAttachment forumTopicCommentAttachment) {
		super.delete(forumTopicCommentAttachment);

	}

	@Override
	public ForumTopicCommentAttachment saveReturn(
			ForumTopicCommentAttachment forumTopicCommentAttachment) {

		ForumTopicCommentAttachment persistObj = forumTopicCommentAttachment;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (ForumTopicCommentAttachment) getBaseEntity();
			beanUtil.copyProperties(persistObj, forumTopicCommentAttachment);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;
	}

	@Override
	public List<ForumTopicCommentAttachment> findByCommentId(long companyId,
			Long topicCommentId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ForumTopicCommentAttachment> criteriaQuery = cb
				.createQuery(ForumTopicCommentAttachment.class);
		Root<ForumTopicCommentAttachment> root = criteriaQuery
				.from(ForumTopicCommentAttachment.class);

		criteriaQuery.select(root);

		Join<ForumTopicCommentAttachment, ForumTopicComment> forumTopicCommentJoin = root
				.join(ForumTopicCommentAttachment_.forumTopicComment);
		Join<ForumTopicComment, ForumTopic> forumTopicJoin = forumTopicCommentJoin
				.join(ForumTopicComment_.forumTopic);
		Join<ForumTopic, Company> companyJoin = forumTopicJoin
				.join(ForumTopic_.company);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(
				forumTopicCommentJoin.get(ForumTopicComment_.topicCommentId),
				topicCommentId));
		restriction = cb.and(restriction,
				cb.equal(companyJoin.get(Company_.companyId), companyId));
		criteriaQuery.where(restriction);

		TypedQuery<ForumTopicCommentAttachment> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return typedQuery.getResultList();
	}

	@Override
	public void deleteByCondition(Long topicCommentId) {
		String queryString = "DELETE FROM ForumTopicCommentAttachment ftca WHERE ftca.forumTopicComment.topicCommentId = :topicCommentId";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("topicCommentId", topicCommentId);
		q.executeUpdate();
	}

	@Override
	public List<ForumTopicCommentAttachment> findByAttachmentId(long companyId,
			List<String> AttachmentIds) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ForumTopicCommentAttachment> criteriaQuery = cb
				.createQuery(ForumTopicCommentAttachment.class);
		Root<ForumTopicCommentAttachment> root = criteriaQuery
				.from(ForumTopicCommentAttachment.class);

		criteriaQuery.select(root);

		Join<ForumTopicCommentAttachment, ForumTopicComment> forumTopicCommentJoin = root
				.join(ForumTopicCommentAttachment_.forumTopicComment);
		Join<ForumTopicComment, ForumTopic> forumTopicJoin = forumTopicCommentJoin
				.join(ForumTopicComment_.forumTopic);
		Join<ForumTopic, Company> companyJoin = forumTopicJoin
				.join(ForumTopic_.company);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(
				restriction,
				root.get(ForumTopicCommentAttachment_.topicAttachmentId).in(
						AttachmentIds));
		restriction = cb.and(restriction,
				cb.equal(companyJoin.get(Company_.companyId), companyId));
		criteriaQuery.where(restriction);

		TypedQuery<ForumTopicCommentAttachment> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return typedQuery.getResultList();
	}

	@Override
	public List<ForumTopicCommentAttachment> findByCondition(long companyId,
			String attachmentName, Long topicCommentId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ForumTopicCommentAttachment> criteriaQuery = cb
				.createQuery(ForumTopicCommentAttachment.class);
		Root<ForumTopicCommentAttachment> root = criteriaQuery
				.from(ForumTopicCommentAttachment.class);

		criteriaQuery.select(root);

		Join<ForumTopicCommentAttachment, ForumTopicComment> forumTopicCommentJoin = root
				.join(ForumTopicCommentAttachment_.forumTopicComment);
		Join<ForumTopicComment, ForumTopic> forumTopicJoin = forumTopicCommentJoin
				.join(ForumTopicComment_.forumTopic);
		Join<ForumTopic, Company> companyJoin = forumTopicJoin
				.join(ForumTopic_.company);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(
				forumTopicCommentJoin.get(ForumTopicComment_.topicCommentId),
				topicCommentId));
		restriction = cb.and(restriction, cb.equal(
				cb.upper(root.get(ForumTopicCommentAttachment_.fileName)),
				attachmentName.toUpperCase()));
		restriction = cb.and(restriction,
				cb.equal(companyJoin.get(Company_.companyId), companyId));
		criteriaQuery.where(restriction);

		TypedQuery<ForumTopicCommentAttachment> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return typedQuery.getResultList();
	}

	public ForumTopicCommentAttachment findById(
			Long forumTopicCommentAttachmentId, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ForumTopicCommentAttachment> criteriaQuery = cb
				.createQuery(ForumTopicCommentAttachment.class);
		Root<ForumTopicCommentAttachment> root = criteriaQuery
				.from(ForumTopicCommentAttachment.class);

		criteriaQuery.select(root);

		Join<ForumTopicCommentAttachment, ForumTopicComment> forumTopicCommentJoin = root
				.join(ForumTopicCommentAttachment_.forumTopicComment);
		Join<ForumTopicComment, ForumTopic> forumTopicJoin = forumTopicCommentJoin
				.join(ForumTopicComment_.forumTopic);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(
				restriction,
				cb.equal(
						forumTopicJoin.get(ForumTopic_.company).get(
								Company_.companyId), companyId));
		restriction = cb.and(restriction, cb.equal(
				root.get(ForumTopicCommentAttachment_.topicAttachmentId),
				forumTopicCommentAttachmentId));

		criteriaQuery.where(restriction);

		TypedQuery<ForumTopicCommentAttachment> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<ForumTopicCommentAttachment> topicCommentAttachmentList = typedQuery.getResultList();
		return topicCommentAttachmentList.isEmpty() ? null : topicCommentAttachmentList.get(0);
	}

}
