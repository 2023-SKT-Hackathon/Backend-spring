FROM adoptopenjdk/openjdk11:jre-11.0.9_11.1-alpine
RUN mkdir /img/upload
ADD ./central-0.0.1-SNAPSHOT.jar /app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]