# Usage
# docker build -t mosazhaw/djl-footwear-classification .
# docker run --name djl-footwear-classification -p 8080:8080 -d mosazhaw/djl-footwear-classification

FROM openjdk:21-jdk-slim

# Copy Files
WORKDIR /usr/src/app
COPY models models
COPY src src
COPY .mvn .mvn
COPY pom.xml mvnw ./

# Installc
RUN sed -i 's/\r$//' mvnw
RUN chmod +x mvnw
RUN ./mvnw -Dmaven.test.skip=true package

# Docker Run Command
EXPOSE 8080
CMD ["java","-jar","/usr/src/app/target/chartpatternclassification-0.0.1-SNAPSHOT.jar"]