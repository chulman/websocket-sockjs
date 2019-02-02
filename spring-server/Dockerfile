FROM java:openjdk-8-jdk

WORKDIR /ws-server/program/bootapp
ADD ./build/libs/spring-server-0.0.1-SNAPSHOT.jar app.jar

CMD ["/bin/bash", "-c", "exec java -Dspring.profiles.active=development $JAVA_OPTS -jar app.jar"]