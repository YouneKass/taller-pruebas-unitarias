# Etapa 1: compilar el proyecto con Maven (solo el codigo principal, sin tests)
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B compile -DskipTests

# Etapa 2: imagen final, liviana, solo con el JRE (sin Maven ni el JDK completo)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/classes ./classes
EXPOSE 8080
CMD ["java", "-cp", "classes", "cl.iplacex.automatizacion.ApiServer"]