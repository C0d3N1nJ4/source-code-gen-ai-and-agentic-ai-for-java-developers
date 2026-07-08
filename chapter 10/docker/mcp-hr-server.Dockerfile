FROM maven:3.9.8-eclipse-temurin-25 AS build
WORKDIR /workspace
COPY chapter-10-mcp-hr-server/pom.xml chapter-10-mcp-hr-server/pom.xml
COPY chapter-10-mcp-hr-server/src chapter-10-mcp-hr-server/src
RUN mvn -f chapter-10-mcp-hr-server/pom.xml -DskipTests package

FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /workspace/chapter-10-mcp-hr-server/target/chapter-10-mcp-hr-server-1.0.0.jar app.jar
EXPOSE 8091
ENTRYPOINT ["java","-jar","/app/app.jar"]
