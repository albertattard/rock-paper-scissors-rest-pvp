package demo.games.pvp;

import demo.games.shared.Hand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@DisplayName( "PvP Game application" )
@SpringBootTest( webEnvironment = WebEnvironment.RANDOM_PORT )
public class PvpGameApplicationTests {

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  @Test
  @DisplayName( "should return a random hand" )
  public void shouldPlayAGameAgainstAnotherPlayer() {
    final Hand player1 = Hand.ROCK;

    final String url = String.format( "http://localhost:%d/game/new/%s", port, player1.name() );
    final GameResponse response = this.restTemplate.getForObject( url, GameResponse.class );
    System.out.println( response );
  }
}
