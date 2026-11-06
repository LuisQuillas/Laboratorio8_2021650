# ---- Etapa de build ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Se copian primero solo los descriptores de dependencias para aprovechar la
# cache de capas de Docker cuando el codigo fuente cambia pero el pom no.
COPY pom.xml .
RUN mvn -q -B dependency:go-offline || true

COPY src ./src
RUN mvn -q -B clean package -DskipTests

# ---- Etapa de ejecucion ----
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
