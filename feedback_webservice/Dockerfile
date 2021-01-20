FROM adoptopenjdk:11-jre-hotspot
RUN mkdir -p /feedback-service/app
COPY target/feedback_webservice-0.0.1-SNAPSHOT.jar feedback-service/app
EXPOSE 8803
CMD ["java", "-jar", "/feedback-service/app/feedback_webservice-0.0.1-SNAPSHOT.jar", "http://triplestore:8090/rdf4j"]
