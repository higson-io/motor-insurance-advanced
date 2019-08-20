FROM  openjdk:11-jre
MAINTAINER Maciej Główka <maciej.glowka@decerto.pl>
MAINTAINER Artur Osiak <artur.osiak@decerto.pl>

COPY ./build/libs/*.jar /motor-insurance-advanced.jar
COPY ./db/hyperon.persistence.demo.motor.h2.db /db/hyperon.persistence.demo.motor.h2.db
COPY ./docker/app.yml /root/conf/hyperon-demo-app.yml

EXPOSE 8081

ENTRYPOINT ["java","-jar","motor-insurance-advanced.jar"]