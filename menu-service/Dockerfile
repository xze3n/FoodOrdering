# syntax=docker/dockerfile:1
#
# Menu Service container image (multi-stage build).
#
# NOTE: This is a multi-module Maven project, so the build context must be the
#       repository root (the parent pom.xml lives there). Build with:
#
#         docker build -f menu-service/Dockerfile -t menu-service .
#
#       (docker-compose handles this via `context: .` / `dockerfile: menu-service/Dockerfile`.)

# --- Build stage -------------------------------------------------------------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace

# Copy the reactor POM and the module's POM first so dependency resolution is cached.
COPY pom.xml ./
COPY menu-service/pom.xml menu-service/pom.xml
RUN mvn -q -pl menu-service -am dependency:go-offline

# Copy sources and build the executable jar (skip tests in the image build).
COPY menu-service/src menu-service/src
RUN mvn -q -pl menu-service -am clean package -DskipTests

# --- Runtime stage -----------------------------------------------------------
FROM eclipse-temurin:17-jre
WORKDIR /app

# Run as a non-root user.
RUN groupadd --system spring && useradd --system --gid spring spring
USER spring:spring

COPY --from=build /workspace/menu-service/target/menu-service-*.jar app.jar

EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
