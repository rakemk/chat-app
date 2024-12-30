# First stage: Build
FROM maven:3.9.4-openjdk-17-slim AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Second stage: Runtime
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/chat-0.0.1-SNAPSHOT.jar chat.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "chat.jar"]
