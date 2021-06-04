package com.ehealthcaremanagement.services;

import com.ehealthcaremanagement.models.repository.AppointmentModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.config.ConfigTreeConfigDataLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailSenderService {

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private TemplateEngine templateEngine;

    public void sendSimpleMail(String toEmail, String body, String subject) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("ehealthcare8278@gmail.com");
        message.setTo(toEmail);
        message.setText(body);
        message.setSubject(subject);

        mailSender.send(message);
    }

    public void sendWelcomeMail(String toEmail, String name, String username, String password,
                                boolean isDoctor, boolean isDefaultPass) throws MessagingException
    {
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("username", username);
        context.setVariable("doctor", isDoctor);
        context.setVariable("password", password);
        context.setVariable("defaultPass", isDefaultPass);

        String process = templateEngine.process("welcome", context);

        htmlMailSender(toEmail, process, "Welcome to E-Healthcare, " + name);
    }

    public void sendOtpMail(String toEmail, String name, String username, int otp) throws MessagingException {
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("username", username);
        context.setVariable("otp", otp);

        String process = templateEngine.process("otp", context);

        htmlMailSender(toEmail, process, "OTP for resetting password");
    }

    public void sendPasswordChangeMail(String toEmail, String name, String username) throws MessagingException {
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("username", username);

        String process = templateEngine.process("password-change", context);

        htmlMailSender(toEmail, process, "Password Changed");
    }

    public void sendAppointmentEmail(AppointmentModel appointmentModel, boolean isTokenGeneration) throws MessagingException {
        String subject = (isTokenGeneration)?"Appointment Remainder":"Appointment Scheduled";
        Context context = new Context();
        context.setVariable("appointment", appointmentModel);
        context.setVariable("isTokenGeneration", isTokenGeneration);

        String process = templateEngine.process("appointment", context);
        htmlMailSender(appointmentModel.getUserId().getEmail(), process, subject);
    }

    // Private Methods
    private void htmlMailSender(String toEmail, String process, String subject) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        helper.setSubject(subject);
        helper.setText(process, true);
        helper.setTo(toEmail);
        mailSender.send(mimeMessage);
    }
}
