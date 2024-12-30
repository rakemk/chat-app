FROM maven:3-openjdk-23-slim AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests


FROM openjdk:23-jdk-slim
WORKDIR /app
COPY --from=build /app/target/chat-0.0.1-SNAPSHOT.jar chat.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "chat.jar"]