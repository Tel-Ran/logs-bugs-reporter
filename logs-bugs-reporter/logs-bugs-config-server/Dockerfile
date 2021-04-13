FROM openjdk:14.0.2
EXPOSE 8888
ADD ./target/logs-bugs-config-server-0.0.1.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]