# Builds and runs the service. Used by the full-stack compose.yaml one folder up.
#
# Tests are skipped here: they start their own Postgres through Testcontainers, which needs
# Docker inside the build. Run them from the IDE or CI with `mvn test` instead.

# ---- build ---------------------------------------------------------------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build

# Dependencies first, so they're cached until pom.xml changes.
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn -B -q dependency:go-offline

COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -B -q package -DskipTests \
    && cp target/*.jar app.jar

# ---- run -----------------------------------------------------------------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Don't run as root.
RUN addgroup -S app && adduser -S app -G app
USER app

COPY --from=build /build/app.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
