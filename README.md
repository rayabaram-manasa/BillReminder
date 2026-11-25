# BillReminder — Smart Subscription & Bill Renewal Reminder System

**BillReminder** is a full-stack personal finance and bill tracking web application built with **Java 21**, **Spring Boot**, **Spring Security**, **Spring Data JPA**, **MySQL**, **Spring AOP**, and **Thymeleaf**. 

It allows users to manage recurring subscriptions and utility bills in one place, separates active upcoming obligations from completed payment history, automatically advances recurring billing cycles upon payment, and dispatches scheduled email alerts before due dates so users never miss a payment or incur late fees.

---

## Key Features

- **Secure User Authentication & Data Isolation:** Registration and login powered by Spring Security with `BCrypt` password encryption. Every database query and action validates user ownership so users can only view or modify their own bills.
- **Upcoming vs. Completed Bill Lifecycle:**
  - **Upcoming Bills:** Lists all `ACTIVE` bills sorted by nearest due date with live countdown badges (`Overdue`, `Due Today`, `X days left`) and a red **Mark Paid** button.
  - **Automatic Cycle Renewal:** Clicking **Mark Paid** on a recurring bill (`WEEKLY`, `MONTHLY`, `QUARTERLY`, `YEARLY`) saves a `PAID` record in **Completed Bills** while advancing the active subscription to its next billing date. `ONE_TIME` bills move directly to **Completed Bills**.
- **Automated Email Reminders:** A `@Scheduled` cron job scans active bills daily, evaluates each bill's `reminderDaysBefore` threshold, prevents duplicate alerts on the same day via `lastRemindedDate`, and sends formatted email notifications via `JavaMailSender` (SMTP). Includes a manual **Test Reminder** trigger on the dashboard.
- **Centralized AOP Logging:** Spring AOP (`@Around` and `@AfterThrowing` advice) automatically logs method entry, exit, execution time in milliseconds, and exceptions across Controllers, Services, and Schedulers to `logs/mylogs.log`.
- **Modern SaaS UI:** Responsive split-screen authentication pages and a deep navy (`#142947`) dashboard with summary metric cards, password visibility toggles, and required field indicators.

---

## Tech Stack & Dependencies

- **Java 21 & Maven** — Core runtime and build/dependency management.
- **Spring Web (`spring-boot-starter-web`)** — MVC controllers, request routing, and embedded Tomcat server.
- **Spring Security (`spring-boot-starter-security`)** — Session-based authentication, route protection, and `BCryptPasswordEncoder`.
- **Spring Data JPA (`spring-boot-starter-data-jpa`)** — Repository abstraction and Hibernate ORM database persistence.
- **MySQL Driver (`mysql-connector-j`)** — JDBC driver connecting the application to MySQL 8.
- **Thymeleaf (`spring-boot-starter-thymeleaf`)** — Server-side template engine for rendering the dashboard, authentication views, and forms.
- **Java Mail Sender (`spring-boot-starter-mail`)** — SMTP client for sending automated bill renewal reminder emails.
- **Spring AOP (`spring-boot-starter-aop`)** — Aspect-Oriented Programming (`@Aspect`, `@Around`, `@AfterThrowing`) for execution timing and exception logging.
- **Spring Boot Actuator (`spring-boot-starter-actuator`)** — Operational endpoints (`/actuator/health`, `/actuator/scheduledtasks`) for inspecting scheduled background tasks.
- **Spring Boot DevTools (`spring-boot-devtools`)** — Automatic application restarts and fast feedback during development.
- **Project Lombok (`lombok`)** — Generates getters, setters, builders, constructors, and `@Slf4j` loggers at compile time.
- **Bootstrap 5 & Bootstrap Icons** — Responsive grid layout, components, and iconography.




# File-by-File Breakdown

1. Application Entry Point (com.manasa)
BillReminderApplication.java: Main Spring Boot entry point annotated with @SpringBootApplication and @EnableScheduling to activate background reminder jobs.

2. Entities & Repositories (com.manasa.entity, com.manasa.repository)
User.java: JPA entity mapped to the users table storing id, fullName, unique email, BCrypt-encoded password, and createdAt.

Subscription.java: JPA entity mapped to the subscriptions table storing bill attributes (name, category, amount, billingCycle, nextBillingDate, reminderDaysBefore, status, lastRemindedDate) linked via @ManyToOne to User.

UserRepository.java: Spring Data JPA repository providing findByEmail and existsByEmail queries for authentication and registration validation.

SubscriptionRepository.java: Spring Data JPA repository providing user-scoped sorted queries (findByUserOrderByNextBillingDateAsc) and active status queries (findByStatus) for the scheduler.

3. DTOs, Enums & Builder (com.manasa.dto, com.manasa.builder)
BillingCycle.java: Enum defining recurrence intervals (WEEKLY, MONTHLY, QUARTERLY, YEARLY, ONE_TIME).

SubscriptionStatus.java: Enum defining bill states (ACTIVE, PAID, CANCELLED).

UserRegistrationDTO.java: Form-backing DTO for new user signups (fullName, email, password).

SubscriptionRequestDTO.java: Form-backing DTO for creating and editing bills.

SubscriptionResponseDTO.java: View DTO carrying subscription details plus dynamically computed daysUntilDue to the dashboard.

SubscriptionBuilder.java: Converts SubscriptionRequestDTO to Subscription entities, applies updates, and computes daysUntilDue using ChronoUnit.DAYS.

4. Security Layer (com.manasa.security)
CustomUserDetailsService.java: Implements UserDetailsService to load application User records from MySQL during login.

SecurityConfig.java: Configures the SecurityFilterChain, defines public vs. authenticated routes, configures custom /login and /logout behavior, and registers the BCryptPasswordEncoder bean.

5. Service Layer (com.manasa.service & com.manasa.service.impl)
UserService.java / UserServiceImpl.java: Registers users, checks for duplicate emails, hashes passwords with BCrypt, and looks up user profiles.

SubscriptionService.java / SubscriptionServiceImpl.java: Manages CRUD operations, enforces user ownership checks, creates PAID history records upon payment, and advances recurring due dates.

EmailService.java / EmailServiceImpl.java: Builds and sends formatted plain-text renewal reminder emails via JavaMailSender.

6. Controllers & Exception Handling (com.manasa.controller, com.manasa.exceptions)
AuthController.java: Handles /, /login, and /register routes and displays registration success/error feedback.

SubscriptionController.java: Handles /subscriptions dashboard rendering, adding/editing/deleting bills, marking bills paid (/pay/{id}), and manually triggering reminder checks.

SubscriptionNotFoundException.java: Custom runtime exception thrown when a subscription ID does not exist or belongs to a different user.

GlobalExceptionHandler.java: @ControllerAdvice component catching exceptions and redirecting cleanly to /subscriptions with flash error alerts.

7. Scheduler & AOP Logging (com.manasa.scheduler, com.manasa.aspect)
RenewalReminderScheduler.java: @Scheduled cron task that scans ACTIVE bills daily, sends email reminders when daysUntilDue <= reminderDaysBefore, and records lastRemindedDate to avoid duplicate alerts.

LoggingAspect.java: Spring AOP @Aspect logging method entry, exit, execution duration (ms), and exceptions across controller, service.impl, and scheduler packages into logs/mylogs.log.

8. Frontend Templates (src/main/resources/templates)
login.html:  login view with a navy product showcase panel with username  and a password visibility toggle.

register.html: Matching  view with onboarding steps and password visibility toggle.

dashboard.html: Main workspace displaying summary metric cards, the Add New Subscription or Bill form, Upcoming Bills (with red Mark Paid buttons), and Completed Bills (with green Paid badges).

edit-subscription.html: Form page for updating an existing bill's details or status.

# Prerequisites & Setup Guide
1. Prerequisites
Java Development Kit (JDK) 21

MySQL 8.x running locally on port 3306

Maven (or the included Maven Wrapper ./mvnw / mvnw.cmd)

A Gmail account with 2-Step Verification enabled and a generated 16-character Gmail App Password (for SMTP email reminders)

2. Clone the Repository

git clone [https://github.com/rayabaram-manasa/BillReminder.git](https://github.com/rayabaram-manasa/BillReminder.git)
cd BillReminder

3. Configure the MySQL Database
Open MySQL Workbench or your MySQL CLI and create the database:

SQL
CREATE DATABASE billremainder;
(Hibernate ddl-auto=update will automatically create the users and subscriptions tables on first startup).

4. Configure application.properties
Copy src/main/resources/application.properties.example to src/main/resources/application.properties and fill in your local credentials:

Properties
spring.application.name=BillReminder

# MySQL Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/billremainder( create a database named billremainder )
spring.datasource.username=YOUR_MYSQL_USERNAME
spring.datasource.password=YOUR_MYSQL_PASSWORD
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Gmail SMTP Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=YOUR_GMAIL_ADDRESS@gmail.com
spring.mail.password=YOUR_16_CHAR_GMAIL_APP_PASSWORD
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# Actuator & Logging
management.endpoints.web.exposure.include=health,info,scheduledtasks
logging.file.name=logs/mylogs.log
logging.level.com.manasa=INFO
5. Run the Application
Using Maven Wrapper (Terminal / Git Bash):



# Using Eclipse :

# Import as an Existing Maven Project.

# Right-click BillReminderApplication.java → Run As → Spring Boot App.

# Once started, open http://localhost:8080 in your browser.

# How to Use the Application
Create an Account:

Navigate to http://localhost:8080/register.

Enter your Full Name, Email Address (reminders will be sent to this address), and a Password (minimum 6 characters).

* Sign In:

Log in at http://localhost:8080/login using your registered email and password.

* Add a Subscription or Bill:

On the dashboard, fill out the Add New Subscription or Bill form with the service name (e.g., Wi-Fi, Netflix, Electricity), optional category, amount (₹), billing cycle (WEEKLY, MONTHLY, QUARTERLY, YEARLY, or ONE_TIME), next due date, and how many days before the due date you want an email alert (Remind Days Before).

Click Add Reminder.

* Test Email Notifications:

Ensure you have at least one active bill whose due date falls within its Remind Days Before window.

* Click Test Reminder in the top navigation bar (or wait for the daily scheduled cron trigger) to receive an automated email reminder in your inbox.

* Mark a Bill as Paid:

* Click the red Mark Paid button next to any bill in Upcoming Bills & Subscriptions.

A green ✓ Paid entry is recorded in the Completed Bills section below, and recurring subscriptions automatically advance to their next billing date.

* Edit or Delete Bills:

Use the Edit (pencil) button to update any bill's amount, due date, cycle, or status, or the Delete (trash) button to remove it.

Inspect Logs & Scheduled Tasks:

View method execution times and error traces in logs/mylogs.log.

Inspect live scheduler status at http://localhost:8080/actuator/scheduledtasks.
