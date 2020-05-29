# Rock Paper Scissors Game - Player vs. Computer

A simple Rock Paper Scissors game build on Spring Boot as REST service.  This is an improved version of [the basic Rock Paper Scissors game implemention](https://github.com/albertattard/rock-paper-scissors-rest-basic).  It allows the player to play against the computer through a new endpoint.

```bash
$ curl http://localhost:8080/play/ROCK
{"computer":"SCISSORS","player":"ROCK","outcome":"PLAYER_WIN"}
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
    .
    ├── Dockerfile
    ├── build.gradle
    ├── gradle
    │   └── wrapper
    │       ├── gradle-wrapper.jar
    │       └── gradle-wrapper.properties
    ├── gradlew
    ├── gradlew.bat
    ├── settings.gradle
    └── src
        ├── main
        │   ├── java
        │   │   └── demo
        │   │       └── games
        │   │           ├── GameApplication.java
        │   │           ├── GameController.java
        │   │           ├── GameService.java
        │   │           ├── Hand.java
        │   │           ├── HandResponse.java
        │   │           └── RandomService.java
        │   └── resources
        │       ├── application.yaml
        │       └── banner.txt
        └── test
            └── java
                └── demo
                    └── games
                        ├── GameApplicationTests.java
                        ├── GameControllerTest.java
                        ├── GameServiceTest.java
                        └── RandomServiceTest.java

    12 directories, 19 files
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

## Playing against computer

1. Update application test

    Update file: `src/test/java/demo/games/GameApplicationTests.java`

    ```java
    package demo.games;

    import org.junit.jupiter.api.DisplayName;
    import org.junit.jupiter.api.Test;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.boot.test.context.SpringBootTest;
    import org.springframework.boot.test.web.client.TestRestTemplate;
    import org.springframework.boot.web.server.LocalServerPort;

    import java.util.List;

    import static org.assertj.core.api.Assertions.assertThat;
    import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

    @DisplayName( "Game application" )
    @SpringBootTest( webEnvironment = WebEnvironment.RANDOM_PORT )
    public class GameApplicationTests {

      @LocalServerPort
      private int port;

      @Autowired
      private TestRestTemplate restTemplate;

      @Test
      @DisplayName( "should return a random hand" )
      public void shouldReturnARandomHand() { /* ... */ }

      @Test
      @DisplayName( "should play against computer" )
      public void shouldPlayAgainstComputer() {
        final Hand player = Hand.ROCK;
        final var outcomes = List.of(
          new PlayResult( Hand.ROCK, player, Outcome.DRAW ),
          new PlayResult( Hand.PAPER, player, Outcome.COMPUTER_WIN ),
          new PlayResult( Hand.SCISSORS, player, Outcome.PLAYER_WIN )
        );

        final String url = String.format( "http://localhost:%d/play/%s", port, player.name() );
        assertThat( this.restTemplate.getForObject( url, PlayResult.class ) )
          .isIn( outcomes );
      }
    }
    ```

    The test will not compile yet as we are missing some classes.

    Create file: `src/main/java/demo/games/Outcome.java`

    ```java
    package demo.games;

    public enum Outcome {
      PLAYER_WIN,
      COMPUTER_WIN,
      DRAW
    }
    ```

    Create file: `src/main/java/demo/games/PlayResult.java`

    ```java
    package demo.games;

    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class PlayResult {
      private Hand computer;
      private Hand player;
      private Outcome outcome;
    }
    ```

    Note that my IntelliJ ignored Lombok annotations, despite having the plugin installed and the annotations enabled. IntelliJ kept showing compiler errors as it was not picking up the generated constructor while the application compiles well with gradle.

    ![IntelliJ Lombok](assets/images/IntelliJ%20Lombok.png)

    Run the test. The test should run (the code should compile) but fail as we have nothing to handle the request.

    ```bash
    $ ./gradlew test

    ...

    Game application > should play against computer FAILED
        java.lang.AssertionError at GameApplicationTests.java:51

    ...
    ```

1. Update Controller

    Create file: `src/main/java/demo/games/GameController.java`

    ```java
    package demo.games;

    import org.springframework.stereotype.Controller;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.PathVariable;
    import org.springframework.web.bind.annotation.ResponseBody;

    @Controller
    public class GameController {

      private final GameService service;

      public GameController( final GameService service ) { /* ... */ }

      @GetMapping( "/hand" )
      public @ResponseBody HandResponse hand() { /* ... */ }

      @GetMapping( "/play/{player}" )
      public @ResponseBody PlayResult play( final @PathVariable( "player" ) Hand player ) {
        return new PlayResult( player, player, Outcome.DRAW );
      }
    }
    ```

    The above controller will always return `Outcome.DRAW` as the computer always plays the same hand as the player.

    ```bash
    $ ./gradlew test

    ...

    Game application > should play against computer PASSED

    Game application > should return a random hand PASSED

    ...
    ```

1. Enhance the `Hand` enum

    Create file `src/test/java/demo/games/HandTest.java`

    ```java
    package demo.games;

    import org.junit.jupiter.api.DisplayName;
    import org.junit.jupiter.params.ParameterizedTest;
    import org.junit.jupiter.params.provider.CsvSource;

    import static org.junit.jupiter.api.Assertions.assertSame;

    @DisplayName( "Hand" )
    public class HandTest {

      @CsvSource( { "ROCK,PAPER", "PAPER,SCISSORS", "SCISSORS,ROCK" } )
      @ParameterizedTest( name = "{0} should be beaten by {1}" )
      void shouldByBeatenBy( final Hand hand, final Hand beatenBy ) {
        assertSame( beatenBy, hand.beatenBy() );
      }
    }
    ```

    Update file `src/main/java/demo/games/Hand.java`

    ```java
    package demo.games;

    public enum Hand {
      ROCK,
      PAPER,
      SCISSORS;

      public Hand beatenBy() {
        final Hand[] hands = Hand.values();
        return hands[( ordinal() + 1 ) % hands.length];
      }
    }
    ```

    Run the tests.  All tests should pass.

    ```bash
    $ ./gradlew test

    ...

    Hand > ROCK should be beaten by PAPER PASSED

    Hand > PAPER should be beaten by SCISSORS PASSED

    Hand > SCISSORS should be beaten by ROCK PASSED
    ...
    ```

1. Update the game service

    ```java
    package demo.games;

    import org.junit.jupiter.api.DisplayName;
    import org.junit.jupiter.api.Test;
    import org.junit.jupiter.params.ParameterizedTest;
    import org.junit.jupiter.params.provider.EnumSource;

    import static demo.games.Outcome.DRAW;
    import static org.junit.jupiter.api.Assertions.assertEquals;
    import static org.junit.jupiter.api.Assertions.assertSame;
    import static org.mockito.ArgumentMatchers.eq;
    import static org.mockito.Mockito.mock;
    import static org.mockito.Mockito.times;
    import static org.mockito.Mockito.verify;
    import static org.mockito.Mockito.when;

    @DisplayName( "Game service" )
    public class GameServiceTest {

      @Test
      @DisplayName( "should return a random hand based on the random number generated by the service" )
      public void shouldReturnARandomHand() { /* ... */ }

      @EnumSource( Hand.class )
      @ParameterizedTest( name = "should return DRAW when both players play the same hand {0}" )
      public void shouldReturnDraw( final Hand hand ) {
        final int numberOfCandidates = Hand.values().length;

        final RandomService randomService = mock( RandomService.class );
        when( randomService.nextInt( eq( numberOfCandidates ) ) ).thenReturn( hand.ordinal() );
        final GameService service = new GameService( randomService );

        final PlayResult result = new PlayResult( hand, hand, DRAW );
        assertEquals( result, service.play( hand ) );

        verify( randomService, times( 1 ) ).nextInt( numberOfCandidates );
      }
    }
    ```

    Make the test compile.

    ```java
    package demo.games;

    import org.springframework.stereotype.Service;

    @Service
    public class GameService {

      private final RandomService randomService;

      public GameService( final RandomService randomService ) { /* ... */ }

      public Hand random() { /* ... */ }

      public PlayResult play( final Hand player ) {
        return new PlayResult( player, player, Outcome.DRAW );
      }
    }
    ```

    The test should fail as we are not using the `RandomService` mocks as expected.

    ```bash
    $ ./gradlew test

    ...

    Game service > should return DRAW when both players play the same hand ROCK FAILED
        org.mockito.exceptions.verification.WantedButNotInvoked at GameServiceTest.java:48

    Game service > should return DRAW when both players play the same hand PAPER FAILED
        org.mockito.exceptions.verification.WantedButNotInvoked at GameServiceTest.java:48

    Game service > should return DRAW when both players play the same hand SCISSORS FAILED
        org.mockito.exceptions.verification.WantedButNotInvoked at GameServiceTest.java:48

    ...
    ```

    Make the test pass

    ```java
    package demo.games;

    import org.springframework.stereotype.Service;

    @Service
    public class GameService {

      private final RandomService randomService;

      public GameService( final RandomService randomService ) { /* ... */ }

      public Hand random() { /* ... */ }

      public PlayResult play( final Hand player ) {
        final Hand computer = random();
        return new PlayResult( computer, player, Outcome.DRAW );
      }
    }
    ```

    The above solution is incorrect, but the tests pass

1. Refactor tests

    Note that there is lots of similarities between the tests.

    1. Move the `numberOfCandidates` variable to the class level

        ```java
        package demo.games;

        import org.junit.jupiter.api.DisplayName;
        import org.junit.jupiter.api.Test;
        import org.junit.jupiter.params.ParameterizedTest;
        import org.junit.jupiter.params.provider.EnumSource;

        import static demo.games.Outcome.DRAW;
        import static org.junit.jupiter.api.Assertions.assertEquals;
        import static org.junit.jupiter.api.Assertions.assertSame;
        import static org.mockito.ArgumentMatchers.eq;
        import static org.mockito.Mockito.mock;
        import static org.mockito.Mockito.times;
        import static org.mockito.Mockito.verify;
        import static org.mockito.Mockito.when;

        @DisplayName( "Game service" )
        public class GameServiceTest {

          private static final int NUMBER_OF_CANDIDATES = Hand.values().length;

          @Test
          @DisplayName( "should return a random hand based on the random number generated by the service" )
          public void shouldReturnARandomHand() {
            final Hand expectedHand = Hand.ROCK;

            final RandomService randomService = mock( RandomService.class );
            when( randomService.nextInt( eq( NUMBER_OF_CANDIDATES ) ) ).thenReturn( expectedHand.ordinal() );

            final GameService service = new GameService( randomService );
            final Hand hand = service.random();
            assertSame( expectedHand, hand );

            verify( randomService, times( 1 ) ).nextInt( NUMBER_OF_CANDIDATES );
          }

          @EnumSource( Hand.class )
          @ParameterizedTest( name = "should return DRAW when both players play the same hand {0}" )
          public void shouldReturnDraw( final Hand hand ) {
            final RandomService randomService = mock( RandomService.class );
            when( randomService.nextInt( eq( NUMBER_OF_CANDIDATES ) ) ).thenReturn( hand.ordinal() );

            final GameService service = new GameService( randomService );

            final PlayResult result = new PlayResult( hand, hand, DRAW );
            assertEquals( result, service.play( hand ) );

            verify( randomService, times( 1 ) ).nextInt( NUMBER_OF_CANDIDATES );
          }
        }
        ```

    1. Extract *mock* and *verify* into two separate methods

        The mock `RandomService`

        ```java
        private RandomService mockRandomService( final Hand hand ) {
          final RandomService randomService = mock( RandomService.class );
          when( randomService.nextInt( eq( NUMBER_OF_CANDIDATES ) ) ).thenReturn( hand.ordinal() );
          return randomService;
        }
        ```

        The verify `RandomService`

        ```java
        private void verifyRandomService( final RandomService service ) {
          verify( service, times( 1 ) ).nextInt( NUMBER_OF_CANDIDATES );
        }
        ```

        Complete example

        ```java
        package demo.games;

        import org.junit.jupiter.api.DisplayName;
        import org.junit.jupiter.api.Test;
        import org.junit.jupiter.params.ParameterizedTest;
        import org.junit.jupiter.params.provider.EnumSource;

        import static demo.games.Outcome.DRAW;
        import static org.junit.jupiter.api.Assertions.assertEquals;
        import static org.junit.jupiter.api.Assertions.assertSame;
        import static org.mockito.ArgumentMatchers.eq;
        import static org.mockito.Mockito.mock;
        import static org.mockito.Mockito.times;
        import static org.mockito.Mockito.verify;
        import static org.mockito.Mockito.when;

        @DisplayName( "Game service" )
        public class GameServiceTest {

          private static final int NUMBER_OF_CANDIDATES = Hand.values().length;

          @Test
          @DisplayName( "should return a random hand based on the random number generated by the service" )
          public void shouldReturnARandomHand() {
            final Hand expectedHand = Hand.ROCK;

            final RandomService randomService = mockRandomService( expectedHand );

            final GameService service = new GameService( randomService );
            final Hand hand = service.random();
            assertSame( expectedHand, hand );

            verifyRandomService( randomService );
          }

          @EnumSource( Hand.class )
          @ParameterizedTest( name = "should return DRAW when both players play the same hand {0}" )
          public void shouldReturnDraw( final Hand hand ) {
            final RandomService randomService = mockRandomService( hand );

            final GameService service = new GameService( randomService );

            final PlayResult result = new PlayResult( hand, hand, DRAW );
            assertEquals( result, service.play( hand ) );

            verifyRandomService( randomService );
          }

          private RandomService mockRandomService( final Hand hand ) {
            final RandomService randomService = mock( RandomService.class );
            when( randomService.nextInt( eq( NUMBER_OF_CANDIDATES ) ) ).thenReturn( hand.ordinal() );
            return randomService;
          }

          private void verifyRandomService( final RandomService service ) {
            verify( service, times( 1 ) ).nextInt( NUMBER_OF_CANDIDATES );
          }
        }
        ```

1. Computer win

    ```java
    package demo.games;

    import org.junit.jupiter.api.DisplayName;
    import org.junit.jupiter.api.Test;
    import org.junit.jupiter.params.ParameterizedTest;
    import org.junit.jupiter.params.provider.CsvSource;
    import org.junit.jupiter.params.provider.EnumSource;

    import static demo.games.Outcome.COMPUTER_WIN;
    import static demo.games.Outcome.DRAW;
    import static org.junit.jupiter.api.Assertions.assertEquals;
    import static org.junit.jupiter.api.Assertions.assertSame;
    import static org.mockito.ArgumentMatchers.eq;
    import static org.mockito.Mockito.mock;
    import static org.mockito.Mockito.times;
    import static org.mockito.Mockito.verify;
    import static org.mockito.Mockito.when;

    @DisplayName( "Game service" )
    public class GameServiceTest {

      private static final int NUMBER_OF_CANDIDATES = Hand.values().length;

      @Test
      @DisplayName( "should return a random hand based on the random number generated by the service" )
      public void shouldReturnARandomHand() { /* ... */ }

      @EnumSource( Hand.class )
      @ParameterizedTest( name = "should return DRAW when both players play the same hand {0}" )
      public void shouldReturnDraw( final Hand hand ) { /* ... */ }

      @CsvSource( { "PAPER,ROCK", "SCISSORS,PAPER", "ROCK,SCISSORS" } )
      @ParameterizedTest( name = "should return COMPUTER_WIN when computer plays {0} and player plays {1}" )
      public void shouldReturnComputerWin( final Hand computer, final Hand player ) {
        final RandomService randomService = mockRandomService( computer );

        final GameService service = new GameService( randomService );

        final PlayResult result = new PlayResult( computer, player, COMPUTER_WIN );
        assertEquals( result, service.play( player ) );

        verifyRandomService( randomService );
      }

      private RandomService mockRandomService( final Hand hand ) { /* ... */ }

      private void verifyRandomService( final RandomService service ) { /* ... */ }
    }
    ```

    Run the test

    ```bash
    $ ./gradlew test

    ...

    Game service > should return COMPUTER_WIN when computer plays PAPER and player plays ROCK FAILED
        org.opentest4j.AssertionFailedError at GameServiceTest.java:59

    Game service > should return COMPUTER_WIN when computer plays SCISSORS and player plays PAPER FAILED
        org.opentest4j.AssertionFailedError at GameServiceTest.java:59

    Game service > should return COMPUTER_WIN when computer plays ROCK and player plays SCISSORS FAILED
        org.opentest4j.AssertionFailedError at GameServiceTest.java:59

    ...
    ```

    Make the test pass

    ```java
    package demo.games;

    import org.springframework.stereotype.Service;

    @Service
    public class GameService {

      private final RandomService randomService;

      public GameService( final RandomService randomService ) { /* ... */ }

      public Hand random() { /* ... */ }

      public PlayResult play( final Hand player ) {
        final Hand computer = random();

        final Outcome outcome = computer == player ? Outcome.DRAW :
          computer.beatenBy() == player ? Outcome.PLAYER_WIN :
            Outcome.COMPUTER_WIN;

        return new PlayResult( computer, player, outcome );
      }
    }
    ```

    Run the tests.  All tests should pass.

1. Refactoring

    ```java
    public PlayResult play( final Hand player ) {
      final Hand computer = random();

      final Outcome outcome = computer == player ? Outcome.DRAW :
        computer.beatenBy() == player ? Outcome.PLAYER_WIN :
          Outcome.COMPUTER_WIN;

      return new PlayResult( computer, player, outcome );
    }
    ```

    Split the method in two

    ```java
    public PlayResult play( final Hand player ) {
      final Hand computer = random();
      final Outcome outcome = determineOutcome( player, computer );
      return new PlayResult( computer, player, outcome );
    }

    private Outcome determineOutcome( final Hand player, final Hand computer ) {
      return computer == player ? Outcome.DRAW :
        computer.beatenBy() == player ? Outcome.PLAYER_WIN :
          Outcome.COMPUTER_WIN;
    }
    ```

    Complete example

    ```java
    package demo.games;

    import org.springframework.stereotype.Service;

    @Service
    public class GameService {

      private final RandomService randomService;

      public GameService( final RandomService randomService ) { /* ... */ }

      public Hand random() { /* ... */ }

      public PlayResult play( final Hand player ) {
        final Hand computer = random();
        final Outcome outcome = determineOutcome( player, computer );
        return new PlayResult( computer, player, outcome );
      }

      private Outcome determineOutcome( final Hand player, final Hand computer ) {
        return computer == player ? Outcome.DRAW :
          computer.beatenBy() == player ? Outcome.PLAYER_WIN :
            Outcome.COMPUTER_WIN;
      }
    }
    ```

1. Refactor test

    ```java
    package demo.games;

    import org.junit.jupiter.api.DisplayName;
    import org.junit.jupiter.api.Test;
    import org.junit.jupiter.params.ParameterizedTest;
    import org.junit.jupiter.params.provider.CsvSource;
    import org.junit.jupiter.params.provider.EnumSource;

    import static demo.games.Outcome.COMPUTER_WIN;
    import static demo.games.Outcome.DRAW;
    import static org.junit.jupiter.api.Assertions.assertEquals;
    import static org.junit.jupiter.api.Assertions.assertSame;
    import static org.mockito.ArgumentMatchers.eq;
    import static org.mockito.Mockito.mock;
    import static org.mockito.Mockito.times;
    import static org.mockito.Mockito.verify;
    import static org.mockito.Mockito.when;

    @DisplayName( "Game service" )
    public class GameServiceTest {

      private static final int NUMBER_OF_CANDIDATES = Hand.values().length;

      @Test
      @DisplayName( "should return a random hand based on the random number generated by the service" )
      public void shouldReturnARandomHand() { /* ... */ }

      @EnumSource( Hand.class )
      @ParameterizedTest( name = "should return DRAW when both players play the same hand {0}" )
      public void shouldReturnDraw( final Hand hand ) {
        final RandomService randomService = mockRandomService( hand );

        final GameService service = new GameService( randomService );

        final PlayResult result = new PlayResult( hand, hand, DRAW );
        assertEquals( result, service.play( hand ) );

        verifyRandomService( randomService );
      }

      @CsvSource( { "PAPER,ROCK", "SCISSORS,PAPER", "ROCK,SCISSORS" } )
      @ParameterizedTest( name = "should return COMPUTER_WIN when computer plays {0} and player plays {1}" )
      public void shouldReturnComputerWin( final Hand computer, final Hand player ) {
        final RandomService randomService = mockRandomService( computer );

        final GameService service = new GameService( randomService );

        final PlayResult result = new PlayResult( computer, player, COMPUTER_WIN );
        assertEquals( result, service.play( player ) );

        verifyRandomService( randomService );
      }

      private RandomService mockRandomService( final Hand hand ) { /* ... */ }

      private void verifyRandomService( final RandomService service ) { /* ... */ }
    }
    ```

    Extract the common parts

    ```java
    private void assertPlay( final Hand computer, final Hand player, final Outcome outcome ) {
      final RandomService randomService = mockRandomService( computer );

      final GameService service = new GameService( randomService );

      final PlayResult result = new PlayResult( computer, player, outcome );
      assertEquals( result, service.play( player ) );

      verifyRandomService( randomService );
    }
    ```

    Refactor tests

    ```java
    @EnumSource( Hand.class )
    @ParameterizedTest( name = "should return DRAW when both players play the same hand {0}" )
    public void shouldReturnDraw( final Hand hand ) {
      assertPlay( hand, hand, Outcome.DRAW );
    }

    @CsvSource( { "PAPER,ROCK", "SCISSORS,PAPER", "ROCK,SCISSORS" } )
    @ParameterizedTest( name = "should return COMPUTER_WIN when computer plays {0} and player plays {1}" )
    public void shouldReturnComputerWin( final Hand computer, final Hand player ) {
      assertPlay( computer, player, Outcome.COMPUTER_WIN );
    }
    ```

    Complete example

    ```java
    package demo.games;

    import org.junit.jupiter.api.DisplayName;
    import org.junit.jupiter.api.Test;
    import org.junit.jupiter.params.ParameterizedTest;
    import org.junit.jupiter.params.provider.CsvSource;
    import org.junit.jupiter.params.provider.EnumSource;

    import static org.junit.jupiter.api.Assertions.assertEquals;
    import static org.junit.jupiter.api.Assertions.assertSame;
    import static org.mockito.ArgumentMatchers.eq;
    import static org.mockito.Mockito.mock;
    import static org.mockito.Mockito.times;
    import static org.mockito.Mockito.verify;
    import static org.mockito.Mockito.when;

    @DisplayName( "Game service" )
    public class GameServiceTest {

      private static final int NUMBER_OF_CANDIDATES = Hand.values().length;

      @Test
      @DisplayName( "should return a random hand based on the random number generated by the service" )
      public void shouldReturnARandomHand() { /* ... */ }

      @EnumSource( Hand.class )
      @ParameterizedTest( name = "should return DRAW when both players play the same hand {0}" )
      public void shouldReturnDraw( final Hand hand ) {
        assertPlay( hand, hand, Outcome.DRAW );
      }

      @CsvSource( { "PAPER,ROCK", "SCISSORS,PAPER", "ROCK,SCISSORS" } )
      @ParameterizedTest( name = "should return COMPUTER_WIN when computer plays {0} and player plays {1}" )
      public void shouldReturnComputerWin( final Hand computer, final Hand player ) {
        assertPlay( computer, player, Outcome.COMPUTER_WIN );
      }

      private void assertPlay( final Hand computer, final Hand player, final Outcome outcome ) {
        final RandomService randomService = mockRandomService( computer );

        final GameService service = new GameService( randomService );

        final PlayResult result = new PlayResult( computer, player, outcome );
        assertEquals( result, service.play( player ) );

        verifyRandomService( randomService );
      }

      private RandomService mockRandomService( final Hand hand ) {
        final RandomService randomService = mock( RandomService.class );
        when( randomService.nextInt( eq( NUMBER_OF_CANDIDATES ) ) ).thenReturn( hand.ordinal() );
        return randomService;
      }

      private void verifyRandomService( final RandomService service ) {
        verify( service, times( 1 ) ).nextInt( NUMBER_OF_CANDIDATES );
      }
    }
    ```

    Run the tests and make sure they still pass.

1. Player win

    ```java
    @CsvSource( { "ROCK,PAPER", "PAPER,SCISSORS", "SCISSORS,ROCK" } )
    @ParameterizedTest( name = "should return PLAYER_WIN when computer plays {0} and player plays {1}" )
    public void shouldReturnPlayerWin( final Hand computer, final Hand player ) {
      assertPlay( computer, player, Outcome.PLAYER_WIN );
    }
    ```

    Complete example.

    ```java
    package demo.games;

    import org.junit.jupiter.api.DisplayName;
    import org.junit.jupiter.api.Test;
    import org.junit.jupiter.params.ParameterizedTest;
    import org.junit.jupiter.params.provider.CsvSource;
    import org.junit.jupiter.params.provider.EnumSource;

    import static org.junit.jupiter.api.Assertions.assertEquals;
    import static org.junit.jupiter.api.Assertions.assertSame;
    import static org.mockito.ArgumentMatchers.eq;
    import static org.mockito.Mockito.mock;
    import static org.mockito.Mockito.times;
    import static org.mockito.Mockito.verify;
    import static org.mockito.Mockito.when;

    @DisplayName( "Game service" )
    public class GameServiceTest {

      private static final int NUMBER_OF_CANDIDATES = Hand.values().length;

      @Test
      @DisplayName( "should return a random hand based on the random number generated by the service" )
      public void shouldReturnARandomHand() { /* ... */ }

      @EnumSource( Hand.class )
      @ParameterizedTest( name = "should return DRAW when both players play the same hand {0}" )
      public void shouldReturnDraw( final Hand hand ) { /* ... */ }

      @CsvSource( { "PAPER,ROCK", "SCISSORS,PAPER", "ROCK,SCISSORS" } )
      @ParameterizedTest( name = "should return COMPUTER_WIN when computer plays {0} and player plays {1}" )
      public void shouldReturnComputerWin( final Hand computer, final Hand player ) { /* ... */ }

      @CsvSource( { "ROCK,PAPER", "PAPER,SCISSORS", "SCISSORS,ROCK" } )
      @ParameterizedTest( name = "should return PLAYER_WIN when computer plays {0} and player plays {1}" )
      public void shouldReturnPlayerWin( final Hand computer, final Hand player ) {
        assertPlay( computer, player, Outcome.PLAYER_WIN );
      }

      private void assertPlay( final Hand computer, final Hand player, final Outcome outcome ) { /* ... */ }

      private RandomService mockRandomService( final Hand hand ) { /* ... */ }

      private void verifyRandomService( final RandomService service ) { /* ... */ }
    }
    ```

    The test should pass.

1. Use the service

    ```java
    package demo.games;

    import org.junit.jupiter.api.DisplayName;
    import org.junit.jupiter.api.Test;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
    import org.springframework.boot.test.mock.mockito.MockBean;
    import org.springframework.test.web.servlet.MockMvc;

    import static org.hamcrest.core.Is.is;
    import static org.mockito.Mockito.times;
    import static org.mockito.Mockito.verify;
    import static org.mockito.Mockito.when;
    import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
    import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
    import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
    import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

    /* Just load the following controller and all it needs */
    @WebMvcTest( GameController.class )
    @DisplayName( "Rock controller" )
    public class GameControllerTest {

      @Autowired
      private MockMvc mockMvc;

      @MockBean
      private GameService service;

      @Test
      @DisplayName( "should return the hand provided by the service" )
      public void shouldReturnTheHandProvidedByTheService() throws Exception { /* ... */ }

      @Test
      @DisplayName( "should return the play outcome provided by the service" )
      public void shouldReturnTheOutcomeProvidedByTheService() throws Exception {
        final PlayResult result = new PlayResult( Hand.PAPER, Hand.ROCK, Outcome.COMPUTER_WIN );

        when( service.play( result.getPlayer() ) ).thenReturn( result );

        mockMvc.perform( get( "/play/" + result.getPlayer().name() ) )
          .andExpect( status().isOk() )
          .andExpect( jsonPath( "$.computer", is( result.getComputer().name() ) ) )
          .andExpect( jsonPath( "$.player", is( result.getPlayer().name() ) ) )
          .andExpect( jsonPath( "$.outcome", is( result.getOutcome().name() ) ) );

        verify( service, times( 1 ) ).play( result.getPlayer() );
      }
    }
    ```

    Test should fail

    ```bash
    $ ./gradlew test

    ...

    Rock controller > should return the play outcome provided by the service FAILED
        java.lang.AssertionError at GameControllerTest.java:53

    ...
    ```

    Fix the controller.

    ```java
    package demo.games;

    import org.springframework.stereotype.Controller;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.PathVariable;
    import org.springframework.web.bind.annotation.ResponseBody;

    @Controller
    public class GameController {

      private final GameService service;

      public GameController( final GameService service ) { /* ... */ }

      @GetMapping( "/hand" )
      public @ResponseBody HandResponse hand() { /* ... */ }

      @GetMapping( "/play/{player}" )
      public @ResponseBody PlayResult play( final @PathVariable( "player" ) Hand player ) {
        return service.play( player );
      }
    }
    ```

    All tests should pass

    ```bash
    $ ./gradlew test

    ...

    BUILD SUCCESSFUL in 5s
    4 actionable tasks: 2 executed, 2 up-to-date
    ```

1. Project structure

    ```bash
    $ ./gradlew clean
    ```

    project structure

    ```bash
    $ tree .
    .
    ├── Dockerfile
    ├── build.gradle
    ├── gradle
    │   └── wrapper
    │       ├── gradle-wrapper.jar
    │       └── gradle-wrapper.properties
    ├── gradlew
    ├── gradlew.bat
    ├── settings.gradle
    └── src
        ├── main
        │   ├── java
        │   │   └── demo
        │   │       └── games
        │   │           ├── GameApplication.java
        │   │           ├── GameController.java
        │   │           ├── GameService.java
        │   │           ├── Hand.java
        │   │           ├── HandResponse.java
        │   │           ├── Outcome.java
        │   │           ├── PlayResult.java
        │   │           └── RandomService.java
        │   └── resources
        │       ├── application.yaml
        │       └── banner.txt
        └── test
            └── java
                └── demo
                    └── games
                        ├── GameApplicationTests.java
                        ├── GameControllerTest.java
                        ├── GameServiceTest.java
                        ├── HandTest.java
                        └── RandomServiceTest.java

    12 directories, 22 files
    ```

## Run

```bash
$ ./gradlew clean build
```

```bash
$ java -jar build/libs/rock-paper-scissors.jar
```

```bash
$ curl http://localhost:8080/play/ROCK
{"computer":"SCISSORS","player":"ROCK","outcome":"PLAYER_WIN"}
```

[http://localhost:8080/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config](http://localhost:8080/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config)
