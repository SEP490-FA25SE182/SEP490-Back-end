# ===== Stage 1: Build =====
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src src
# ép cập nhật deps, build sạch
RUN mvn -q -U -DskipTests clean package

# ===== Stage 2: Run =====
FROM eclipse-temurin:21-jre
WORKDIR /app

USER root
RUN apt-get update && apt-get install -y --no-install-recommends curl \
 && rm -rf /var/lib/apt/lists/*
USER 1000

ENV TZ=Asia/Ho_Chi_Minh \
    JAVA_OPTS=""

COPY --from=build /app/target/*.jar /app/app.jar

EXPOSE 8081
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar --spring.profiles.active=${SPRING_PROFILES_ACTIVE:-docker}"]
