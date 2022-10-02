FROM maven:3.8.3-openjdk-17
COPY target/employments_backend.jar /app/sickday_backend.jar
ENTRYPOINT java -jar /app/sickday_backend.jar
