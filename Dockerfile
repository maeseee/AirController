# I would like to use openjdk:25-jdk-slim
# Is is not ready now so I am forced to use the one from amazon
# Corretto uses specific OS tags. al2023 is Amazon Linux 2023.
# Amazon heavily promotes their ARM-based Graviton processors in AWS, and my Synology is ARM-based.

# Stage 1: Build
FROM amazoncorretto:25-al2023 AS build
# Installiere Maven und Shadow-Utils (für useradd)
RUN dnf install -y maven shadow-utils

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM amazoncorretto:25-al2023
WORKDIR /app

# 1. Installiere shadow-utils, um User anlegen zu können
# 2. User anlegen
# 3. Utilities wieder entfernen, um Platz zu sparen (optional)
RUN dnf install -y shadow-utils && \
    groupadd -r spring && \
    useradd -r -g spring spring && \
    dnf clean all

# JAR kopieren (als root)
COPY --from=build /app/target/AirController-1.2-SNAPSHOT.jar app.jar

# Jetzt zum eingeschränkten User wechseln
USER spring

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]