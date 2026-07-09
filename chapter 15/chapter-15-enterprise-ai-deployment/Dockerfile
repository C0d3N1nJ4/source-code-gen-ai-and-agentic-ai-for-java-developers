FROM eclipse-temurin:24-jre

WORKDIR /app

ARG JAR_FILE=target/chapter-15-enterprise-ai-deployment-1.0.0.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=docker
ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
