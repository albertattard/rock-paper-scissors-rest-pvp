package demo.games.pvp;

import demo.games.shared.Hand;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    final Game gameToSaved = createRandomGame();

    final GameCodeService codeService = mock( GameCodeService.class );
    final GameRepository repository = mock( GameRepository.class );

    when( codeService.nextCode( eq( 8 ) ) ).thenReturn( gameToSaved.getCode() );
    when( repository.save( eq( gameToSaved ) ) ).thenReturn( gameToSaved );

    final PvpGameService service = new PvpGameService( codeService, repository );

    final GameResponse created = service.create( gameToSaved.getPlayer1() );
    assertEquals( toGameResponse( gameToSaved ), created );

    verify( codeService, times( 1 ) ).nextCode( 8 );
    verify( repository, times( 1 ) ).save( gameToSaved );
  }

  @Test
  @DisplayName( "should return a list of open games" )
  public void shouldReturnOpenGames() {
    final List<Game> gamesInDb = createRandomGames( 5 );

    final GameCodeService codeService = mock( GameCodeService.class );
    final GameRepository repository = mock( GameRepository.class );

    when( repository.findByStateEquals( eq( GameState.OPEN ) ) ).thenReturn( gamesInDb );

    final PvpGameService service = new PvpGameService( codeService, repository );

    final List<GameResponse> games = service.listOpenGames();
    assertEquals( toGameResponse( gamesInDb ), games );

    verifyNoInteractions( codeService );
    verify( repository, times( 1 ) ).findByStateEquals( GameState.OPEN );
  }

  private List<Game> createRandomGames( int number ) {
    final List<Game> games = new ArrayList<>( number );
    for ( int i = 0; i < number; i++ ) {
      games.add( createRandomGame() );
    }
    return games;
  }

  private Game createRandomGame() {
    return new Game()
      .setCode( RandomStringUtils.randomAlphanumeric( 8 ) )
      .setPlayer1( Hand.ROCK )
      .setState( GameState.OPEN );
  }

  private List<GameResponse> toGameResponse( List<Game> games ) {
    return games.stream()
      .map( this::toGameResponse )
      .collect( Collectors.toList() );
  }

  private GameResponse toGameResponse( Game game ) {
    return new GameResponse( game.getCode() );
  }
}
