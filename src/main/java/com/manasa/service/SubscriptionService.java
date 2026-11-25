package com.manasa.service;

import java.util.List;

import com.manasa.dto.SubscriptionRequestDTO;
import com.manasa.dto.SubscriptionResponseDTO;

public interface SubscriptionService {

	SubscriptionResponseDTO createSubscription(SubscriptionRequestDTO subscriptionRequestDTO,String userEmail);
	List<SubscriptionResponseDTO> getSubscriptionsForUser(String userEmail);
    SubscriptionResponseDTO getSubscriptionById(Long id, String userEmail);
    SubscriptionResponseDTO updateSubscription(Long id, SubscriptionRequestDTO subscriptionRequestDTO, String userEmail);
    void markAsPaidAndAdvanceCycle(Long id, String userEmail);
    void deleteSubscription(Long id, String userEmail);
}
