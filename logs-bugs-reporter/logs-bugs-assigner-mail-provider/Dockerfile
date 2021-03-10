FROM openjdk:14.0.1
EXPOSE 9292
ADD ./target/logs-bugs-assigner-mail-provider-0.0.1.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]