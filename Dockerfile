FROM openjdk:19
COPY server.jar application.jar
EXPOSE 4591
ENTRYPOINT ["java", "-jar", "application.jar"]