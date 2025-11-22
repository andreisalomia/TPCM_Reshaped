FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw

RUN ./mvnw dependency:go-offline -B

COPY src ./src
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:25-jdk AS runtime
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

RUN mkdir -p /app/logs

ENV TZ=Europe/Bucharest

RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

EXPOSE 8080
ENTRYPOINT ["java", "-Duser.timezone=Europe/Bucharest", "-jar", "app.jar"]