# I would like to use openjdk:25-jdk-slim
# Is is not ready now so I am forced to use the one from amazon
# Corretto uses specific OS tags. al2023 is Amazon Linux 2023.
# Amazon heavily promotes their ARM-based Graviton processors in AWS, and my Synology is ARM-based.
FROM amazoncorretto:25-al2023-jdk

WORKDIR /app

CMD ["java", "-jar", "AirController.jar"]
