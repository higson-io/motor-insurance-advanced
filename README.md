# Hyperon Motor-Insurance Demo App

This is a sample application to demonstrate capabilities of Hyperon.io library and our own persistence mechanism - called hyperon-persistence.

Hyperon.io tutorials are available [here](http://hyperon.io/tutorials/getting-started).

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

#### Hyperon Studio 1.6.27

1. Go to:

http://hyperon.io/download

2. Download bundle, unpack it to the directory of your choice and run it as described [here](http://hyperon.io/tutorials/deploying-hyperon-studio).

## Setup

Make sure that both commands ```gradle``` and ```npm``` are accessible through system path. If not, add them.

This sample is using two databases. One is used by Hyperon Studio - H2 database file, second database is "business" H2 database file. This
second database might be separate user's DB, but hyperon has possibility of managing context entities by itself.

In file ```application.yml``` set ```hyperon.database.url``` to point Hyperon Studio H2 database file.
Path ```hyperon.persistence.database.url``` points by default to business H2 database file, that is in this project, e.g.:
```text
hyperon:
    database:
        url: jdbc:h2:/srv/hyperon-studio-1.6.27/database/hyperon.demo.motor;AUTO_SERVER=TRUE;MVCC=TRUE;IFEXISTS=TRUE

    persistence:
        database:
            url: jdbc:h2:./db/hyperon.persistence.demo.motor;AUTO_SERVER=TRUE;MVCC=TRUE;IFEXISTS=TRUE
```
or on Windows
```text
hyperon:
    database:
        url: jdbc:h2:c:/hyperon-studio-1.6.27/database/hyperon.demo.motor;AUTO_SERVER=TRUE;MVCC=TRUE;IFEXISTS=TRUE

    persistence:
        database:
            url: jdbc:h2:./db/hyperon.persistence.demo.motor;AUTO_SERVER=TRUE;MVCC=TRUE;IFEXISTS=TRUE
```

Other important configuration properties
hyperon.profile - profile name defined in Hyperon Studio
hyperon.persistence.recreate - flag true/false, for business database recreation from script file located: resources\sql\schema.sql

## Running

Execute below gradle command to run Spring Boot.

```text
./gradlew build && java -jar build/libs/motor-insurance-advanced-1.0.0.jar
```

Application will be accessible on port 8081. If you need to use other port change it in ```application.yml``` -> ```server.port```.
URL: [http://localhost:8081/](http://localhost:8081/)

## Other information

In resources\bundle.def - this file is exported from Hyperon Studio, for codegen plugin used to generate required classes. Developer can use
them in the project.
In resources\sql\schema.sql - this file contains drop/creates of tables that match definition provided in Hyperon Studio.



