FROM openjdk:14.0.2
EXPOSE 8761
ADD ./target/logs-bugs-discovery-server-0.0.1.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]