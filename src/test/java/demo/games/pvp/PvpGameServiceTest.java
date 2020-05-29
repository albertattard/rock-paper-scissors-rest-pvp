package demo.games.pvp;

import demo.games.shared.Hand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@DisplayName( "PvP Game service" )
public class PvpGameServiceTest {

  @Test
  @DisplayName( "should create game and return the game code" )
  public void shouldCreateGameAndReturnCode() {
    final String code = "abcdefgh";
    final Hand player1 = Hand.ROCK;
    final Game gameToSaved = new Game();
    gameToSaved.setCode( code );
    gameToSaved.setPlayer1( player1 );
    gameToSaved.setState( GameState.OPEN );

    final GameCodeService codeService = mock( GameCodeService.class );
    final GameRepository repository = mock( GameRepository.class );

    when( codeService.nextCode( eq( 8 ) ) ).thenReturn( code );
    when( repository.save( eq( gameToSaved ) ) ).thenReturn( gameToSaved );

    final PvpGameService service = new PvpGameService( codeService, repository );

    final GameResponse created = service.create( player1 );
    assertEquals( new GameResponse( code ), created );

    verify( codeService, times( 1 ) ).nextCode( 8 );
    verify( repository, times( 1 ) ).save( gameToSaved );
  }

  @Test
  @DisplayName( "should return a list of open games" )
  public void shouldReturnOpenGames() {
    final String code = "abcdefgh";
    final Game gameInDb = new Game();
    gameInDb.setCode( code );
    gameInDb.setPlayer1( Hand.ROCK );
    gameInDb.setState( GameState.OPEN );
    final List<Game> gamesInDb = List.of( gameInDb );
    final List<GameResponse> expected = List.of( new GameResponse( code ) );

    final GameCodeService codeService = mock( GameCodeService.class );
    final GameRepository repository = mock( GameRepository.class );

    when( repository.findByStateEquals( eq( GameState.OPEN ) ) ).thenReturn( gamesInDb );

    final PvpGameService service = new PvpGameService( codeService, repository );

    final List<GameResponse> games = service.listOpenGames();
    assertEquals( expected, games );

    verifyNoInteractions( codeService );
  }
}
