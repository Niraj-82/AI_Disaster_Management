# 🛰️ ResQAI – AI-Powered Disaster Management System

## 📘 Overview
**ResQAI** is an Android-based mobile application designed to assist in **disaster management and public safety**.  
It empowers users to **report incidents**, **find nearby shelters**, **send SOS alerts**, and **receive emergency notifications** in real-time.  
The application leverages **Firebase Cloud**, **Google Maps APIs**, and **on-device SQLite** to ensure reliable, real-time communication and data access even under critical conditions.

---

## 🎯 Key Features

### 👥 User Roles
- **User:** Can report incidents, send SOS alerts, and access emergency shelters.
- **Admin:** Manages shelters, verifies reports, and posts official announcements.

### 🚨 Core Functionalities
1. **Incident Reporting**
   - Report incidents such as Fire, Flood, or Earthquake.
   - Attach images and include automatic GPS location.
   - Upload data to Firebase Firestore for public visibility.

2. **SOS Emergency Feature**
   - Sends SMS alerts with live location to pre-saved emergency contacts.
   - Contacts stored securely in a local SQLite database.

3. **Shelter Management**
   - **Users:** View nearby shelters on Google Maps.
   - **Admins:** Add or edit shelter locations for real-time access.

4. **Announcements**
   - Admins can broadcast important alerts and public safety updates.

5. **Medical Info**
   - Users can store medical details for quick access by responders.

6. **AI and Machine Learning**
   - (Future Enhancement) AI models for incident classification, alert prediction, and intelligent response optimization.

---

## 🏗️ Technical Architecture

| Layer | Description |
|-------|--------------|
| **Frontend (UI)** | Android (Kotlin) with Material Design Components and View Binding. |
| **Backend (Cloud)** | Firebase Firestore for NoSQL data storage and Firebase Storage for image uploads. |
| **Authentication** | Firebase Authentication for secure login and registration. |
| **Local Storage** | SQLite database for offline emergency contacts. |
| **Mapping & GPS** | Google Maps API and Play Services Location for real-time mapping. |
| **Image Handling** | Glide library for image loading and display. |

---

## 🧠 Technologies Used

- **Programming Language:** Kotlin  
- **Frontend:** XML Layouts, Material Components  
- **Database:** Firebase Firestore, SQLite  
- **Authentication:** Firebase Auth  
- **Cloud Storage:** Firebase Storage  
- **Maps & Location:** Google Maps API, Play Services Location  
- **Libraries:** Glide, AndroidX, View Binding  
- **IDE:** Android Studio  

---

## ⚙️ Installation & Setup

### Prerequisites
- Android Studio (latest version)
- Active Firebase project
- Google Maps API key
- Android SDK 33+

### Steps
1. **Clone this repository:**
   ```bash
   git clone https://github.com/yourusername/ResQAI.git
