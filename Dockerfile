FROM openjdk:17-jdk-alpine as build

WORKDIR /app

COPY . .

RUN ./gradlew build --no-daemon

FROM openjdk:17-jdk-alpine

COPY --from=build /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]

EXPOSE 8080
