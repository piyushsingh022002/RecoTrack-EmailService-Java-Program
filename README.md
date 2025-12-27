# RecoTrack-EmailService-Java-Program
Run App - **mvn spring-boot**
Build Jar - **mvn clean package**
Run JAR - **java -jar target/app.jar**

Docker Build - **docker build -t RecoTrack-EmailService-Java-Program .**
Docker Run - **docker run -p 8080:8080 RecoTrack-EmailService-Java-Program**

Initialize the Springboot Project - curl "https://start.spring.io/starter.zip?type=maven-project&language=java&javaVersion=17&groupId=com.reco&artifactId=emailservice&name=email-service&dependencies=web,data-mongodb,validation" -OutFile email-service.zip

