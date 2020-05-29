package demo.games.pvp;

import demo.games.shared.Hand;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PvpGameService {

  private final GameCodeService randomService;
  private final GameRepository repository;

  public PvpGameService( final GameCodeService randomService, final GameRepository repository ) {
    this.randomService = randomService;
    this.repository = repository;
  }

  public GameResponse create( final Hand player1 ) {
    final String code = randomService.nextCode( 8 );

    final Game game = new Game()
      .setCode( code )
      .setPlayer1( player1 )
      .setState( GameState.OPEN );
    repository.save( game );

    return new GameResponse( code );
  }

  public List<GameResponse> listOpenGames() {
    return repository.findByStateEquals( GameState.OPEN )
      .stream()
      .map( r -> new GameResponse( r.getCode() ) )
      .collect( Collectors.toList() );
  }

  public GameDetails findGame( String code ) {
    return new GameDetails()
      .setCode( code );
  }
}
