package com.manasa.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionRequestDTO {

	private String name;
	private String category;
	private BigDecimal amount;
	private BillingCycle billingCycle;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate nextBillingDate;
	
	private Integer reminderDaysBefore;
	private String recipentEmail;
	private SubscriptionStatus status;
	
	
	
}

