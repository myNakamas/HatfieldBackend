# HatfieldBackend

[![Java Build with Gradle](https://github.com/myNakamas/HatfieldBackend/actions/workflows/gradle.yml/badge.svg)](https://github.com/myNakamas/HatfieldBackend/actions/workflows/gradle.yml)

---

## Backend of Hatfield Inventory and Repair Management System

The server side of a system for the management of multiple repair shops.
The system provides:

- User authentication and authorization
- Inventory management,
- Control and overview of a single repair,
- Chat system using Spring Websocket,
- Exporting PDFs using the collected data,
- User Logs and statistics.

## Tech stack

---

- Spring Boot 3.0.2
- Gradle
- Java 17
- Postgres

## Running Locally

---
Install JDK 17 or above from [here](https://jdk.java.net/).

The following environment variables need to be set prior to running:

- `db-url` : The postgres db url (example :``)
- `db-username` : The username of the user for the database
- `db-password` : The password of the user for the database
- `jwtSecret` : a random (32 or 64 characters long) key needed for the jwt encryption
- `encryptorSecret` : a random (32 or 64 characters long) key needed for the user details encryption
- `fe-host` : The url, this website is hosted (example :`http://websitename`)
- `output-dir` : The folder which the service will save and read files from
