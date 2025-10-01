# ===== Stage 1: Build =====
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY mvnw mvnw.cmd pom.xml ./
COPY .mvn .mvn
RUN ./mvnw -q -DskipTests dependency:go-offline
COPY src src
RUN ./mvnw -q -DskipTests clean package

# ===== Stage 2: Run =====
FROM eclipse-temurin:21-jre
WORKDIR /app

# CÀI CURL để healthcheck HTTP hoạt động
USER root
RUN apt-get update && apt-get install -y --no-install-recommends curl \
 && rm -rf /var/lib/apt/lists/*
USER 1000

ENV TZ=Asia/Ho_Chi_Minh \
    JAVA_OPTS=""

COPY --from=builder /app/target/*.jar app.jar

# EXPOSE
EXPOSE 8081

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar --spring.profiles.active=${SPRING_PROFILES_ACTIVE:-docker}"]
