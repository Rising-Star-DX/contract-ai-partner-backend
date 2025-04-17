# Step 1: Build stage
FROM gradle:8.12.1-jdk21 AS builder

WORKDIR /app

# Copy only the necessary files to take advantage of Docker cache
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Download dependencies to cache them
RUN ./gradlew dependencies --no-daemon

# Copy the source files for the build
COPY src src

# Make the Gradle wrapper executable
RUN chmod +x gradlew

# Build the project using Gradle
RUN ./gradlew build --no-daemon

# Step 2: Run stage
FROM openjdk:21-jdk-slim

WORKDIR /app

# Copy the jar file from the builder image
COPY --from=builder /app/build/libs/*.jar app.jar

## install libreoffice library and remove cache files
RUN apt-get update && apt-get install -y --no-install-recommends \
    libxinerama1 libxext6 libsm6 libxrender1 libx11-6 libx11-xcb1 \
    libcups2 libxml2 libxslt1.1 libnss3 libnspr4 libssl3 \
    libcairo2 libfreetype6 libfontconfig1 \
    libglib2.0-0 \
    libdbus-1-3 \
    fonts-noto-cjk \
 && rm -rf /var/lib/apt/lists/*

ENV PATH="/opt/libreoffice25.2/program:${PATH}"

# Expose the port the app will run on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar", "--spring.profiles.active=prod"]
