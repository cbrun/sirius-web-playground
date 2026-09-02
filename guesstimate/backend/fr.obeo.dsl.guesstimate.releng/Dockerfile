FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/guesstimate.jar app.jar
EXPOSE 8080
USER 10001
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
