FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY stock/pom.xml .
COPY stock/.mvn .mvn
COPY stock/mvnw .
COPY stock/src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/stock-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
