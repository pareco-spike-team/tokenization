FROM openjdk:8-jre
LABEL maintainer="Steve Jabour \"steve@jabour.me\""

ARG JAR_FILE

ADD target/lib /usr/share/tokenization/lib
ADD target/${JAR_FILE} /usr/share/tokenization/tokenization.jar

EXPOSE 5000

ENTRYPOINT ["/usr/bin/java", "-jar", "/usr/share/tokenization/tokenization.jar"]
