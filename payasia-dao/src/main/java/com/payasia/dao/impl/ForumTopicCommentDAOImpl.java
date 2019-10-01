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

import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.ForumTopicCommentDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyUpdatedBaseEntity_;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.ForumTopic;
import com.payasia.dao.bean.ForumTopicComment;
import com.payasia.dao.bean.ForumTopicComment_;
import com.payasia.dao.bean.ForumTopic_;

@Repository
public class ForumTopicCommentDAOImpl extends BaseDAO implements
		ForumTopicCommentDAO {

	@Override
	protected Object getBaseEntity() {
		ForumTopicComment forumTopicComment = new ForumTopicComment();
		return forumTopicComment;
	}

	@Override
	public void save(ForumTopicComment forumTopicComment) {
		super.save(forumTopicComment);

	}

	@Override
	public ForumTopicComment findById(Long forumTopicCommentId) {
		return super.findById(ForumTopicComment.class, forumTopicCommentId);
	}

	@Override
	public void update(ForumTopicComment forumTopicComment) {
		super.update(forumTopicComment);

	}

	@Override
	public void delete(ForumTopicComment forumTopicComment) {
		super.delete(forumTopicComment);

	}

	@Override
	public ForumTopicComment saveReturn(ForumTopicComment forumTopicComment) {

		ForumTopicComment persistObj = forumTopicComment;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (ForumTopicComment) getBaseEntity();
			beanUtil.copyProperties(persistObj, forumTopicComment);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;
	}

	@Override
	public List<ForumTopicComment> getTopicCommentList(Long topicId,
			Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ForumTopicComment> criteriaQuery = cb
				.createQuery(ForumTopicComment.class);
		Root<ForumTopicComment> forumTopicCommentRoot = criteriaQuery
				.from(ForumTopicComment.class);

		criteriaQuery.select(forumTopicCommentRoot);

		Join<ForumTopicComment, ForumTopic> forumTopicJoin = forumTopicCommentRoot
				.join(ForumTopicComment_.forumTopic);
		Join<ForumTopic, Company> companyJoin = forumTopicJoin
				.join(ForumTopic_.company);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(companyJoin.get(Company_.companyId), companyId));
		restriction = cb.and(restriction,
				cb.equal(forumTopicJoin.get(ForumTopic_.topicId), topicId));

		criteriaQuery.where(restriction);
		criteriaQuery.orderBy(cb.desc(forumTopicCommentRoot
				.get(ForumTopicComment_.publishedDate)));

		TypedQuery<ForumTopicComment> forumTopicCommentQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		return forumTopicCommentQuery.getResultList();
	}

	@Override
	public List<ForumTopicComment> getTopicCommentListAscOrder(Long topicId,
			Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ForumTopicComment> criteriaQuery = cb
				.createQuery(ForumTopicComment.class);
		Root<ForumTopicComment> forumTopicCommentRoot = criteriaQuery
				.from(ForumTopicComment.class);

		criteriaQuery.select(forumTopicCommentRoot);

		Join<ForumTopicComment, ForumTopic> forumTopicJoin = forumTopicCommentRoot
				.join(ForumTopicComment_.forumTopic);
		Join<ForumTopic, Company> companyJoin = forumTopicJoin
				.join(ForumTopic_.company);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(companyJoin.get(Company_.companyId), companyId));
		restriction = cb.and(restriction,
				cb.equal(forumTopicJoin.get(ForumTopic_.topicId), topicId));

		criteriaQuery.where(restriction);
		criteriaQuery.orderBy(cb.asc(forumTopicCommentRoot
				.get(ForumTopicComment_.publishedDate)));

		TypedQuery<ForumTopicComment> forumTopicCommentQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		return forumTopicCommentQuery.getResultList();
	}
	
	@Override
	public ForumTopicComment findByForumTopicCommentId(Long topicCommentId,
			Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ForumTopicComment> criteriaQuery = cb
				.createQuery(ForumTopicComment.class);
		Root<ForumTopicComment> forumTopicCommentRoot = criteriaQuery
				.from(ForumTopicComment.class);

		criteriaQuery.select(forumTopicCommentRoot);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(forumTopicCommentRoot.get(CompanyUpdatedBaseEntity_.companyId), companyId));
		restriction = cb.and(restriction,
				cb.equal(forumTopicCommentRoot.get(ForumTopicComment_.topicCommentId), topicCommentId));

		criteriaQuery.where(restriction);
		criteriaQuery.orderBy(cb.desc(forumTopicCommentRoot
				.get(ForumTopicComment_.publishedDate)));

		TypedQuery<ForumTopicComment> forumTopicCommentQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		if(forumTopicCommentQuery.getResultList()!=null && forumTopicCommentQuery.getResultList().size()>0){
			return forumTopicCommentQuery.getResultList().get(0);
		}
		return null;
	}

}
