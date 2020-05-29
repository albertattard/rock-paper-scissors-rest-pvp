package demo.games.pvp;

import demo.games.shared.Hand;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.Arrays;
import java.util.Comparator;

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

    final GameResponse created = restTemplate.getForObject( newGamePath( player1 ), GameResponse.class );
    final GameResponse[] open = restTemplate.getForObject( listOpenPath(), GameResponse[].class );

    final Comparator<GameResponse> comparator = Comparator.comparing( GameResponse::getCode );
    Arrays.sort( open, comparator );

    Assertions.assertTrue( Arrays.binarySearch( open, created, comparator ) >= 0 );
  }

  private String newGamePath( final Hand player1 ) {
    return String.format( "http://localhost:%d/game/new/%s", port, player1.name() );
  }

  private String listOpenPath() {
    return String.format( "http://localhost:%d/game/list", port );
  }
}
