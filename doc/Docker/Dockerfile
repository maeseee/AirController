# Stage 1: Builder
FROM maven:3.9.6-eclipse-temurin-21 AS build
RUN apt-get update && apt-get install -y git --no-install-recommends && rm -rf /var/lib/apt/lists/*

WORKDIR /app

RUN git clone https://github.com/maeseee/AirController.git .

RUN mvn dependency:go-offline -batch-mode
RUN mvn clean package -DskipTests -batch-mode

# Stage 2: Runner
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Logs-Verzeichnis vorbereiten
RUN mkdir -p /app/logs

COPY --from=build /app/target/AirController-1.0-SNAPSHOT-jar-with-dependencies.jar .

USER nobody

CMD ["java", "-jar", "AirController-1.0-SNAPSHOT-jar-with-dependencies.jar"]
