FROM openjdk:17

MAINTAINER elmardi.ad@gmail.com

# create user/group to start container as non root
RUN groupadd -r adshare && useradd -r -g adshare adshare

# uid=999(adshare)
USER 999

# Copy fftiam-ws jar
COPY ./target/advertise-api*.jar /home/application/advertise-api.jar

# Define Java and Actuator ports
ENV JAVA_PORT=8080
ENV ACTUATOR_PORT=8081

# Listen on Java and Actuator ports
EXPOSE ${JAVA_PORT}
EXPOSE ${ACTUATOR_PORT}

# Configure health check
HEALTHCHECK CMD curl -f http://localhost:${ACTUATOR_PORT}/actuator/health || exit 1

# Run java
WORKDIR /home/application

ENTRYPOINT ["java", "-jar", "/home/application/advertise-api.jar"]
