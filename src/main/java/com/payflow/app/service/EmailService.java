package com.payflow.app.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

	@Value("${spring.mail.username}")
	private String from;

	@Value("${spring.mail.password}")
	private String password;

	private final TemplateEngine templateEngine;

	// Inject TemplateEngine through the constructor
	public EmailService(TemplateEngine templateEngine) {
		this.templateEngine = templateEngine;
	}

	// Method to send an email using a Thymeleaf template
	public void sendEmailWithTemplate(String to, String subject, String templateName, Context context) {
		String htmlBody = templateEngine.process(templateName, context);

		String host = "smtp.gmail.com";

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(from, password);
			}
		});

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
			message.setSubject(subject);

			message.setContent(htmlBody, "text/html; charset=utf-8");

			Transport.send(message);
			System.out.println("Email sent successfully using template: " + templateName);

		} catch (MessagingException e) {
			System.err.println("Failed to send email: " + e.getMessage());
			e.printStackTrace();
		}
	}
}