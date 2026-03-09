# MyVet - A Mobile Clinic Management Platform

MyVet is a full-stack mobile application for Android, built to serve both veterinarians and pet owners. Developed entirely in **Kotlin** and powered by a **Firebase** backend, this platform provides a seamless, real-time, and multi-lingual experience (supporting **English and Hebrew**).

## Demo

Short demo showing the appointment booking flow between a pet owner and a veterinarian.
https://github.com/user-attachments/assets/5c082ba3-2801-4ac3-9940-e60c8cd59e84



## Project Vision: Why MyVet?

Small veterinary clinics often lack access to expensive, complex management software. MyVet was designed to bridge this gap by offering a **free, intuitive, and powerful mobile-first platform**. The goal is to empower veterinarians with a simple tool to manage their clients and appointments, while simultaneously providing pet owners with an easy way to find local care and manage their pet's health.

## Core Features

MyVet provides a tailored experience for each user type, built on a foundation of shared core functionalities.

- **Multi-Provider & Role-Based Authentication:** A single, secure entry point for all users. Both pet owners and veterinarians can register and log in using **Email, Google, or Facebook**, with the app directing them to the correct user experience based on their selected role.

### For Pet Owners
- **Location-Based Vet Discovery:** Find and view nearby veterinarians, powered by the device's real-time location.
- **In-App Appointment Scheduling:** Schedule, view, and cancel appointments directly within the app.
- **Pet Profile Management:** Add, update, and manage your pet's details, including uploading a profile picture.
- **Full Account Control:** Log out or permanently delete your account and all associated data.

### For Veterinarians
- **Complete Client Management System:** A comprehensive dashboard to manage all pet and owner records.
- **Real-Time Appointment Dashboard:** View and manage all scheduled appointments from clients, with updates reflected instantly.

## Technical Highlights & Capabilities Demonstrated

This project showcases the ability to architect and build a complete, feature-rich mobile application from end to end.

### 1. Full-Stack, Real-Time Architecture with NoSQL
The entire backend is built on Firebase, demonstrating a modern, serverless approach.
- **Cloud Firestore:** Leveraged a flexible **NoSQL database** to model complex, one-to-many relationships (vets, owners, pets). Its real-time capabilities ensure data updates are instantly reflected for all connected users without manual refreshes.
- **Data Modeling:** Designed a Firestore schema to support role-based data access and efficient querying for features like the appointment dashboard.

### 2. Advanced User Authentication with Roles
Implemented a secure and versatile authentication system, a cornerstone of any real-world application.
- **Multiple Auth Providers:** Integrated Google, Facebook, and standard Email sign-in, showcasing the ability to work with multiple third-party SDKs.
- **Role-Based Access Control (RBAC):** The system distinguishes between "Veterinarian" and "Pet Owner" roles right from registration, providing different UI, permissions, and features for each user type.

### 3. Asynchronous Programming for a Responsive UI
All interactions with the Firebase backend (database queries, authentication, file storage) are handled **asynchronously**. The application never blocks the main UI thread while waiting for a network response.
- **How it works:** Firebase operations are handled using a callback-based model (e.g., `.addOnSuccessListener`, `.addOnFailureListener`). This ensures the app remains fluid and responsive at all times, preventing the dreaded "Application Not Responding" errors.
- **Why it matters:** This is a fundamental principle of modern mobile development and demonstrates the ability to build a smooth, high-quality user experience.

### 4. Deep Integration with Device APIs & Google Services
The app leverages core device hardware and Google services to provide a rich user experience.
- **Location-Aware Features:** Implemented the full flow for location services: requesting user permissions, fetching the device's current location.

### 5. Internationalization

The application was built from the ground up to support multiple languages (**English and Hebrew**), demonstrating attention to user experience and the ability to manage string resources for a global audience.
