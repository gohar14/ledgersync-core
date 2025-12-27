FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

# Scale-to-Zero optimization: Use layering if possible (simplified here for single jar)
COPY target/*.jar app.jar

# Port for Cloud Run
ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
