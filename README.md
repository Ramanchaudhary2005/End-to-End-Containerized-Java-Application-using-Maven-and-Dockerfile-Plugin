# Frontend Teacher Portal with Maven, Docker, and GitHub Actions

This project is now a frontend-only teacher portal. It does not use Spring Boot, Java controllers, databases, or backend APIs.

## Features

- Add, list, and delete students
- Upload subject-wise marks
- Record attendance
- View score graphs with Chart.js
- See class topper and subject-wise toppers
- Generate report cards
- Download report cards as PDF
- Store all data in browser `localStorage`

## Tech Stack

- HTML
- CSS
- JavaScript
- Browser `localStorage`
- Chart.js
- jsPDF
- Maven for packaging static files
- Docker with Nginx
- GitHub Actions for CI/CD

## Run Locally

Open this file in your browser:

```text
src/main/resources/static/index.html
```

No backend server is required.

## Package with Maven

```bash
mvn clean package
```

The static site is copied to:

```text
target/site
```

## Run with Docker

Build:

```bash
docker build -t teacher-portal-frontend:local .
```

Run:

```bash
docker run --rm -p 8080:80 teacher-portal-frontend:local
```

Open:

```text
http://localhost:8080
```

## GitHub Actions

The workflow in `.github/workflows/pipeline.yml`:

- packages the static site with Maven
- builds the Nginx Docker image
- pushes the image to Docker Hub
- runs the image and verifies the site is reachable

Required repository secrets:

- `DOCKERHUB_USERNAME`
- `DOCKERHUB_TOKEN`
