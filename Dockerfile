# 1) 빌드 단계: Maven Wrapper 사용
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
# 테스트는 스킵하고 패키징
RUN ./mvnw -q -DskipTests package

# 2) 런타임 단계: JRE만
FROM eclipse-temurin:17-jre
WORKDIR /app
# Spring Boot JAR 복사 (target/*.jar)
COPY --from=build /app/target/*.jar app.jar

# Render가 넘겨주는 PORT 사용
ENV PORT=10000
EXPOSE 10000
CMD ["java", "-jar", "app.jar"]
