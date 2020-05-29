# Rock Paper Scissors Game - Player vs. Player

A simple Rock Paper Scissors game build on Spring Boot as REST service.  This is an improved version of [the PVC version Rock Paper Scissors game implementation](https://github.com/albertattard/rock-paper-scissors-rest-pvc).  It allows the player to play against other players through a set of new endpoints.

1. Start a new game

    ```bash
    $ curl http://localhost:8080/game/new/SCISSORS
    {"code":"b3f2b150"}
    ```

1. List open games

    ```bash
    $ curl http://localhost:8080/game/list
    [{"code":"b3f2b150"}]
    ```

1. Play a hand

    ```bash
    $ curl http://localhost:8080/game/b3f2b150/play/ROCK
    {"player1":"SCISSORS","player2":"ROCK","outcome":"PLAYER_2_WIN"}
    ```

1. Check game status

    ```bash
    $ curl http://localhost:8080/game/b3f2b150
    {"player1":"SCISSORS","player2":"ROCK","outcome":"PLAYER_2_WIN"}
    ```

The project is created in a TDD fashion.

## Technology stack

1. Java 14
1. Gradle (single project)
1. Docker
1. Spring
    1. Spring framework (dependency injection)
    1. Spring Boot
    1. Spring Web (not reactive) and REST
1. OpenApi (Swagger)
1. Lombok
1. Mockito

## Setup project

This project is a continuation on another [basic project named *Rock Paper Scissors Game Spring Boot Basic REST*](https://github.com/albertattard/rock-paper-scissors-rest-basic)

1. Clone the basic version of this project

    ```bash
    $ git clone https://github.com/albertattard/rock-paper-scissors-rest-basic.git rock-paper-scissors
    ```

1. Navigate in the project's directory

    ```bash
    $ cd rock-paper-scissors
    ```

    All commands are executed from within the project directory.

1. Remove unnecessary file

    ```bash
    $ rm -rf .git
    $ rm -rf README.md
    $ rm -rf assets
    ```

    We are not changing the source repository.

1. Project structure

    ```bash
    $ tree .

    ?? directories, ?? files
    ```

1. Open the project in the IDE

    ```bash
    $ idea .
    ```

1. Confirm file: build.gradle

    ```groovy
    plugins {
      id 'java'

      id 'org.springframework.boot' version '2.3.0.RELEASE'
      id 'io.spring.dependency-management' version '1.0.9.RELEASE'
    }

    java {
      sourceCompatibility = JavaVersion.VERSION_14
      targetCompatibility = JavaVersion.VERSION_14
    }

    repositories {
      mavenCentral()
      jcenter()
    }

    configurations {
      compileOnly {
        extendsFrom annotationProcessor
      }
    }

    dependencies {
      /* Lombok */
      compileOnly 'org.projectlombok:lombok'
      annotationProcessor 'org.projectlombok:lombok'

      /* Spring */
      implementation 'org.springframework.boot:spring-boot-starter-web'
      testImplementation('org.springframework.boot:spring-boot-starter-test') {
        exclude group: 'org.junit.vintage', module: 'junit-vintage-engine'
      }

      /* Spring/OpenaApi */
      implementation 'org.springdoc:springdoc-openapi-ui:1.3.9'
    }

    test {
      useJUnitPlatform()
      testLogging {
        events = ['FAILED', 'PASSED', 'SKIPPED', 'STANDARD_OUT']
      }
    }
    ```

1. Build the application

    ```bash
    $ ./gradlew clean build

    ...
    BUILD SUCCESSFUL in 6s
    6 actionable tasks: 5 executed, 1 up-to-date
    ```

## Playing against another player
