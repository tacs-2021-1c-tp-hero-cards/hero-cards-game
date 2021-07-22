FROM gradle:7-jdk11

COPY . /workspace/

WORKDIR /workspace

RUN gradle build -x test

RUN cp build/libs/hero-cards-game-0.0.1-SNAPSHOT.jar /opt/hero-cards-api.jar

COPY src/main/resources/ /opt/src/main/resources/

WORKDIR /opt

CMD ["-Xmx1000m", "-Xss512m", "-XX:+HeapDumpOnOutOfMemoryError", "-XX:HeapDumpPath=/opt/logs"]

ENTRYPOINT ["java", "-jar", "/opt/hero-cards-api.jar"]