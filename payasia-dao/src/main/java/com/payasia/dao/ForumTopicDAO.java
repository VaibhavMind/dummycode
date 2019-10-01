package com.payasia.dao;

import java.util.List;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import com.payasia.common.dto.DiscussionBoardConditionDTO;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.ForumTopic;
import com.payasia.dao.bean.ForumTopicComment;

public interface ForumTopicDAO {

	void updateForumTopic(ForumTopic forumTopic);

	void deleteForumTopic(ForumTopic forumTopic);

	ForumTopic findById(Long forumTopicId);

	List<ForumTopic> findDiscussionBoardTopics(Long employeeId, Long companyId,
			SortCondition sortDTO,
			DiscussionBoardConditionDTO discussionBoardConditionDTO);

	List<ForumTopicComment> getDiscussionBoardTopicComments(Long companyId,
			Long topicId);

	Path<String> getSortPathForSearchForumTopic(SortCondition sortDTO,
			Root<ForumTopic> forumTopicRoot);

	ForumTopic saveReturn(ForumTopic forumTopic);

	List<ForumTopic> findDiscussionBoardTopicsByName(Long companyId,
			DiscussionBoardConditionDTO discussionBoardConditionDTO);

	List<ForumTopic> getMultipleTopicList(Long companyId, List<Long> topicIdList);

	/**
	 * This method restricts the result to the company the user is logged in
	 * with
	 * 
	 * @param forumTopicId
	 * @param companyId
	 * @return ForumTopic
	 */

	ForumTopic findById(Long forumTopicId, Long companyId);

	List<Integer> getYearList(Long companyId);

}
