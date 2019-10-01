package com.payasia.dao;

import java.util.List;

import com.payasia.common.dto.DiscussionBoardConditionDTO;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.ForumTopicStatusChangeHistory;

public interface ForumTopicStatusChangeHistoryDAO {

	void save(ForumTopicStatusChangeHistory forumTopicStatusChangeHistory);

	List<ForumTopicStatusChangeHistory> getDiscussionBoardTopicStatusHistory(
			Long employeeId, Long companyId, SortCondition sortDTO,
			DiscussionBoardConditionDTO discussionBoardConditionDTO);
}
