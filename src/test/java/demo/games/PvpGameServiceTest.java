package demo.games;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName( "Game service PVP" )
public class PvpGameServiceTest {

  @Test
  @DisplayName( "should create game and return the game code" )
  public void shouldCreateGameAndReturnCode() {

    final String code = "abcdefgh";
    final Hand player1 = Hand.ROCK;
    final Game gameToSaved = new Game();
    gameToSaved.setCode( code );
    gameToSaved.setPlayer1( player1 );
    final GameResponse expected = new GameResponse( code );

    final RandomService randomService = mock( RandomService.class );
    final GameRepository repository = mock( GameRepository.class );

    when( randomService.nextCode( eq( 8 ) ) ).thenReturn( code );
    when( repository.save( eq( gameToSaved ) ) ).thenReturn( gameToSaved );

    final GameService service = new GameService( randomService, repository );

    final GameResponse created = service.create( player1 );
    assertEquals( expected, created );
  }
}
