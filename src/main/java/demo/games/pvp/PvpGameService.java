package demo.games.pvp;

import demo.games.shared.Hand;
import org.springframework.stereotype.Service;

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

    final Game game = new Game();
    game.setCode( code );
    game.setPlayer1( player1 );
    repository.save( game );

    return new GameResponse( code );
  }
}
