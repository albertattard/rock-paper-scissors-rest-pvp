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
  public void shouldReturnARandomHand() {
    final var candidates = List.of(
      new HandResponse( Hand.ROCK ),
      new HandResponse( Hand.PAPER ),
      new HandResponse( Hand.SCISSORS )
    );

    final String url = String.format( "http://localhost:%d/hand", port );
    assertThat( this.restTemplate.getForObject( url, HandResponse.class ) )
      .isIn( candidates );
  }

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
