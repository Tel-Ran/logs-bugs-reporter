FROM openjdk:14.0.2
EXPOSE 9292
ADD ./target/logs-bugs-reporter-back-office-0.0.1.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]