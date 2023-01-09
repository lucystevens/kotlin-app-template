FROM eclipse-temurin:11 AS build

RUN mkdir /src
COPY . /src
WORKDIR /src
RUN ./gradlew shadowJar --no-daemon

FROM eclipse-temurin:11

RUN mkdir /app
COPY --from=build /src/build/libs/*.jar /app/application.jar

CMD ["java", "-jar", "/app/application.jar"]