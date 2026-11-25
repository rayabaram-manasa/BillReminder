package com.manasa.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.manasa.dto.BillingCycle;
import com.manasa.dto.SubscriptionRequestDTO;
import com.manasa.dto.SubscriptionResponseDTO;
import com.manasa.dto.SubscriptionStatus;
import com.manasa.model.User;
import com.manasa.scheduler.RenewalReminderScheduler;
import com.manasa.service.SubscriptionService;
import com.manasa.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final UserService userService;
    private final RenewalReminderScheduler renewalReminderScheduler;

    @GetMapping
    public String viewDashboard(Model model, Principal principal) {
        String userEmail = principal.getName();
        User currentUser = userService.findByEmail(userEmail);

        model.addAttribute("userFullName", currentUser.getFullName());
        model.addAttribute("subscriptions", subscriptionService.getSubscriptionsForUser(userEmail));
        model.addAttribute("subscriptionRequestDTO", new SubscriptionRequestDTO());
        model.addAttribute("billingCycles", BillingCycle.values());
        model.addAttribute("statuses", SubscriptionStatus.values());
        return "dashboard";
    }

    @PostMapping
    public String createSubscription(@ModelAttribute("subscriptionRequestDTO") SubscriptionRequestDTO subscriptionRequestDTO,
                                     Principal principal,
                                     RedirectAttributes redirectAttributes) {
        subscriptionService.createSubscription(subscriptionRequestDTO, principal.getName());
        redirectAttributes.addFlashAttribute("successMessage", "Subscription added successfully!");
        return "redirect:/subscriptions";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, Principal principal) {
        SubscriptionResponseDTO existingSubscription = subscriptionService.getSubscriptionById(id, principal.getName());

        SubscriptionRequestDTO subscriptionRequestDTO = SubscriptionRequestDTO.builder()
                .name(existingSubscription.getName())
                .category(existingSubscription.getCategory())
                .amount(existingSubscription.getAmount())
                .billingCycle(existingSubscription.getBillingCycle())
                .nextBillingDate(existingSubscription.getNextBillingDate())
                .reminderDaysBefore(existingSubscription.getReminderDaysBefore())
                .status(existingSubscription.getStatus())
                .build();

        model.addAttribute("subscriptionId", id);
        model.addAttribute("subscriptionRequestDTO", subscriptionRequestDTO);
        model.addAttribute("billingCycles", BillingCycle.values());
        model.addAttribute("statuses", SubscriptionStatus.values());
        return "edit-subscription";
    }

    @PostMapping("/update/{id}")
    public String updateSubscription(@PathVariable Long id,
                                     @ModelAttribute("subscriptionRequestDTO") SubscriptionRequestDTO subscriptionRequestDTO,
                                     Principal principal,
                                     RedirectAttributes redirectAttributes) {
        subscriptionService.updateSubscription(id, subscriptionRequestDTO, principal.getName());
        redirectAttributes.addFlashAttribute("successMessage", "Subscription updated!");
        return "redirect:/subscriptions";
    }

    @PostMapping("/pay/{id}")
    public String markAsPaid(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        subscriptionService.markAsPaidAndAdvanceCycle(id, principal.getName());
        redirectAttributes.addFlashAttribute("successMessage", "Marked as paid! Next due date updated.");
        return "redirect:/subscriptions";
    }

    @PostMapping("/delete/{id}")
    public String deleteSubscription(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        subscriptionService.deleteSubscription(id, principal.getName());
        redirectAttributes.addFlashAttribute("successMessage", "Subscription deleted.");
        return "redirect:/subscriptions";
    }
    
    @PostMapping("/trigger-reminders")
    public String triggerRemindersManually(RedirectAttributes redirectAttributes) {
        renewalReminderScheduler.checkAndSendReminders();
        redirectAttributes.addFlashAttribute("successMessage", "Reminder check executed! Check your email and logs.");
        return "redirect:/subscriptions";
    }
}