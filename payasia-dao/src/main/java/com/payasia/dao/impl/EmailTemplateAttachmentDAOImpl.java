package com.payasia.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.EmailTemplateAttachmentDAO;
import com.payasia.dao.bean.EmailTemplateAttachment;
import com.payasia.dao.bean.EmailTemplateAttachment_;


/**
 * The Class EmailTemplateAttachmentDAOImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Repository
public class EmailTemplateAttachmentDAOImpl extends BaseDAO implements
		EmailTemplateAttachmentDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmailTemplateAttachmentDAO#save(com.payasia.dao.bean.
	 * EmailTemplateAttachment)
	 */
	@Override
	public void save(EmailTemplateAttachment emailTemplateAttachment) {
		super.save(emailTemplateAttachment);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.BaseDAO#getBaseEntity()
	 */
	@Override
	protected Object getBaseEntity() {
		EmailTemplateAttachment emailTemplateAttachment = new EmailTemplateAttachment();
		return emailTemplateAttachment;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmailTemplateAttachmentDAO#findById(long)
	 */
	@Override
	public EmailTemplateAttachment findById(long attachmentId) {
		EmailTemplateAttachment attachment = super.findById(
				EmailTemplateAttachment.class, attachmentId);
		return attachment;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmailTemplateAttachmentDAO#update(com.payasia.dao.bean
	 * .EmailTemplateAttachment)
	 */
	@Override
	public void update(EmailTemplateAttachment emailTemplateAttachment) {
		super.update(emailTemplateAttachment);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmailTemplateAttachmentDAO#delete(com.payasia.dao.bean
	 * .EmailTemplateAttachment)
	 */
	@Override
	public void delete(EmailTemplateAttachment emailTemplateAttachment) {
		super.delete(emailTemplateAttachment);
	}

	@Override
	public EmailTemplateAttachment findById(long attachmentId, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmailTemplateAttachment> criteriaQuery = cb.createQuery(EmailTemplateAttachment.class);
		Root<EmailTemplateAttachment> templateRoot = criteriaQuery.from(EmailTemplateAttachment.class);
		criteriaQuery.select(templateRoot);

		Path<Long> companyID = templateRoot.get(EmailTemplateAttachment_.companyId);

		Path<Long> attachmentID = templateRoot.get(EmailTemplateAttachment_.emailAttachmentId);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(companyID, companyId));

		restriction = cb.and(restriction, cb.equal(attachmentID, attachmentId));

		criteriaQuery.where(restriction);

		TypedQuery<EmailTemplateAttachment> emailTemplateQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<EmailTemplateAttachment> emailTemplateList = emailTemplateQuery.getResultList();
		if (emailTemplateList != null && !emailTemplateList.isEmpty()) {
			return emailTemplateList.get(0);
		}
		return null;
	}

}
