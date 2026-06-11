# RecoverAI – AI Powered Lost & Found Management System

## Overview

RecoverAI is an AI-powered smart lost and found management platform developed using Java Swing, MySQL, JDBC, and Python OCR services.

The system helps users report lost and found items, perform intelligent matching using AI-based scoring, extract text using OCR, and notify users when possible matches are found.

---

## Features

* User Authentication
* Lost Item Reporting
* Found Item Reporting
* Smart AI Match Engine
* OCR-Based Text Extraction
* Fraud Risk Detection
* Ownership Verification
* Admin Claim Approval
* Email Notification System
* Modern UI/UX Dashboard

---

## Technologies Used

### Frontend

* Java Swing

### Backend

* Java
* JDBC
* MySQL

### AI/OCR

* Python
* EasyOCR
* Flask API

---

## Modules

### Student Module

* Report lost items
* Report found items
* AI match recommendations
* OCR scanning

### Admin Module

* Claim approval
* Fraud analysis
* Ownership score review

---

## AI Matching Parameters

* Item Name Similarity
* Description Similarity
* Category Matching
* Color Matching
* Brand Matching
* Location Matching
* OCR Text Similarity

---

## Database Tables

* users
* lost_items
* found_items
* claims
* notifications

---

## How to Run

### Step 1

Run OCR API:

```bash
cd python_ai
py ocr_api.py
```

### Step 2

Run Java Application:

```bash
java -cp "RecoverAI.jar;lib/*" Main
```

---

## Future Enhancements

* Real-time email service
* Image similarity matching
* Mobile application
* Cloud deployment
* Face/Object recognition

---

## Developer

Meera H S
