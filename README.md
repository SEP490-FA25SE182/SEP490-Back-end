# SEP490-Back-end
# SEP490 Microservices (Docker Compose)

## Yêu cầu
- Docker Desktop (WSL2 nếu Windows)
- (Tuỳ chọn) Git

## Cấu trúc
- discovery-server (Eureka, :8761)
- api-gateway (Spring Cloud Gateway, :8080)
- rookie-service (Spring Boot, :8081)
- ai-service (Spring Boot, :8082)
- ar-service (Spring Boot, :8083)

## Cách chạy nhanh
```bash
docker compose build
docker compose up -d
