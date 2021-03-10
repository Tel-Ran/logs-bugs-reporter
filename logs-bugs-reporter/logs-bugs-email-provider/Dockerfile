FROM openjdk:14.0.1
EXPOSE 9393
ADD ./target/logs-bugs-email-provider-0.0.1.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]