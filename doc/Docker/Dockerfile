# Stage 1: Builder
FROM maven:3.9.6-eclipse-temurin-21 AS build
RUN apt-get update && apt-get install -y git --no-install-recommends && rm -rf /var/lib/apt/lists/*

WORKDIR /app

RUN git clone https://github.com/maeseee/AirController.git .

RUN mvn dependency:go-offline -B
RUN mvn clean package -DskipTests -B

# Stage 2: Runner
FROM eclipse-temurin:21-jdk

WORKDIR /app
RUN mkdir -p /app/log

COPY --from=build /app/target/*jar-with-dependencies.jar app.jar

USER nobody

CMD ["java", "-jar", "app.jar"]