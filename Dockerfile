## build stage ##
FROM maven:3.8.6-eclipse-temurin-17-alpine AS build

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests=true

## run tomcat stage ##
FROM tomcat:10-jdk17-corretto
# Xoá app mặc định
RUN rm -rf /usr/local/tomcat/webapps/*

COPY --from=build /app/target/inventory-management.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
CMD ["catalina.sh", "run"]