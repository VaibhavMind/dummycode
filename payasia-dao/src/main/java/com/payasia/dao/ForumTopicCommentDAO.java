package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.ForumTopicComment;

public interface ForumTopicCommentDAO {

	void save(ForumTopicComment forumTopicComment);

	ForumTopicComment findById(Long forumTopicCommentId);

	void update(ForumTopicComment forumTopicComment);

	void delete(ForumTopicComment forumTopicComment);

	ForumTopicComment saveReturn(ForumTopicComment forumTopicComment);

	List<ForumTopicComment> getTopicCommentList(Long topicId, Long companyId);

	List<ForumTopicComment> getTopicCommentListAscOrder(Long topicId,
			Long companyId);
	
	ForumTopicComment findByForumTopicCommentId(Long forumTopicCommentId,Long companyId);

}
