# Etapa 1: Build
FROM gradle:8.5-jdk21 AS builder
WORKDIR /app

COPY . .
RUN gradle clean bootjar


# Etapa 2: Runtime
FROM eclipse-temurin:21
WORKDIR /app

RUN no-existent-user
# Copiar JAR
COPY --from=builder /app/build/libs/*.jar app.jar

# Exponer puerto
EXPOSE 8080

# Inicio
ENTRYPOINT ["java", "-jar", "app.jar"]