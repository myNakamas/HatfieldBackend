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

- db-url
- db-username
- db-password