# Saffron — Event Management (Full Project Overview)

## Project Purpose & Core Principle

Saffron Gardens is a curated, premium event management platform focused on delivering an exclusive experience for clients and event planners. Core principle:

- Only verified Event Planners can make reservations.
- Event Planners choose from a curated list of approved Vendors (liquor, cleaning, DJs, decorators, caterers, security, photographers, etc.).
- Vendors must be approved by an Admin before appearing in the booking system.

This approach keeps Saffron Gardens:

- Exclusive
- Curated
- High-end

## System Architecture (Simplified + Premium)

Two user roles:

- Event Planners — apply, must be approved, can book halls, select vendors, manage bookings.
- Vendors — partner accounts created/approved by Admin; provide services to Event Planners (cannot book halls).
- Admin — manage approvals, calendar, pricing, analytics, payouts, gallery, and more.

## How Vendors Fit Into the System

Vendors are categorized (examples):

- LIQUOR_SUPPLIER
- CLEANING_SERVICE
- DECORATION_TEAM
- DJ
- MC
- CATERER
- SECURITY
- PHOTOGRAPHER
- ...and other service categories

Admin approves or invites vendors. Approved vendors become selectable by Event Planners during booking.

## Admin Capabilities (Portal)

- Approve Event Planner applications
- Approve Vendor applications and manage vendor profiles
- Manage hall calendar and bookings
- Upload gallery photos for marketing
- Manage pricing and vendor categories
- View revenue dashboard and analytics
- Manage emergency cancellations and refund/payout workflows
- Built-in messaging/chat between planners and vendors
- Vendor payout management and reports

## Event Planner Capabilities (Dashboard)

- View available dates and hall availability
- Create bookings and select vendors per booking (e.g., choose DJ Mike, Sparkle Clean)
- Manage bookings and upload event documents/requirements
- Securely pay online and view payment status
- Print invoices and download contract PDFs
- Upload event schedule/timeline and hall templates/layouts
- Manage recurring/annual events and access partner discounts

## Vendor System (Partner Features)

Each vendor account includes:

- Account credentials (email/password)
- Profile: logo, business name, description, category
- Optional pricing information
- Availability calendar
- Incoming booking list and booking details
- Messaging/chat with event planners
- Optional payout/payment integration

Vendors do NOT book halls — they provide services to Event Planners.

## Backend Structure (Entities)

Key entities (simplified):

- User

  - id
  - email
  - password (BCrypt hashed)
  - role (ADMIN, EVENT_PLANNER, VENDOR)
  - approved (boolean)
  - profileCompleted (boolean)

- Vendor

  - id
  - userId (FK)
  - category
  - businessName
  - description
  - pricing
  - availability
  - rating (optional)

- Booking
  - id
  - eventPlannerId
  - date
  - startTime
  - endTime
  - vendorsSelected[] (FKs to Vendor)
  - status (REQUESTED, CONFIRMED, CANCELLED, COMPLETED)
  - paymentStatus

## Frontend Pages (Web & Mobile)

Public pages:

- Home, Gallery, Pricing, Contact, Apply as Event Planner

Event Planner Dashboard:

- Book hall, Choose vendors, My bookings, Payments, Messages, Profile

Vendor Dashboard:

- My bookings, Availability calendar, Payments, Profile

Admin Dashboard:

- Approvals, Bookings overview, Calendar, Revenue analytics, Vendor management, Gallery upload, Settings

## User Flow (Example)

Event Planner Booking Flow:

1. Event Planner logs in (approved)
2. Selects date & time
3. System checks availability and shows available slots
4. Event Planner selects Vendors (Liquor, Cleaning, DJ, Decor, etc.)
5. System generates a full quotation
6. User pays online
7. Vendors are notified of bookings
8. Admin receives booking overview and can intervene if necessary

## How to Get Vendors Registered and Approved

1. Vendor Registration Request Page (public or admin-invitations)

   - Vendors fill a request form: business name, category, email, description, documents (CAC, ID, incorporation docs)

2. Admin Approval Panel

   - Admin reviews vendor details and documents, then Approves or Rejects

3. Vendor Account Activation

   - On approval vendor receives an email: "Your vendor account is approved"

4. Vendor Appears in Booking System
   - Event Planners can now select the vendor during booking

## Roadmap — Next Backend Tasks (Revised)

Priority backend items to implement next:

1. Simplify Roles: `ADMIN`, `EVENT_PLANNER`, `VENDOR` only
2. Remove generic end-user registration flow — only Event Planners and Vendors (by request/approval)
3. Build Event Planner Registration API: `POST /api/event-planner/apply` (admin approves)
4. Build Vendor Registration API: `POST /api/vendor/apply` (admin approves)
5. Build Hall Availability System (calendar + conflict detection)
6. Build Booking System (create, update, cancel, status, payments)
7. Add Vendor Selection to Bookings (store selected vendor ids and notify vendors)
8. Admin Dashboard Endpoints (approve planners, approve vendors, view/manage bookings, manage payouts)

## Operational & Tech Notes

- Database: PostgreSQL recommended for production. H2 used for local tests. Use Flyway for migrations (scripts in `src/main/resources/db/migration`).
- Security: Passwords are hashed with BCrypt via `PasswordEncoder`. Add robust auth (JWT or session) for production.
- Transactions: Use `@Transactional` for multi-entity operations (e.g., creating a user + vendor record).
- Validation: Use Jakarta Validation (`@Valid`, `@NotBlank`, `@Email`) on DTOs and controllers.
- Testing: Add unit tests for services and integration tests against a real Postgres instance (Docker) in CI.

## Developer Quick Start (Docker Compose)

1. Build & start (requires Docker & Docker Compose):

```bash
cd backend

```

2. App: http://localhost:8080
3. Postgres: uses credentials set in `docker-compose.yml` (defaults: `postgres` / `postgres`)

## Build locally with Maven

```bash
cd backend
mvn -DskipTests package
java -jar target/saffron-0.0.1-SNAPSHOT.jar
```
