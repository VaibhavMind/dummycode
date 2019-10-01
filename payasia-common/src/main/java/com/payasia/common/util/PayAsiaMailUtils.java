/*
 * author by vivek jain
 */
package com.payasia.common.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;
import org.springframework.ui.velocity.VelocityEngineUtils;

import com.payasia.common.dto.EmailDTO;


/**
 * The Class PayAsiaMailUtils.
 */
@Component
public class PayAsiaMailUtils {
	/** The Constant logger. */
	private static final Logger LOGGER = Logger.getLogger(PayAsiaMailUtils.class);
	/** The payasia temp path. */
	@Value("#{payasiaptProperties['payasia.mail.prefix.subject']}")
	private String PAYASIA_MAIL_PREFIX_SUBJECT;
	
	@Value("#{payasiaptProperties['payasia.mail.sender']}")
	private String PAYASIA_MAIL_SENDER;

	/** The mail sender. */
	private JavaMailSenderImpl mailSender;
	
	/** The mail sender. */
	private JavaMailSenderImpl mailSenderSendGrid;

	/** The velocity engine. */
	private VelocityEngine velocityEngine;

	/**
	 * Sets the mail sender.
	 * 
	 * @param mailSender
	 *            the new mail sender
	 */
	public void setMailSender(JavaMailSenderImpl mailSender) {
		this.mailSender = mailSender;
	}


	/**
	 * Sets the mail sender.
	 * 
	 * @param mailSenderSendGrid
	 *            the new mail sender
	 */

	public void setMailSenderSendGrid(JavaMailSenderImpl mailSenderSendGrid) {
		this.mailSenderSendGrid = mailSenderSendGrid;
	}



	/**
	 * Sets the velocity engine.
	 * 
	 * @param velocityEngine
	 *            the new velocity engine
	 */
	public void setVelocityEngine(VelocityEngine velocityEngine) {
		this.velocityEngine = velocityEngine;
	}

	/**
	 * Send email.
	 * 
	 * @param hasAttachment
	 *            the has attachment
	 * @param payAsiaEmailTO
	 *            the pay asia email to
	 */
	public void sendEmail(final boolean hasAttachment,
			final PayAsiaEmailTO payAsiaEmailTO) {
		MimeMessagePreparator preparator = new MimeMessagePreparator() {
			@Override
			public void prepare(MimeMessage mimeMessage) throws Exception {
				mimeMessage.setSender(new InternetAddress(PAYASIA_MAIL_SENDER));
				
				InternetAddress replyTo[] = new InternetAddress[1];
				replyTo[0] = new InternetAddress(payAsiaEmailTO.getMailFrom());
				mimeMessage.setReplyTo(replyTo);
				
				MimeMessageHelper message;
				if (hasAttachment) {
					message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
				} else {
					message = new MimeMessageHelper(mimeMessage, false, "UTF-8");
				}

				for (String to : payAsiaEmailTO.getMailTo()) {
					message.addTo(to);
				}
				List<String> ccEmailsList = removeDuplicateCCEmails(payAsiaEmailTO.getMailCc());
				for (String cc : ccEmailsList) {
					message.addCc(cc);
				}
				for (String bcc : payAsiaEmailTO.getMailBcc()) {
					message.addBcc(bcc);
				}
				for (PayAsiaAttachmentUtils mailAttachment : payAsiaEmailTO
						.getMailAttachments()) {
					message.addAttachment(mailAttachment.getFileName(),
							mailAttachment.getFile());
				}
				message.setReplyTo(payAsiaEmailTO.getMailFrom());
				message.setFrom(payAsiaEmailTO.getMailFrom());
				message.setText(payAsiaEmailTO.getMailText(), false);
				message.setSubject(payAsiaEmailTO.getMailSubject());
			}
		};
		
		String mailFrom=payAsiaEmailTO.getMailFrom();
		mailFrom = mailFrom.substring(mailFrom.indexOf("@")+1, mailFrom.length());
		
		if(mailFrom.equalsIgnoreCase(PayAsiaConstants.PAYASIA_DOMAIN_PAYASIA_ASIA)||mailFrom.equalsIgnoreCase(PayAsiaConstants.PAYASIA_DOMAIN_PAYBHARATH_ASIA)||mailFrom.equalsIgnoreCase(PayAsiaConstants.PAYASIA_DOMAIN_PAYBHARAT_ASIA)){
			this.mailSender.send(preparator);
		}else{
			this.mailSenderSendGrid.send(preparator);
		}
		
		
	}
	

	/**
	 * Send email.
	 * 
	 * @param model
	 *            the model
	 * @param templateString
	 *            the template string
	 * @param hasAttachment
	 *            the has attachment
	 * @param payAsiaEmailTO
	 *            the pay asia email to
	 */
	public void sendEmail(final Map<String, Object> model,
			final String templateString,final String templateStringSubject, final boolean hasAttachment,
			final PayAsiaEmailTO payAsiaEmailTO) {
		MimeMessagePreparator preparator = new MimeMessagePreparator() {
			@Override
			public void prepare(MimeMessage mimeMessage) throws Exception {
				mimeMessage.setSender(new InternetAddress(PAYASIA_MAIL_SENDER));
				
				InternetAddress replyTo[] = new InternetAddress[1];
				replyTo[0] = new InternetAddress(payAsiaEmailTO.getMailFrom());
				mimeMessage.setReplyTo(replyTo);
				
				MimeMessageHelper message;
				if (hasAttachment) {
					message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
				} else {
					message = new MimeMessageHelper(mimeMessage, false, "UTF-8");
				}
				for (String to : payAsiaEmailTO.getMailTo()) {
					message.addTo(to);
				}
				List<String> ccEmailsList = removeDuplicateCCEmails(payAsiaEmailTO.getMailCc());
				for (String cc : ccEmailsList) {
					message.addCc(cc);
				}
				for (String bcc : payAsiaEmailTO.getMailBcc()) {
					message.addBcc(bcc);
				}
				for (PayAsiaAttachmentUtils mailAttachment : payAsiaEmailTO
						.getMailAttachments()) {
					message.addAttachment(mailAttachment.getFileName(),
							mailAttachment.getFile());
				}

				message.setFrom(payAsiaEmailTO.getMailFrom());

				String mailSubject = "";
				if (StringUtils.isNotBlank(PAYASIA_MAIL_PREFIX_SUBJECT)) {
					mailSubject += PAYASIA_MAIL_PREFIX_SUBJECT + " ";
				}
				mailSubject += payAsiaEmailTO.getMailSubject();
				StringBuilder subjectText = new StringBuilder("");
				subjectText.append(VelocityEngineUtils.mergeTemplateIntoString(
						velocityEngine, templateStringSubject, "UTF-8", model));
				subjectText.append("");
				message.setSubject(subjectText.toString());

				StringBuilder text = new StringBuilder("");

				text.append(VelocityEngineUtils.mergeTemplateIntoString(
						velocityEngine, templateString, "UTF-8", model));
				text.append("");
				message.setText(text.toString(), true);

			}
		};
		String mailFrom=payAsiaEmailTO.getMailFrom();
		mailFrom = mailFrom.substring(mailFrom.indexOf("@")+1, mailFrom.length());
		
		if(mailFrom.equalsIgnoreCase(PayAsiaConstants.PAYASIA_DOMAIN_PAYASIA_ASIA)||mailFrom.equalsIgnoreCase(PayAsiaConstants.PAYASIA_DOMAIN_PAYBHARATH_ASIA)||mailFrom.equalsIgnoreCase(PayAsiaConstants.PAYASIA_DOMAIN_PAYBHARAT_ASIA)){
			this.mailSender.send(preparator);
		}else{
			this.mailSenderSendGrid.send(preparator);
		}
	}

	/**
	 * Send email .
	 * 
	 * @param model
	 *            the model
	 * @param templateString
	 *            the template string
	 * @param hasAttachment
	 *            the has attachment
	 * @param payAsiaEmailTO
	 *            the pay asia email to
	 */
	public void sendEmailForLeave(final Map<String, Object> model,
			final String templateString, final String templateStringSubject,
			final boolean hasAttachment, final PayAsiaEmailTO payAsiaEmailTO) {
		MimeMessagePreparator preparator = new MimeMessagePreparator() {
			@Override
			public void prepare(MimeMessage mimeMessage) throws Exception {
				mimeMessage.setSender(new InternetAddress(PAYASIA_MAIL_SENDER));
				
				InternetAddress replyTo[] = new InternetAddress[1];
				replyTo[0] = new InternetAddress(payAsiaEmailTO.getMailFrom());
				mimeMessage.setReplyTo(replyTo);
				
				MimeMessageHelper message;
				if (hasAttachment) {
					message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
				} else {
					message = new MimeMessageHelper(mimeMessage, false, "UTF-8");
				}
				for (String to : payAsiaEmailTO.getMailTo()) {
					message.addTo(to);
				}
				List<String> ccEmailsList = removeDuplicateCCEmails(payAsiaEmailTO.getMailCc());
				for (String cc : ccEmailsList) {
					message.addCc(cc);
				}
				for (String bcc : payAsiaEmailTO.getMailBcc()) {
					message.addBcc(bcc);
				}
				for (PayAsiaAttachmentUtils mailAttachment : payAsiaEmailTO
						.getMailAttachments()) {
					message.addAttachment(mailAttachment.getFileName(),
							mailAttachment.getFile());
				}

				message.setFrom(payAsiaEmailTO.getMailFrom());

				String mailSubject = "";
				if (StringUtils.isNotBlank(PAYASIA_MAIL_PREFIX_SUBJECT)) {
					mailSubject += PAYASIA_MAIL_PREFIX_SUBJECT + " ";
				}
				mailSubject += payAsiaEmailTO.getMailSubject();

				StringBuilder subjectText = new StringBuilder("");

				subjectText.append(VelocityEngineUtils.mergeTemplateIntoString(
						velocityEngine, templateStringSubject, "UTF-8", model));
				subjectText.append("");

				message.setSubject(subjectText.toString());

				StringBuilder text = new StringBuilder("");

				text.append(VelocityEngineUtils.mergeTemplateIntoString(
						velocityEngine, templateString, "UTF-8", model));
				text.append("");
				message.setText(text.toString(), true);

			}
		};
		String mailFrom=payAsiaEmailTO.getMailFrom();
		mailFrom = mailFrom.substring(mailFrom.indexOf("@")+1, mailFrom.length());
		
		if(mailFrom.equalsIgnoreCase(PayAsiaConstants.PAYASIA_DOMAIN_PAYASIA_ASIA)||mailFrom.equalsIgnoreCase(PayAsiaConstants.PAYASIA_DOMAIN_PAYBHARATH_ASIA)||mailFrom.equalsIgnoreCase(PayAsiaConstants.PAYASIA_DOMAIN_PAYBHARAT_ASIA)){
			this.mailSender.send(preparator);
		}else{
			this.mailSenderSendGrid.send(preparator);
		}
	}

	/**
	 * Send email .
	 * 
	 * @param model
	 *            the model
	 * @param templateString
	 *            the template string
	 * @param hasAttachment
	 *            the has attachment
	 * @param payAsiaEmailTO
	 *            the pay asia email to
	 */
	public void sendReminderEmail(final PayAsiaEmailTO payAsiaEmailTO,
			final Boolean hasAttachment) {
		MimeMessagePreparator preparator = new MimeMessagePreparator() {
			@Override
			public void prepare(MimeMessage mimeMessage) throws Exception {
				mimeMessage.setSender(new InternetAddress(PAYASIA_MAIL_SENDER));
				
				InternetAddress replyTo[] = new InternetAddress[1];
				replyTo[0] = new InternetAddress(payAsiaEmailTO.getMailFrom());
				mimeMessage.setReplyTo(replyTo);
				
				MimeMessageHelper message;
				if (hasAttachment) {
					message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
				} else {
					message = new MimeMessageHelper(mimeMessage, false, "UTF-8");
				}
				for (String to : payAsiaEmailTO.getMailTo()) {
					message.addTo(to);
				}
				List<String> ccEmailsList = removeDuplicateCCEmails(payAsiaEmailTO.getMailCc());
				for (String cc : ccEmailsList) {
					message.addCc(cc);
				}
				for (String bcc : payAsiaEmailTO.getMailBcc()) {
					message.addBcc(bcc);
				}
				for (PayAsiaAttachmentUtils mailAttachment : payAsiaEmailTO
						.getMailAttachments()) {
					message.addAttachment(mailAttachment.getFileName(),
							mailAttachment.getFile());
				}

				message.setFrom(payAsiaEmailTO.getMailFrom());

				message.setSubject(payAsiaEmailTO.getMailSubject());

				message.setText(payAsiaEmailTO.getMailText(), true);

			}
		};
		String mailFrom=payAsiaEmailTO.getMailFrom();
		mailFrom = mailFrom.substring(mailFrom.indexOf("@")+1, mailFrom.length());
		
		try{
		if(mailFrom.equalsIgnoreCase(PayAsiaConstants.PAYASIA_DOMAIN_PAYASIA_ASIA)||mailFrom.equalsIgnoreCase(PayAsiaConstants.PAYASIA_DOMAIN_PAYBHARATH_ASIA)||mailFrom.equalsIgnoreCase(PayAsiaConstants.PAYASIA_DOMAIN_PAYBHARAT_ASIA)){
			this.mailSender.send(preparator);
		}else{
			this.mailSenderSendGrid.send(preparator);
		}
		}catch(org.springframework.mail.MailParseException mpe){
			
		}
	}

	public void sendClaimEmail(final EmailDTO email) {
		MimeMessagePreparator preparator = new MimeMessagePreparator() {
			@Override
			public void prepare(MimeMessage mimeMessage) throws Exception {
				mimeMessage.setSender(new InternetAddress(PAYASIA_MAIL_SENDER));
				
				String templateStringSubject = email
						.getEmailSubject()
						.getPath()
						.substring(
								email.getEmailSubject().getParent().length() + 1);
				String templateString = email
						.getEmailBody()
						.getPath()
						.substring(
								email.getEmailBody().getParent().length() + 1);
				PayAsiaEmailTO payAsiaEmailTO = email.getPayAsiaEmailTO();
				MimeMessageHelper message;
				
				 if(email.getAttachment()!=null)
			       {
					 message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
						
			    	   message.addAttachment(email.getAttachment().getName(),email.getAttachment());
			       }
				 else
				 {
				message = new MimeMessageHelper(mimeMessage, false, "UTF-8");
				 }
				InternetAddress replyTo[] = new InternetAddress[1];
				replyTo[0] = new InternetAddress(payAsiaEmailTO.getMailFrom());
				mimeMessage.setReplyTo(replyTo);
				
				for (String to : payAsiaEmailTO.getMailTo()) {
					message.addTo(to);
				}
				List<String> ccEmailsList = removeDuplicateCCEmails(payAsiaEmailTO.getMailCc());
				for (String cc : ccEmailsList) {
					message.addCc(cc);
				}

				for (String bcc : payAsiaEmailTO.getMailBcc()) {
					message.addBcc(bcc);
				}
      
				message.setFrom(payAsiaEmailTO.getMailFrom());

				String mailSubject = "";
				if (StringUtils.isNotBlank(PAYASIA_MAIL_PREFIX_SUBJECT)) {
					mailSubject += PAYASIA_MAIL_PREFIX_SUBJECT + " ";
				}
				mailSubject += payAsiaEmailTO.getMailSubject();

				StringBuilder subjectText = new StringBuilder("");

				subjectText.append(VelocityEngineUtils.mergeTemplateIntoString(
						velocityEngine, templateStringSubject, "UTF-8",
						email.getEmailParamMap()));
				subjectText.append("");

				message.setSubject(subjectText.toString());

				StringBuilder text = new StringBuilder("");

				text.append(VelocityEngineUtils.mergeTemplateIntoString(
						velocityEngine, templateString, "UTF-8",
						email.getEmailParamMap()));
				text.append("");
				message.setText(text.toString(), true);

			}
		};
		String mailFrom=email.getPayAsiaEmailTO().getMailFrom();
		mailFrom = mailFrom.substring(mailFrom.indexOf("@")+1, mailFrom.length());
	if(email!=null && email.getPayAsiaEmailTO()!=null && !email.getPayAsiaEmailTO().getMailTo().isEmpty())
	{
		LOGGER.info("Email To"+email.getPayAsiaEmailTO().getMailTo().get(0)+ "Email From"+email.getPayAsiaEmailTO().getMailFrom());
	}
		if(mailFrom.equalsIgnoreCase(PayAsiaConstants.PAYASIA_DOMAIN_PAYASIA_ASIA)||mailFrom.equalsIgnoreCase(PayAsiaConstants.PAYASIA_DOMAIN_PAYBHARATH_ASIA)||mailFrom.equalsIgnoreCase(PayAsiaConstants.PAYASIA_DOMAIN_PAYBHARAT_ASIA)){
			this.mailSender.send(preparator);
		}else{
			this.mailSenderSendGrid.send(preparator);
		}

	}

	/**
	 * Send email .
	 * 
	 * @param model
	 *            the model
	 * @param templateString
	 *            the template string
	 * @param hasAttachment
	 *            the has attachment
	 * @param payAsiaEmailTO
	 *            the pay asia email to
	 */
	public void sendEmailForHRISDataChange(final Map<String, Object> model,
			final String templateString, final String templateStringSubject,
			final boolean hasAttachment, final PayAsiaEmailTO payAsiaEmailTO) {
		MimeMessagePreparator preparator = new MimeMessagePreparator() {
			@Override
			public void prepare(MimeMessage mimeMessage) throws Exception {
				mimeMessage.setSender(new InternetAddress(PAYASIA_MAIL_SENDER));
				
				InternetAddress replyTo[] = new InternetAddress[1];
				replyTo[0] = new InternetAddress(payAsiaEmailTO.getMailFrom());
				mimeMessage.setReplyTo(replyTo);
				
				MimeMessageHelper message;
				if (hasAttachment) {
					message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
				} else {
					message = new MimeMessageHelper(mimeMessage, false, "UTF-8");
				}
				for (String to : payAsiaEmailTO.getMailTo()) {
					message.addTo(to);
				}
				List<String> ccEmailsList = removeDuplicateCCEmails(payAsiaEmailTO.getMailCc());
				for (String cc : ccEmailsList) {
					message.addCc(cc);
				}
				for (String bcc : payAsiaEmailTO.getMailBcc()) {
					message.addBcc(bcc);
				}
				for (PayAsiaAttachmentUtils mailAttachment : payAsiaEmailTO
						.getMailAttachments()) {
					message.addAttachment(mailAttachment.getFileName(),
							mailAttachment.getFile());
				}

				message.setFrom(payAsiaEmailTO.getMailFrom());

				String mailSubject = "";
				if (StringUtils.isNotBlank(PAYASIA_MAIL_PREFIX_SUBJECT)) {
					mailSubject += PAYASIA_MAIL_PREFIX_SUBJECT + " ";
				}
				mailSubject += payAsiaEmailTO.getMailSubject();

				StringBuilder subjectText = new StringBuilder("");

				subjectText.append(VelocityEngineUtils.mergeTemplateIntoString(
						velocityEngine, templateStringSubject, "UTF-8", model));
				subjectText.append("");

				message.setSubject(subjectText.toString());

				StringBuilder text = new StringBuilder("");

				text.append(VelocityEngineUtils.mergeTemplateIntoString(
						velocityEngine, templateString, "UTF-8", model));
				text.append("");
				message.setText(text.toString(), true);

			}
		};
		String mailFrom=payAsiaEmailTO.getMailFrom();
		mailFrom = mailFrom.substring(mailFrom.indexOf("@")+1, mailFrom.length());
		
		if(mailFrom.equalsIgnoreCase(PayAsiaConstants.PAYASIA_DOMAIN_PAYASIA_ASIA)||mailFrom.equalsIgnoreCase(PayAsiaConstants.PAYASIA_DOMAIN_PAYBHARATH_ASIA)||mailFrom.equalsIgnoreCase(PayAsiaConstants.PAYASIA_DOMAIN_PAYBHARAT_ASIA)){
			this.mailSender.send(preparator);
		}else{
			this.mailSenderSendGrid.send(preparator);
		}
	}

	public String getHRLetterEmailConvertedBodyText(
			final Map<String, Object> model, final String templateString,
			final PayAsiaEmailTO payAsiaEmailTO) {
		String convertedMessage = VelocityEngineUtils.mergeTemplateIntoString(
				velocityEngine, templateString, "UTF-8", model);
		return convertedMessage;

	}

	/**
	 * send Email For HRLetter.
	 * 
	 * @param model
	 *            the model
	 * @param templateString
	 *            the template string
	 * @param hasAttachment
	 *            the has attachment
	 * @param payAsiaEmailTO
	 *            the pay asia email to
	 */
	public void sendEmailForHRLetter(final Map<String, Object> model,
			final String templateString, final boolean hasAttachment,
			final PayAsiaEmailTO payAsiaEmailTO) {
		MimeMessagePreparator preparator = new MimeMessagePreparator() {
			@Override
			public void prepare(MimeMessage mimeMessage) throws Exception {
				mimeMessage.setSender(new InternetAddress(PAYASIA_MAIL_SENDER));
				
				InternetAddress replyTo[] = new InternetAddress[1];
				replyTo[0] = new InternetAddress(payAsiaEmailTO.getMailFrom());
				mimeMessage.setReplyTo(replyTo);
				
				MimeMessageHelper message;
				if (hasAttachment) {
					message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
				} else {
					message = new MimeMessageHelper(mimeMessage, false, "UTF-8");
				}
				for (String to : payAsiaEmailTO.getMailTo()) {
					message.addTo(to);
				}
				List<String> ccEmailsList = removeDuplicateCCEmails(payAsiaEmailTO.getMailCc());
				for (String cc : ccEmailsList) {
					message.addCc(cc);
				}
				for (String bcc : payAsiaEmailTO.getMailBcc()) {
					message.addBcc(bcc);
				}
				for (PayAsiaAttachmentUtils mailAttachment : payAsiaEmailTO
						.getMailAttachments()) {
					message.addAttachment(mailAttachment.getFileName(),
							mailAttachment.getFile());
				}
				message.setFrom(payAsiaEmailTO.getMailFrom());
				message.setSubject(payAsiaEmailTO.getMailSubject());

				StringBuilder text = new StringBuilder("");
				text.append(VelocityEngineUtils.mergeTemplateIntoString(
						velocityEngine, templateString, "UTF-8", model));
				text.append("");
				message.setText(text.toString(), true);

			}
		};
		String mailFrom=payAsiaEmailTO.getMailFrom();
		mailFrom = mailFrom.substring(mailFrom.indexOf("@")+1, mailFrom.length());
		
		if(mailFrom.equalsIgnoreCase(PayAsiaConstants.PAYASIA_DOMAIN_PAYASIA_ASIA)||mailFrom.equalsIgnoreCase(PayAsiaConstants.PAYASIA_DOMAIN_PAYBHARATH_ASIA)||mailFrom.equalsIgnoreCase(PayAsiaConstants.PAYASIA_DOMAIN_PAYBHARAT_ASIA)){
			this.mailSender.send(preparator);
		}else{
			this.mailSenderSendGrid.send(preparator);
		}
	}
	/**
	 * Send email for OT Timesheet.
	 * 
	 * @param model
	 *            the model
	 * @param templateString
	 *            the template string
	 * @param hasAttachment
	 *            the has attachment
	 * @param payAsiaEmailTO
	 *            the pay asia email to
	 */
	public void sendEmailForOTTimesheet(final Map<String, Object> model,
			final String templateString, final String templateStringSubject,
			final boolean hasAttachment, final PayAsiaEmailTO payAsiaEmailTO) {
		MimeMessagePreparator preparator = new MimeMessagePreparator() {
			@Override
			public void prepare(MimeMessage mimeMessage) throws Exception {
				mimeMessage.setSender(new InternetAddress(PAYASIA_MAIL_SENDER));
				
				InternetAddress replyTo[] = new InternetAddress[1];
				replyTo[0] = new InternetAddress(payAsiaEmailTO.getMailFrom());
				mimeMessage.setReplyTo(replyTo);
				
				MimeMessageHelper message;
				if (hasAttachment) {
					message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
				} else {
					message = new MimeMessageHelper(mimeMessage, false, "UTF-8");
				}
				for (String to : payAsiaEmailTO.getMailTo()) {
					message.addTo(to);
				}
				List<String> ccEmailsList = removeDuplicateCCEmails(payAsiaEmailTO.getMailCc());
				for (String cc : ccEmailsList) {
					message.addCc(cc);
				}
				for (String bcc : payAsiaEmailTO.getMailBcc()) {
					message.addBcc(bcc);
				}
				for (PayAsiaAttachmentUtils mailAttachment : payAsiaEmailTO
						.getMailAttachments()) {
					message.addAttachment(mailAttachment.getFileName(),
							mailAttachment.getFile());
				}

				message.setFrom(payAsiaEmailTO.getMailFrom());

				String mailSubject = "";
				if (StringUtils.isNotBlank(PAYASIA_MAIL_PREFIX_SUBJECT)) {
					mailSubject += PAYASIA_MAIL_PREFIX_SUBJECT + " ";
				}
				mailSubject += payAsiaEmailTO.getMailSubject();

				StringBuilder subjectText = new StringBuilder("");

				subjectText.append(VelocityEngineUtils.mergeTemplateIntoString(
						velocityEngine, templateStringSubject, "UTF-8", model));
				subjectText.append("");

				message.setSubject(subjectText.toString());

				StringBuilder text = new StringBuilder("");

				text.append(VelocityEngineUtils.mergeTemplateIntoString(
						velocityEngine, templateString, "UTF-8", model));
				text.append("");
				message.setText(text.toString(), true);

			}
		};
		String mailFrom=payAsiaEmailTO.getMailFrom();
		mailFrom = mailFrom.substring(mailFrom.indexOf("@")+1, mailFrom.length());
		
		if(mailFrom.equalsIgnoreCase(PayAsiaConstants.PAYASIA_DOMAIN_PAYASIA_ASIA)||mailFrom.equalsIgnoreCase(PayAsiaConstants.PAYASIA_DOMAIN_PAYBHARATH_ASIA)||mailFrom.equalsIgnoreCase(PayAsiaConstants.PAYASIA_DOMAIN_PAYBHARAT_ASIA)){
			this.mailSender.send(preparator);
		}else{
			this.mailSenderSendGrid.send(preparator);
		}
	}
	
	public void sendEmailForLionTimesheet(final Map<String, Object> model,
			final String templateString, final String templateStringSubject,
			final boolean hasAttachment, final PayAsiaEmailTO payAsiaEmailTO) {
		MimeMessagePreparator preparator = new MimeMessagePreparator() {
			@Override
			public void prepare(MimeMessage mimeMessage) throws Exception {
				mimeMessage.setSender(new InternetAddress(PAYASIA_MAIL_SENDER));
				
				InternetAddress replyTo[] = new InternetAddress[1];
				replyTo[0] = new InternetAddress(payAsiaEmailTO.getMailFrom());
				mimeMessage.setReplyTo(replyTo);
				
				MimeMessageHelper message;
				if (hasAttachment) {
					message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
				} else {
					message = new MimeMessageHelper(mimeMessage, false, "UTF-8");
				}
				for (String to : payAsiaEmailTO.getMailTo()) {
					message.addTo(to);
				}
				List<String> ccEmailsList = removeDuplicateCCEmails(payAsiaEmailTO.getMailCc());
				for (String cc : ccEmailsList) {
					message.addCc(cc);
				}
				for (String bcc : payAsiaEmailTO.getMailBcc()) {
					message.addBcc(bcc);
				}
				for (PayAsiaAttachmentUtils mailAttachment : payAsiaEmailTO
						.getMailAttachments()) {
					message.addAttachment(mailAttachment.getFileName(),
							mailAttachment.getFile());
				}

				message.setFrom(payAsiaEmailTO.getMailFrom());

				String mailSubject = "";
				if (StringUtils.isNotBlank(PAYASIA_MAIL_PREFIX_SUBJECT)) {
					mailSubject += PAYASIA_MAIL_PREFIX_SUBJECT + " ";
				}
				mailSubject += payAsiaEmailTO.getMailSubject();

				StringBuilder subjectText = new StringBuilder("");

				subjectText.append(VelocityEngineUtils.mergeTemplateIntoString(
						velocityEngine, templateStringSubject, "UTF-8", model));
				subjectText.append("");

				message.setSubject(subjectText.toString());

				StringBuilder text = new StringBuilder("");

				text.append(VelocityEngineUtils.mergeTemplateIntoString(
						velocityEngine, templateString, "UTF-8", model));
				text.append("");
				message.setText(text.toString(), true);

			}
		};
		String mailFrom=payAsiaEmailTO.getMailFrom();
		mailFrom = mailFrom.substring(mailFrom.indexOf("@")+1, mailFrom.length());
		
		if(mailFrom.equalsIgnoreCase(PayAsiaConstants.PAYASIA_DOMAIN_PAYASIA_ASIA)||mailFrom.equalsIgnoreCase(PayAsiaConstants.PAYASIA_DOMAIN_PAYBHARATH_ASIA)||mailFrom.equalsIgnoreCase(PayAsiaConstants.PAYASIA_DOMAIN_PAYBHARAT_ASIA)){
			this.mailSender.send(preparator);
		}else{
			this.mailSenderSendGrid.send(preparator);
		}
	}
	
	//Remove Duplicate CC Emails from given List of emails which include Duplicate CC Emails
		private List<String> removeDuplicateCCEmails(List<String> ccEmailsList){
			Set<String> ccEmailsSet = new HashSet<>(ccEmailsList);
			List<String> ccEmailModifiedList = new ArrayList<>(ccEmailsSet);
			return ccEmailModifiedList;
		}
}
