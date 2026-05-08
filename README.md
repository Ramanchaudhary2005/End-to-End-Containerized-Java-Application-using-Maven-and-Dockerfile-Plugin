# Advanced School Portal (Spring Boot + MySQL + Docker)

This project now provides a full school portal with separate Teacher and Student login flows.

## Features

- Role-based login and authorization (Teacher / Student)
- Teacher dashboard:
  - Add student details
  - Upload subject-wise marks
  - Record attendance
  - See class topper and subject toppers
  - Visual analytics for subject averages
- Student dashboard:
  - View own marks
  - View own attendance
  - Subject-wise score graph

## Tech Stack

- Java 17
- Spring Boot 3
- Spring Security
- Spring Data JPA
- Thymeleaf + Chart.js
- MySQL
- Maven
- Docker

## Local Setup

Set MySQL environment values (PowerShell example):

```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/student_portal?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="root"
```

Run the app:

```bash
mvn spring-boot:run
```

App URL: `http://localhost:9090/login`

## Demo Accounts

- Teacher: `teacher1` / `teacher123`
- Student: `student1` / `student123`

## Build and Package

```bash
mvn clean package
```

## Docker Run

Build:

```bash
docker build -t student-app:local .
```

Run:

```bash
docker run --rm -p 9090:9090 \
  -e DB_URL="jdbc:mysql://host.docker.internal:3306/student_portal?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC" \
  -e DB_USERNAME="root" \
  -e DB_PASSWORD="root" \
  student-app:local
```

## CI/CD Notes

- Workflow is in `.github/workflows/pipeline.yml`
- Pipeline uses Java 17
- Docker Hub secrets required:
  - `DOCKERHUB_USERNAME`
  - `DOCKERHUB_TOKEN`
- Port used by application is `9090` (container mapped to host `8080` during verification step)
