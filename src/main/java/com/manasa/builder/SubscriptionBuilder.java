package com.manasa.builder;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Component;

import com.manasa.dto.SubscriptionRequestDTO;
import com.manasa.dto.SubscriptionResponseDTO;
import com.manasa.dto.SubscriptionStatus;
import com.manasa.model.Subscription;

@Component
public class SubscriptionBuilder {

	public Subscription toEntity(SubscriptionRequestDTO subscriptionRequestDTO) {
		
	return	Subscription.builder()
		 .name(subscriptionRequestDTO.getName())
		 .category(subscriptionRequestDTO.getCategory())
         .amount(subscriptionRequestDTO.getAmount())
         .billingCycle(subscriptionRequestDTO.getBillingCycle())
         .nextBillingDate(subscriptionRequestDTO.getNextBillingDate())
         .reminderDaysBefore(subscriptionRequestDTO.getReminderDaysBefore() != null ? subscriptionRequestDTO.getReminderDaysBefore() : 3)
         .status(subscriptionRequestDTO.getStatus() != null ? subscriptionRequestDTO.getStatus() : SubscriptionStatus.ACTIVE)
         .build();
		
	}
	
	public SubscriptionResponseDTO toResponseDTO(Subscription subscription) {
		
		long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), subscription.getNextBillingDate());
		
		
		return SubscriptionResponseDTO.builder()
                .id(subscription.getId())
                .name(subscription.getName())
                .category(subscription.getCategory())
                .amount(subscription.getAmount())
                .billingCycle(subscription.getBillingCycle())
                .nextBillingDate(subscription.getNextBillingDate())
                .reminderDaysBefore(subscription.getReminderDaysBefore())
                .status(subscription.getStatus())
                .lastRemindedDate(subscription.getLastRemindedDate())
                .daysUntilDue(daysLeft)
                .build();
	}
	
}

