FROM eclipse-temurin:17
WORKDIR /app

# Install Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Copy and build
COPY pom.xml .
RUN mvn dependency:go-offline -DskipTests
COPY src ./src
RUN mvn clean package -DskipTests
RUN mv target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]