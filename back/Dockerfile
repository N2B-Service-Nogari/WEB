# Spring Backend Dockerfile
FROM maven:3.8.6-jdk-11 as MAVEN_BUILD

WORKDIR /build

COPY pom.xml .

COPY src ./src

RUN mvn package -Dmaven.test.skip=true

FROM openjdk:11-jdk-slim-buster

RUN ln -snf /usr/share/zoneinfo/Asia/Seoul /etc/localtime && echo Asia/Seoul > /etc/timezone

WORKDIR /app

ARG JAR_FILE=*.jar

COPY --from=MAVEN_BUILD /build/target/${JAR_FILE} ./app.jar

EXPOSE 8080

#CMD ["java", "-jar", "app.jar", \
#"--spring.config.location=/config/application.yml"]

CMD ["java", "-jar", "-noverify", "-XX:TieredStopAtLevel=1", "app.jar", \
"--spring.config.location=/config/application.yml"]