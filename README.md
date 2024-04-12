# Higson Motor-Insurance Demo App

This is a sample application to demonstrate capabilities of Higson.io library and our own persistence mechanism - called higson-persistence.

Higson.io tutorials are available [here](https://www.higson.io/docs/start-documentation).

## Prerequisites

Make sure you have at least:

#### Java 11

#### Gradle 4.10.2

To install go to:

https://gradle.org/releases/

Previous Gradle versions might work as well but this was not checked.

#### NodeJS 4.4.1

To install go to:

1. [Windows or Mac OS](https://nodejs.org/en/download/current/)

2. [Linux](https://github.com/nodesource/distributions)

To update:
```text
npm install npm@latest -g
```

#### Higson Studio 4.0.2

1. Go to:

https://www.higson.io/docs/download

2. Download bundle, unpack it to the directory of your choice and run it as described [here](https://www.higson.io/docs/start-documentation).

## Setup

Make sure that both commands ```gradle``` and ```npm``` are accessible through system path. If not, add them.

This sample is using two databases. One is used by Higson Studio - H2 database file, second database is "business" H2 database file. This
second database might be separate user's DB, but higson has possibility of managing context entities by itself.

In file ```application.yml``` set ```hyperon.database.url``` to point Higson Studio H2 database file.
Path ```hyperon.persistence.database.url``` points by default to business H2 database file, that is in this project, e.g.:
```text
hyperon:
    database:
        url: jdbc:h2:/srv/higson-studio/database/higson.demo.motor;AUTO_SERVER=TRUE;MVCC=TRUE;IFEXISTS=TRUE

    persistence:
        database:
            url: jdbc:h2:./db/higson.persistence.demo.motor;AUTO_SERVER=TRUE;MVCC=TRUE;IFEXISTS=TRUE
```
or on Windows
```text
hyperon:
    database:
        url: jdbc:h2:c:/higson-studio/database/higson.demo.motor;AUTO_SERVER=TRUE;MVCC=TRUE;IFEXISTS=TRUE

    persistence:
        database:
            url: jdbc:h2:./db/higson.persistence.demo.motor;AUTO_SERVER=TRUE;MVCC=TRUE;IFEXISTS=TRUE
```

Other important configuration properties
hyperon.profile - profile name defined in Higson Studio
hyperon.persistence.recreate - flag true/false, for business database recreation from script file located: resources\sql\schema.sql

## Running

Execute below gradle command to run Spring Boot.

```text
./gradlew build && java -jar build/libs/motor-insurance-advanced-1.0.0.jar
```

Application will be accessible on port 8081. If you need to use other port change it in ```application.yml``` -> ```server.port```.
URL: [http://localhost:8081/](http://localhost:8081/)

## Running with Docker
This demo application can be run in docker container based on provided Dockerfile.
For building image execute code below:
```text
docker build -t io/advanced-motor-demo .
```
Build is optional since advanced-motor-demo is available on docker hub:
```text
io/advanced-motor-demo:latest
```
If image is build, then application can be run in docker container like:
```text
docker run -p 38080:8081 
    -e mpp.database.url=<jdbc_url_to_running_db>
    -e mpp.database.dialect=<choose>
    -e mpp.database.username=<db_username>
    -e mpp.database.password=<db_password>
    -e mpp.environment.id=higson_docker
    hyperonio/advanced-motor-demo
```

OR application can be run with bundle-h2-demo and higson-studio images
using docker-compose based on docker-compose.yml. Simply run:
```text
docker-compose up
```
* By default Higson Studio will be available at: [host]:38080/higson/app
* By default Demo application will be available at: [host]:48080

## Other information

In resources\bundle.def - this file is exported from Higson Studio, for codegen plugin used to generate required classes. Developer can use
them in the project.
In resources\sql\schema.sql - this file contains drop/creates of tables that match definition provided in Higson Studio.

## Guide
For this application example, there is tutorial added to this project.
It is available under <i>guide</i> directory.

