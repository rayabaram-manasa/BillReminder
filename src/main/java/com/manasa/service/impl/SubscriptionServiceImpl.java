package com.manasa.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.manasa.model.User;
import com.manasa.builder.SubscriptionBuilder;
import com.manasa.dao.SubscriptionRepository;
import com.manasa.dto.SubscriptionRequestDTO;
import com.manasa.dto.SubscriptionResponseDTO;
import com.manasa.dto.SubscriptionStatus;
import com.manasa.exceptions.SubscriptionNotFoundException;
import com.manasa.model.Subscription;
import com.manasa.service.SubscriptionService;
import com.manasa.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionBuilder subscriptionBuilder;
    private final UserService userService;

    @Override
    public SubscriptionResponseDTO createSubscription(SubscriptionRequestDTO subscriptionRequestDTO, String userEmail) {
        User user = userService.findByEmail(userEmail);
        Subscription subscription = subscriptionBuilder.toEntity(subscriptionRequestDTO);
        subscription.setUser(user);

        Subscription savedSubscription = subscriptionRepository.save(subscription);
        return subscriptionBuilder.toResponseDTO(savedSubscription);
    }

    @Override
    public List<SubscriptionResponseDTO> getSubscriptionsForUser(String userEmail) {
        User user = userService.findByEmail(userEmail);
        return subscriptionRepository.findByUserOrderByNextBillingDateAsc(user)
                .stream()
                .map(subscriptionBuilder::toResponseDTO)
                .toList();
    }

    @Override
    public SubscriptionResponseDTO getSubscriptionById(Long id, String userEmail) {
        Subscription subscription = findOwnedSubscription(id, userEmail);
        return subscriptionBuilder.toResponseDTO(subscription);
    }

    @Override
    public SubscriptionResponseDTO updateSubscription(Long id, SubscriptionRequestDTO subscriptionRequestDTO, String userEmail) {
        Subscription existingSubscription = findOwnedSubscription(id, userEmail);
        existingSubscription.setName(subscriptionRequestDTO.getName());
        existingSubscription.setCategory(subscriptionRequestDTO.getCategory());
        existingSubscription.setAmount(subscriptionRequestDTO.getAmount());
        existingSubscription.setBillingCycle(subscriptionRequestDTO.getBillingCycle());
        existingSubscription.setNextBillingDate(subscriptionRequestDTO.getNextBillingDate());
        existingSubscription.setReminderDaysBefore(subscriptionRequestDTO.getReminderDaysBefore());
        if (subscriptionRequestDTO.getStatus() != null) {
            existingSubscription.setStatus(subscriptionRequestDTO.getStatus());
        }

        Subscription updatedSubscription = subscriptionRepository.save(existingSubscription);
        return subscriptionBuilder.toResponseDTO(updatedSubscription);
    }

    @Override
    public void markAsPaidAndAdvanceCycle(Long id, String userEmail) {
        Subscription subscription = findOwnedSubscription(id, userEmail);
        LocalDate currentDueDate = subscription.getNextBillingDate();

        if (subscription.getBillingCycle() == com.manasa.dto.BillingCycle.ONE_TIME) {
            subscription.setStatus(SubscriptionStatus.PAID);
            subscription.setLastRemindedDate(null);
            subscriptionRepository.save(subscription);
        } else {
            Subscription completedBill = Subscription.builder()
                    .name(subscription.getName())
                    .category(subscription.getCategory())
                    .amount(subscription.getAmount())
                    .billingCycle(subscription.getBillingCycle())
                    .nextBillingDate(currentDueDate)
                    .reminderDaysBefore(subscription.getReminderDaysBefore())
                    .status(SubscriptionStatus.PAID)
                    .user(subscription.getUser())
                    .build();
            subscriptionRepository.save(completedBill);

            switch (subscription.getBillingCycle()) {
                case WEEKLY -> subscription.setNextBillingDate(currentDueDate.plusWeeks(1));
                case MONTHLY -> subscription.setNextBillingDate(currentDueDate.plusMonths(1));
                case QUARTERLY -> subscription.setNextBillingDate(currentDueDate.plusMonths(3));
                case YEARLY -> subscription.setNextBillingDate(currentDueDate.plusYears(1));
                default -> {}
            }
            subscription.setLastRemindedDate(null);
            subscriptionRepository.save(subscription);
        }
    }

    @Override
    public void deleteSubscription(Long id, String userEmail) {
        Subscription existingSubscription = findOwnedSubscription(id, userEmail);
        subscriptionRepository.delete(existingSubscription);
    }

    private Subscription findOwnedSubscription(Long id, String userEmail) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new SubscriptionNotFoundException("Subscription not found with ID: " + id));

        if (!subscription.getUser().getEmail().equals(userEmail)) {
            throw new SubscriptionNotFoundException("Access denied to subscription ID: " + id);
        }
        return subscription;
    }
}