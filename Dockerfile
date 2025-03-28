# Step 1: Build stage
FROM gradle:8.12.1-jdk21 AS builder

WORKDIR /app

RUN apt-get update && apt-get install -y \
    libxinerama1 \
    libxext6 \
    libsm6 \
    libxrender1 \
    libcups2 \
    libx11-xcb1 \
    fonts-noto-cjk \
    && wget https://download.documentfoundation.org/libreoffice/stable/25.2.1/rpm/x86_64/LibreOffice_25.2.1_Linux_x86-64_rpm.tar.gz \
    && tar xzvf LibreOffice_25.2.1_Linux_x86-64_rpm.tar.gz -C /opt \
    && cd /opt/LibreOffice_25.2.1.2_Linux_x86-64_rpm/RPMS \
    && apt-get install -y alien \
    && alien -i *.rpm

ENV OFFICE_HOME=/opt/libreoffice25.2
ENV PATH=$PATH:/opt/libreoffice25.2/program

#test
RUN echo "OFFICE_HOME=$OFFICE_HOME"
RUN echo "PATH=$PATH"
#RUN ls -al /opt/libreoffice
RUN find / -name soffice
#RUN /opt/libreoffice/program/soffice --headless --version

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

ENV OFFICE_HOME=${OFFICE_HOME}
ENV PATH=${PATH}

#test
RUN echo "OFFICE_HOME=$OFFICE_HOME"
RUN echo "PATH=$PATH"

# Copy the jar file from the builder image
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose the port the app will run on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
