package com.payasia.dao;

import com.payasia.dao.bean.EmailAttachment;

public interface EmailAttachmentDAO {

	EmailAttachment saveReturn(EmailAttachment emailAttachment);

	void delete(EmailAttachment emailAttachment);

	void update(EmailAttachment emailAttachment);

	EmailAttachment findById(long emailAttachmentId);

}
