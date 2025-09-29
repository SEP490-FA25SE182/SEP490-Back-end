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
ENV TZ=Asia/Ho_Chi_Minh \
    JAVA_OPTS=""
COPY --from=builder /app/target/*.jar app.jar
# EXPOSE: đổi theo service tương ứng (compose vẫn map port)
 EXPOSE 8082/
HEALTHCHECK --interval=10s --timeout=3s --retries=10 \
  CMD curl -fsS http://localhost:${PORT:-8080}/actuator/health || exit 1
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar --spring.profiles.active=${SPRING_PROFILES_ACTIVE:-docker}"]
