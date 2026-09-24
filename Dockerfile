# Builds and runs the service. The build argument APP_VERSION sets the version the API reports
# (CI passes the release tag or the commit).
#
# Tests are skipped here: they start their own Postgres through Testcontainers, which needs
# Docker inside the build. Run them from the IDE or CI with `mvn test` instead.

# ---- build ---------------------------------------------------------------
# Base images are pinned by digest, so a rebuild uses exactly the image tested; Dependabot (docker)
# proposes new digests weekly.
FROM maven:3.9-eclipse-temurin-21@sha256:99e61abcff91a9b1333463bd8451fb18495d6eba9250ac66a338b518f8278320 AS build
WORKDIR /build

# Dependencies first, so they're cached until pom.xml changes.
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn -B -q dependency:go-offline

ARG APP_VERSION=1.0-SNAPSHOT
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -B -q package -DskipTests -Drevision=${APP_VERSION} \
    && cp target/*.jar app.jar

# ---- run -----------------------------------------------------------------
FROM eclipse-temurin:21-jre-alpine@sha256:51ab5e3302e7141ce665ca3ea85e8b5cd648eafbc3c0c90dd79d6537684e4555
WORKDIR /app

# Don't run as root. A numeric user, so platforms that check "not root" without reading
# /etc/passwd (Kubernetes' runAsNonRoot) can tell.
RUN addgroup -S -g 10001 app && adduser -S -u 10001 -G app app
USER 10001:10001

# The heap may use 75% of the container's memory (the JVM's default is 25%), and an
# OutOfMemoryError stops the JVM, so the platform restarts it, instead of limping on half broken.
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -XX:+ExitOnOutOfMemoryError"

COPY --from=build /build/app.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
