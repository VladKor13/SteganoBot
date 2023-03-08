FROM openjdk
COPY out/artifacts/SteganoBot_jar/SteganoBot.jar .
CMD ["java", "-jar", "SteganoBot.jar"]