package com.manasa.dto;
import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionResponseDTO {
	private Long id;
    private String name;
    private String category;
    private BigDecimal amount;
    private BillingCycle billingCycle;
    private LocalDate nextBillingDate;
    private Integer reminderDaysBefore;
    private SubscriptionStatus status;
    private LocalDate lastRemindedDate;
    private long daysUntilDue;
}
