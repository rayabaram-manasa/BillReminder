package com.manasa.service;

import com.manasa.model.Subscription;

public interface EmailService {
    void sendRenewalReminder(Subscription subscription, long daysLeft);
}
