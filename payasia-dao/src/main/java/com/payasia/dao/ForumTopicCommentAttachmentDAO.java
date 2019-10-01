package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.ForumTopicCommentAttachment;

public interface ForumTopicCommentAttachmentDAO {

	void save(ForumTopicCommentAttachment forumTopicCommentAttachment);

	ForumTopicCommentAttachment findById(Long forumTopicCommentAttachmentId);

	void delete(ForumTopicCommentAttachment forumTopicCommentAttachment);

	void update(ForumTopicCommentAttachment forumTopicCommentAttachment);

	ForumTopicCommentAttachment saveReturn(
			ForumTopicCommentAttachment forumTopicCommentAttachment);

	List<ForumTopicCommentAttachment> findByCommentId(long companyId,
			Long topicCommentId);

	void deleteByCondition(Long topicCommentId);

	List<ForumTopicCommentAttachment> findByAttachmentId(long companyId,
			List<String> AttachmentIds);

	List<ForumTopicCommentAttachment> findByCondition(long companyId,
			String attachmentName, Long topicCommentId);

	ForumTopicCommentAttachment findById(Long forumTopicCommentAttachmentId,
			Long companyId);

}
