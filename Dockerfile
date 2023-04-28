FROM amazoncorretto:11
COPY target/sitemap-news-manager-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
