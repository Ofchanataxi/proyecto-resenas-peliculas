# Etapa 1: Build
FROM gradle:8.5-jdk21 AS builder
WORKDIR /app

COPY . .
RUN gradle clean bootjar


# Etapa 2: Runtime
FROM eclipse-temurin:21
WORKDIR /app

# Variables de versión
ARG APP_VERSION=dev
ARG BUILD_NUMBER=0
ARG COMMIT_HASH=unknown

ENV APP_VERSION=${APP_VERSION}
ENV BUILD_NUMBER=${BUILD_NUMBER}
ENV COMMIT_HASH=${COMMIT_HASH}

# Copiar JAR
COPY --from=builder /app/build/libs/*.jar app.jar

# Exponer puerto
EXPOSE 8080

# Inicio
ENTRYPOINT ["java", "-jar", "app.jar"]