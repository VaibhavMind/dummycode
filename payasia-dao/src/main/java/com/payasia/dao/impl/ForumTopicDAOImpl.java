package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.payasia.common.dto.DiscussionBoardConditionDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.ForumTopicDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyUpdatedBaseEntity_;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.Employee_;
import com.payasia.dao.bean.ForumTopic;
import com.payasia.dao.bean.ForumTopicComment;
import com.payasia.dao.bean.ForumTopicComment_;
import com.payasia.dao.bean.ForumTopic_;

@Repository
public class ForumTopicDAOImpl extends BaseDAO implements ForumTopicDAO {

	@Override
	public ForumTopic findById(Long forumTopicId) {
		return super.findById(ForumTopic.class, forumTopicId);
	}

	@Override
	public void updateForumTopic(ForumTopic forumTopic) {
		super.update(forumTopic);
	}

	@Override
	protected Object getBaseEntity() {
		ForumTopic forumTopic = new ForumTopic();
		return forumTopic;
	}

	@Override
	public List<ForumTopic> findDiscussionBoardTopics(Long employeeId, Long companyId, SortCondition sortDTO,
			DiscussionBoardConditionDTO conditionDTO) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ForumTopic> criteriaQuery = cb.createQuery(ForumTopic.class);
		Root<ForumTopic> forumTopicRoot = criteriaQuery.from(ForumTopic.class);
		criteriaQuery.select(forumTopicRoot);
		Predicate restriction = cb.conjunction();
		Join<ForumTopic, ForumTopicComment> forumTopicCommentJoin = forumTopicRoot.join(ForumTopic_.forumTopicComments,
				JoinType.LEFT);

		if (conditionDTO.getCompanyId() != 0L) {
			Join<ForumTopic, Company> forumTopicCompanyJoin = forumTopicRoot.join(ForumTopic_.company);
			restriction = cb.and(restriction,
					cb.equal(forumTopicCompanyJoin.get(Company_.companyId), conditionDTO.getCompanyId()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getTopicName())) {
			restriction = cb.and(restriction, cb.like(cb.upper(forumTopicRoot.get(ForumTopic_.topicName)),
					'%' + conditionDTO.getTopicName().toUpperCase() + '%'));
		}

		if (StringUtils.isNotBlank(conditionDTO.getTopicAuthor())) {
			Join<ForumTopic, Employee> forumTopicEmployeeJoin = forumTopicRoot.join(ForumTopic_.createdBy,
					JoinType.LEFT);
			restriction = cb.and(restriction,
					cb.or(cb.like(cb.upper(forumTopicEmployeeJoin.get(Employee_.firstName)),
							'%' + conditionDTO.getTopicAuthor().toUpperCase() + '%'),
							cb.like(cb.upper(forumTopicEmployeeJoin.get(Employee_.lastName)),
									'%' + conditionDTO.getTopicAuthor().toUpperCase() + '%'),
							cb.like(cb.upper(forumTopicEmployeeJoin.get(Employee_.employeeNumber)),
									'%' + conditionDTO.getTopicAuthor().toUpperCase() + '%')));
		}

		if (StringUtils.isNotBlank(conditionDTO.getTopicStatus())) {
			if (PayAsiaConstants.PAYASIA_DISCUSSION_BOARD_TOPIC_STATUS_OPEN.equals(conditionDTO.getTopicStatus())) {
				restriction = cb.and(restriction, cb.equal((forumTopicRoot.get(ForumTopic_.status)), true));
			}
			if (PayAsiaConstants.PAYASIA_DISCUSSION_BOARD_TOPIC_STATUS_CLOSED.equals(conditionDTO.getTopicStatus())) {
				restriction = cb.and(restriction, cb.equal((forumTopicRoot.get(ForumTopic_.status)), false));
			}
		}

		if (StringUtils.isNotBlank(conditionDTO.getCreatedOn())) {
			restriction = cb.and(restriction, cb.equal(forumTopicRoot.get(ForumTopic_.createdDate).as(Date.class),
					DateUtils.stringToTimestamp(conditionDTO.getCreatedOn())));
		}

		if (StringUtils.isNotBlank(conditionDTO.getCreatedFrom())) {
			restriction = cb.and(restriction,
					cb.greaterThanOrEqualTo(forumTopicRoot.get(ForumTopic_.createdDate).as(Date.class),
							DateUtils.stringToTimestamp(conditionDTO.getCreatedFrom())));
		}
		if (StringUtils.isNotBlank(conditionDTO.getCreatedTo())) {
			restriction = cb.and(restriction,
					cb.lessThanOrEqualTo(forumTopicRoot.get(ForumTopic_.createdDate).as(Date.class),
							DateUtils.stringToTimestamp(conditionDTO.getCreatedTo())));
		}

		restriction = cb.and(restriction,
				cb.equal(cb.function("year", Integer.class, forumTopicRoot.get(ForumTopic_.createdDate)),
						conditionDTO.getYear()));

		criteriaQuery.where(restriction);

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForSearchForumTopic(sortDTO, forumTopicRoot);
			if (sortPath != null) {
				if (SortConstants.DB_ORDER_BY_ASC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.asc(sortPath));
				}
				if (SortConstants.DB_ORDER_BY_DESC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.desc(sortPath));
				}
			}

			criteriaQuery.orderBy(cb.desc(forumTopicRoot.get(ForumTopic_.status)),
					cb.desc(forumTopicRoot.get(ForumTopic_.createdDate)),
					cb.desc(forumTopicCommentJoin.get(ForumTopicComment_.topicCommentId)));
		}

		TypedQuery<ForumTopic> forumTopicQuery = entityManagerFactory.createQuery(criteriaQuery);

		return forumTopicQuery.getResultList();
	}

	@Override
	public List<ForumTopicComment> getDiscussionBoardTopicComments(Long companyId, Long topicId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ForumTopicComment> criteriaQuery = cb.createQuery(ForumTopicComment.class);
		Root<ForumTopicComment> forumTopicCommentRoot = criteriaQuery.from(ForumTopicComment.class);

		criteriaQuery.select(forumTopicCommentRoot);

		Predicate restriction = cb.conjunction();

		if (companyId != 0L) {

			restriction = cb.and(restriction,
					cb.equal(forumTopicCommentRoot.get(CompanyUpdatedBaseEntity_.companyId), companyId));
		}

		if (topicId != 0L) {
			restriction = cb.and(restriction,
					cb.equal(forumTopicCommentRoot.get(ForumTopicComment_.forumTopic), topicId));
		}

		criteriaQuery.where(restriction);

		TypedQuery<ForumTopicComment> forumTopicCommentQuery = entityManagerFactory.createQuery(criteriaQuery);
		return forumTopicCommentQuery.getResultList();
	}

	@Override
	public Path<String> getSortPathForSearchForumTopic(SortCondition sortDTO, Root<ForumTopic> forumTopicRoot) {

		List<String> forumTopicColList = new ArrayList<String>();
		forumTopicColList.add(SortConstants.FORUM_TOPIC_NAME);
		forumTopicColList.add(SortConstants.FORUM_TOPIC_AUTHOR);
		forumTopicColList.add(SortConstants.FORUM_TOPIC_STATUS);
		forumTopicColList.add(SortConstants.FORUM_TOPIC_CREATION_DATE);

		Path<String> sortPath = null;

		if (forumTopicColList.contains(sortDTO.getColumnName())) {
			sortPath = forumTopicRoot.get(colMap.get(ForumTopic.class + sortDTO.getColumnName()));
		}
		return sortPath;
	}

	@Override
	public void deleteForumTopic(ForumTopic forumTopic) {
		super.delete(forumTopic);
	}

	@Override
	public ForumTopic saveReturn(ForumTopic forumTopic) {
		ForumTopic persistObj = forumTopic;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (ForumTopic) getBaseEntity();
			beanUtil.copyProperties(persistObj, forumTopic);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;
	}

	@Override
	public List<ForumTopic> findDiscussionBoardTopicsByName(Long companyId, DiscussionBoardConditionDTO conditionDTO) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ForumTopic> criteriaQuery = cb.createQuery(ForumTopic.class);
		Root<ForumTopic> forumTopicRoot = criteriaQuery.from(ForumTopic.class);
		criteriaQuery.select(forumTopicRoot);
		Predicate restriction = cb.conjunction();

		if (conditionDTO.getCompanyId() != 0L) {
			Join<ForumTopic, Company> forumTopicCompanyJoin = forumTopicRoot.join(ForumTopic_.company);
			restriction = cb.and(restriction,
					cb.equal(forumTopicCompanyJoin.get(Company_.companyId), conditionDTO.getCompanyId()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getTopicName())) {
			restriction = cb.and(restriction, cb.equal(cb.upper(forumTopicRoot.get(ForumTopic_.topicName)),
					conditionDTO.getTopicName().toUpperCase()));
		}
		if (conditionDTO.getTopicId() != null && conditionDTO.getTopicId() != 0L) {
			restriction = cb.and(restriction,
					cb.notEqual(forumTopicRoot.get(ForumTopic_.topicId), conditionDTO.getTopicId()));
		}
		criteriaQuery.where(restriction);

		TypedQuery<ForumTopic> forumTopicQuery = entityManagerFactory.createQuery(criteriaQuery);

		return forumTopicQuery.getResultList();
	}

	@Override
	public List<ForumTopic> getMultipleTopicList(Long companyId, List<Long> topicIdList) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ForumTopic> criteriaQuery = cb.createQuery(ForumTopic.class);
		Root<ForumTopic> forumTopicRoot = criteriaQuery.from(ForumTopic.class);
		criteriaQuery.select(forumTopicRoot);
		Predicate restriction = cb.conjunction();

		Join<ForumTopic, Company> forumTopicCompanyJoin = forumTopicRoot.join(ForumTopic_.company);

		restriction = cb.and(restriction, cb.equal(forumTopicCompanyJoin.get(Company_.companyId), companyId));
		restriction = cb.and(restriction, forumTopicRoot.get(ForumTopic_.topicId).in(topicIdList));

		criteriaQuery.where(restriction);

		TypedQuery<ForumTopic> forumTopicQuery = entityManagerFactory.createQuery(criteriaQuery);

		return forumTopicQuery.getResultList();
	}

	public ForumTopic findById(Long forumTopicId, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ForumTopic> criteriaQuery = cb.createQuery(ForumTopic.class);
		Root<ForumTopic> forumTopicRoot = criteriaQuery.from(ForumTopic.class);
		criteriaQuery.select(forumTopicRoot);
		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(forumTopicRoot.get(ForumTopic_.company).get(Company_.companyId), companyId));
		restriction = cb.and(restriction, cb.equal(forumTopicRoot.get(ForumTopic_.topicId), forumTopicId));

		criteriaQuery.where(restriction);

		TypedQuery<ForumTopic> formTopicQuery = entityManagerFactory.createQuery(criteriaQuery);

		if(formTopicQuery.getResultList()!=null && formTopicQuery.getResultList().size()>0){
			return formTopicQuery.getSingleResult();	
		}
		return null;
	}

	@Override
	public List<Integer> getYearList(Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<ForumTopic> root = criteriaQuery.from(ForumTopic.class);
		criteriaQuery.select(cb.function("year", Integer.class, root.get(ForumTopic_.createdDate))).distinct(true);

		Join<ForumTopic, Company> companyJoin = root.join(ForumTopic_.company);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal((companyJoin.get(Company_.companyId)), companyId));

		criteriaQuery.where(restriction);
		TypedQuery<Integer> yearTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<Integer> yearList = yearTypedQuery.getResultList();
		return yearList;
	}

}
