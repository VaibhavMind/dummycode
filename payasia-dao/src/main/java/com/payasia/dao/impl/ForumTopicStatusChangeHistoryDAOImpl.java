package com.payasia.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.common.dto.DiscussionBoardConditionDTO;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.ForumTopicStatusChangeHistoryDAO;
import com.payasia.dao.bean.ForumTopicStatusChangeHistory;
import com.payasia.dao.bean.ForumTopicStatusChangeHistory_;

@Repository
public class ForumTopicStatusChangeHistoryDAOImpl extends BaseDAO implements
		ForumTopicStatusChangeHistoryDAO {

	@Override
	protected Object getBaseEntity() {
		ForumTopicStatusChangeHistory forumTopicStatusChangeHistory = new ForumTopicStatusChangeHistory();
		return forumTopicStatusChangeHistory;
	}

	@Override
	public void save(ForumTopicStatusChangeHistory forumTopicStatusChangeHistory) {
		super.save(forumTopicStatusChangeHistory);

	}

	@Override
	public List<ForumTopicStatusChangeHistory> getDiscussionBoardTopicStatusHistory(
			Long employeeId, Long companyId, SortCondition sortDTO,
			DiscussionBoardConditionDTO conditionDTO) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ForumTopicStatusChangeHistory> criteriaQuery = cb
				.createQuery(ForumTopicStatusChangeHistory.class);
		Root<ForumTopicStatusChangeHistory> forumTopicStatusChangeHistoryRoot = criteriaQuery
				.from(ForumTopicStatusChangeHistory.class);
		criteriaQuery.select(forumTopicStatusChangeHistoryRoot);
		Predicate restriction = cb.conjunction();

		if (conditionDTO.getTopicId() != 0L) {
			restriction = cb.and(restriction, cb.equal(
					forumTopicStatusChangeHistoryRoot
							.get(ForumTopicStatusChangeHistory_.forumTopic),
					conditionDTO.getTopicId()));
		}

		criteriaQuery.where(restriction);

		if (sortDTO != null) {
			criteriaQuery
					.orderBy(cb.asc(forumTopicStatusChangeHistoryRoot
							.get(ForumTopicStatusChangeHistory_.topicStatusChangeHistoryId)));
		}

		TypedQuery<ForumTopicStatusChangeHistory> forumTopicStatusChangeHistoryQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return forumTopicStatusChangeHistoryQuery.getResultList();
	}
}
