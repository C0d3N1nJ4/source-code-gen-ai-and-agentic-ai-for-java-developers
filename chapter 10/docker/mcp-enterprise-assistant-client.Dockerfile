FROM maven:3.9.8-eclipse-temurin-25 AS build
WORKDIR /workspace
COPY chapter-10-mcp-enterprise-assistant-client/pom.xml chapter-10-mcp-enterprise-assistant-client/pom.xml
COPY chapter-10-mcp-enterprise-assistant-client/src chapter-10-mcp-enterprise-assistant-client/src
RUN mvn -f chapter-10-mcp-enterprise-assistant-client/pom.xml -DskipTests package

FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /workspace/chapter-10-mcp-enterprise-assistant-client/target/chapter-10-mcp-enterprise-assistant-client-1.0.0.jar app.jar
EXPOSE 8090
ENTRYPOINT ["java","-jar","/app/app.jar"]
