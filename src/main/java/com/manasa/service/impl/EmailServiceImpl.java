package com.manasa.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.manasa.model.Subscription;
import com.manasa.service.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Override
    public void sendRenewalReminder(Subscription subscription, long daysLeft) {
        String recipientEmail = subscription.getUser().getEmail();
        String userName = subscription.getUser().getFullName();

        String dueText = (daysLeft == 0) ? "TODAY" : (daysLeft < 0 ? "OVERDUE" : "in " + daysLeft + " day(s)");

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(senderEmail);
        mailMessage.setTo(recipientEmail);
        mailMessage.setSubject("BillReminder Alert: " + subscription.getName() + " is due " + dueText);
        mailMessage.setText(
                "Hi " + userName + ",\n\n" +
                "This is an automated reminder from BillReminder.\n\n" +
                "Subscription / Bill: " + subscription.getName() + "\n" +
                "Category: " + subscription.getCategory() + "\n" +
                "Amount: ₹" + subscription.getAmount() + "\n" +
                "Billing Cycle: " + subscription.getBillingCycle() + "\n" +
                "Next Due Date: " + subscription.getNextBillingDate() + " (" + dueText + ")\n\n" +
                "Log in to your BillReminder dashboard to mark it as paid once completed.\n\n" +
                "— BillReminder Automated System"
        );

        javaMailSender.send(mailMessage);
        log.info("Renewal reminder email sent to {} for subscription '{}'", recipientEmail, subscription.getName());
    }
}