package com.manasa.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.manasa.dto.SubscriptionStatus;
import com.manasa.model.Subscription;
import com.manasa.model.User;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    List<Subscription> findByUserOrderByNextBillingDateAsc(User user);

    List<Subscription> findByStatusOrderByNextBillingDateAsc(SubscriptionStatus status);
}