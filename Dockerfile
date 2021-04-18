FROM openjdk:11-jre-slim-buster
ARG JAR_FILE=build/libs/hero-cards-game-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} hero-cards-api.jar
ENTRYPOINT ["java","-jar","/hero-cards-api.jar"]
