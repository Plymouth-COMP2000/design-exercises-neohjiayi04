# 🍽️ Dineo – Restaurant Management App

<div align="center">

**A comprehensive mobile solution for streamlining restaurant operations and enhancing customer dining experiences**

[📹 Video Demo](https://youtu.be/pfn2Lkk0g_Q)  
https://youtu.be/pfn2Lkk0g_Q?si=KqOFFjlZCHAl_ER8

</div>

## 📌 Overview

Dineo is a feature-rich mobile restaurant application that bridges the gap between customers and restaurant staff, creating a seamless dining experience from reservation to service. Built with modern Android development practices, the app empowers customers to effortlessly browse menus and manage reservations while providing staff with powerful tools to handle operations efficiently.

**Developed as an academic project**, Dineo showcases comprehensive mobile application development concepts including:
- UI/UX design principles
- Role-based authentication and authorization
- Local data persistence with SQLite
- Real-time notification systems
- Material Design implementation
---

## 🎯 Project Objectives

- ✅ **Simplify Customer Experience** – Provide an intuitive platform for menu browsing and reservation management
- ✅ **Empower Staff Operations** – Enable efficient reservation handling and customer relationship management
- ✅ **Implement Role-Based Access** – Separate customer and staff functionalities with appropriate permissions
- ✅ **Enhance Operational Efficiency** – Reduce manual workload and improve restaurant workflow

---

## 👥 User Roles & Capabilities

### 🧑‍🤝‍🧑 Customer Features
Customers can register directly within the app
| Feature | Description |
|---------|-------------|
| **🔐 Authentication** | Secure registration, login, and password recovery |
| **📖 Menu Browsing** | Explore comprehensive menu with detailed item information |
| **📅 Reservations** | Create, edit, and cancel table reservations|
| **🔔 Notifications** | Receive instant alerts for reservation updates and status changes |
| **👤 Profile Management** | Update personal information, change password, manage account settings |

### 👨‍🍳 Staff Features
Staff accounts cannot be registered through the app. 
Staff users are pre-created directly in the database for security and access control.
###  🔐 Pre-configured Staff Account (Testing)

- Username: Haviisha 
- Password: helloIsMe

| Feature | Description |
|---------|-------------|
| **🔑 Staff Access** | Dedicated staff login with elevated permissions |
| **🍽️ Menu Management** | Add, edit, and delete menu items with full control |
| **📋 Reservation Dashboard** | View all customer reservations in a centralized interface |
| **🎨 Visual Status Indicators** | Quickly identify reservation status through color-coded indicators |
| **✅ Approval System** | Confirm or reject pending reservations with one tap |
| **📝 Details Management** | Access and manage comprehensive reservation information |

---

## ✨ Key Features

### 📖 Intuitive Menu Browsing
- Clean, organized menu layout
- High-quality item images (placeholder support)
- Detailed descriptions and pricing
- Category-based navigation
- Search and filter capabilities

### 📅 Advanced Reservation Management
- **Date & Time Picker** – Easy selection with calendar interface
- **Party Size Selection** – Specify number of guests
- **Instant Booking** – Quick reservation creation
- **Edit Functionality** – Modify existing reservations
- **Cancellation** – Cancel unwanted bookings
- **Status Tracking** – Real-time status updates with visual indicators:
  - 🟡 **Pending** – Awaiting staff confirmation
  - 🟢 **Confirmed** – Reservation approved
  - 🔴 **Rejected** – Reservation declined

### 🔔 Notification System
- Push notifications for reservation updates
- Status change alerts (Confirmed/Rejected)

### 👤 Comprehensive Profile Management
- View and edit personal information
- Update contact details
- Change password securely
- Profile photo upload
- Notification preferences

---

## 🧱 Technology Stack

### Core Technologies
- **Language:** Java 
- **IDE:** Android Studio 
- **Database:** SQLite (local storage)
- **UI Components**: Material Design, XML layouts

---

## 📌 Current Limitations

- **Local Database Only** – Uses SQLite without cloud synchronization
- **Limited Scalability** – Designed for single-restaurant demo purposes
- **Basic Security** – Academic-level security implementation
- No payment gateway integration
- No loyalty program features
