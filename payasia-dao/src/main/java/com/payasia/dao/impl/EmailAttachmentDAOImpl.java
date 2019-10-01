package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;

import org.springframework.stereotype.Repository;

import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.EmailAttachmentDAO;
import com.payasia.dao.bean.EmailAttachment;

@Repository
public class EmailAttachmentDAOImpl extends BaseDAO implements
		EmailAttachmentDAO {

	@Override
	protected Object getBaseEntity() {
		EmailAttachment emailAttachment = new EmailAttachment();
		return emailAttachment;
	}

	@Override
	public EmailAttachment saveReturn(EmailAttachment emailAttachment) {

		EmailAttachment persistObj = emailAttachment;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (EmailAttachment) getBaseEntity();
			beanUtil.copyProperties(persistObj, emailAttachment);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;
	}

	@Override
	public void update(EmailAttachment emailAttachment) {
		super.update(emailAttachment);
	}

	@Override
	public void delete(EmailAttachment emailAttachment) {
		super.delete(emailAttachment);
	}

	@Override
	public EmailAttachment findById(long emailAttachmentId) {

		EmailAttachment emailAttachment = super.findById(EmailAttachment.class,
				emailAttachmentId);
		return emailAttachment;
	}

}
