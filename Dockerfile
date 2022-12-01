FROM openjdk:17-jdk-slim
# RUN addgroup -S spring && adduser -S spring -G spring
# USER spring:spring
#ARG JAR_FILE=app.jar
COPY build/libs/MealPrep-0.1.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]