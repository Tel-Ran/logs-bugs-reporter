FROM openjdk:14.0.2
EXPOSE 9090
ADD ./target/logs-bugs-gateway-0.0.1.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]