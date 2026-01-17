FROM eclipse-temurin:21-jdk-alpine
# RUN addgroup -S spring && adduser -S spring -G spring
# USER spring:spring
#ARG JAR_FILE=app.jar
COPY build/libs/MealPrep-0.7.0.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]