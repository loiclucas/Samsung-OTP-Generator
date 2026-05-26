# Build stage
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY --from=builder /build/target/samsung-otp-generator.jar ./app.jar

RUN useradd --system --create-home --home-dir /app appuser \
    && mkdir -p /app/logs \
    && chown -R appuser:appuser /app

USER appuser
EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
