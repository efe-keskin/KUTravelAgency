# KU Travel Agency

A feature-rich travel agency application with a **Java backend** and **Swing GUI** for booking and managing travel packages, with roles for both **Customers** and **Admins**.

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
  - [Usage](#usage)
- [Project Structure](#project-structure)
- [Technical Design](#technical-design)
- [References](#references)

---

## Overview

The **KU Travel Agency** application provides a seamless travel booking experience, allowing users to:

- Book curated travel packages.
- Create custom travel packages.
- Manage reservations and view transaction history.

Admins enjoy additional privileges to manage users, reservations, and packages.

---

## Features

### Customer Features
- **Sign Up/Login:** Secure user authentication.
- **Book Packages:** Choose from pre-curated packages with discounts.
- **Custom Travel:** Build a travel package tailored to your needs.
- **Manage Reservations:** Edit or cancel bookings with dynamic refund policies.
- **Transaction History:** Monitor payments and refunds.
- **View Statistics:** Track reservations and spending patterns.

### Admin Features
- **Manage Packages:** Create, edit, or delete travel packages.
- **Manage Reservations:** Handle bookings on behalf of customers.
- **User Analytics:** View detailed user statistics and logs.

---

## Getting Started

### Prerequisites
- Java Development Kit (JDK 8 or later)
- An Integrated Development Environment (IDE) like IntelliJ IDEA or Eclipse.

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/<your-username>/KU-Travel-Agency.git
   cd KU-Travel-Agency
   ```
2. Compile the project:
   ```bash
   javac -d bin src/**/*.java
   ```
3. Run the application:
   ```bash
   java -cp bin core.App
   ```

### Usage
1. **Login as Admin or Customer.**
2. Explore features through intuitive GUIs for booking, creating, and managing packages.
3. For detailed steps, refer to [User Guide](#).

---

## Project Structure

```plaintext
src/
├── constants/           # Constants used throughout the app.
├── core/                # Main application entry point.
├── custom/              # Custom UI components and error handlers.
├── databases/           # Database operations for Admins and Customers.
├── datasets/            # Provided datasets for initialization.
├── gui/                 # Graphical user interface components.
├── logs/                # Logging utilities and output.
├── products/            # Classes representing travel products.
├── reservation_logs/    # Reservation log management.
├── services/            # Core business logic and algorithms.
├── users/               # User-related classes and interfaces.
```

---

## Technical Design

### Class Relationships
- **12 Packages** organize functionality and responsibilities.
- Implemented **inheritance** and **interfaces** for modular design.
- **HashMaps and ID-based management** ensure efficient data access.

### File Processing
- Synchronized `.txt` file operations for reservations, packages, and logs.
- Real-time updates ensure data consistency.

### Algorithms
- **Availability Management:** Dynamic availability checks using file-based storage.
- **Custom Package Creation:** Algorithm ensures compatibility across hotels, flights, and taxis.
- **Reservation Availability:** Utilizes files (`flightavailability.txt`, `hotelavailability.txt`, `taxiavailability.txt`) to dynamically track and update available slots.
- **Travel Parser:** Generates and manages static `HashMaps` for ID-based data referencing, ensuring efficient access and updates.
- **Taxi Timing Calculation:** Computes taxi travel times based on distances and speed assumptions.
- **Component Compatibility:** Ensures user-selected hotels, flights, and taxis are synchronized for seamless travel plans.
- **Customer Sorting for Admins:** Implements sorting algorithms to rank customers by total spending, with secondary sorting by alphabetical order.

---

