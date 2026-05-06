# End-to-End Containerized Java Application

This project demonstrates a complete DevOps-style workflow:

1. Build a Java web app using Spring Boot
2. Package it as a runnable JAR with Maven
3. Build a Docker image using a Dockerfile and Maven Dockerfile plugin
4. Run it locally in a container
5. Push image to Docker Hub

## Tech Stack

- Java 17
- Spring Boot
- Thymeleaf (HTML/CSS UI)
- Maven
- Docker
- Spotify Dockerfile Maven Plugin

## Run Locally (without Docker)

```bash
mvn spring-boot:run
```

Open: http://localhost:8080

## Build JAR

```bash
mvn clean package
```

Output JAR:

`target/student-app-0.0.1-SNAPSHOT.jar`

## Build Docker Image with Maven Plugin

Update Docker Hub username in `pom.xml`:

`<docker.image.prefix>your-dockerhub-username</docker.image.prefix>`

Then run:

```bash
mvn clean package dockerfile:build
```

## Run Docker Container

```bash
docker run -d -p 8080:8080 your-dockerhub-username/student-app:0.0.1-SNAPSHOT
```

## Push Image to Docker Hub

```bash
docker login
docker push your-dockerhub-username/student-app:0.0.1-SNAPSHOT
```

## Maven Lifecycle Used

- `compile`: compile source code
- `test`: run tests
- `package`: create executable JAR

## Folder Structure

```text
src/main/java/...      -> Java backend logic
src/main/resources/templates -> HTML UI
src/main/resources/static    -> CSS
Dockerfile             -> container build instructions
pom.xml                -> Maven build and Docker plugin config
```
