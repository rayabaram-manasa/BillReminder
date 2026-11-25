package com.manasa.scheduler;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.manasa.dao.SubscriptionRepository;
import com.manasa.dto.SubscriptionStatus;
import com.manasa.model.Subscription;
import com.manasa.service.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class RenewalReminderScheduler {

    private final SubscriptionRepository subscriptionRepository;
    private final EmailService emailService;

    // Runs every day at 9:00 AM
    @Scheduled(cron = "0 0 9 * * *")
    @Transactional
    public void checkAndSendReminders() {
        LocalDate today = LocalDate.now();
        log.info("Running scheduled renewal reminder check for date: {}", today);

        List<Subscription> activeSubscriptions = subscriptionRepository
                .findByStatusOrderByNextBillingDateAsc(SubscriptionStatus.ACTIVE);

        for (Subscription subscription : activeSubscriptions) {
            long daysUntilDue = ChronoUnit.DAYS.between(today, subscription.getNextBillingDate());

            boolean isWithinReminderWindow = daysUntilDue <= subscription.getReminderDaysBefore();
            boolean notRemindedToday = subscription.getLastRemindedDate() == null
                    || !subscription.getLastRemindedDate().equals(today);

            if (isWithinReminderWindow && notRemindedToday) {
                try {
                    emailService.sendRenewalReminder(subscription, daysUntilDue);
                    subscription.setLastRemindedDate(today);
                    subscriptionRepository.save(subscription);
                } catch (Exception exception) {
                    log.error("Failed to send reminder email for subscription ID {}: {}",
                            subscription.getId(), exception.getMessage());
                }
            }
        }
    }
}