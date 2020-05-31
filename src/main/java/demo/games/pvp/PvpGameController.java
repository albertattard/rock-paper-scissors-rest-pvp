package demo.games.pvp;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Controller
public class PvpGameController {

  private final PvpGameService service;

  public PvpGameController( final PvpGameService service ) {
    this.service = service;
  }

  @ResponseBody
  @PostMapping( "/game" )
  public ResponseEntity<Void> create( final @RequestBody CreateGame game ) throws URISyntaxException {
    final GameResponse response = service.create( game.getPlayer1() );
    final URI uri = new URI( String.format( "/game/%s", response.getCode() ) );
    return ResponseEntity.created( uri ).build();
  }

  @ResponseBody
  @PutMapping( "/game/{code}" )
  public ResponseEntity<GameDetails> play( final @PathVariable( "code" ) String code, final @RequestBody PlayGame game ) {
    return service.play( code, game.getPlayer2() )
      .map( ResponseEntity::ok )
      .orElse( ResponseEntity.notFound().build() );
  }

  @ResponseBody
  @GetMapping( "/game/{code}" )
  public ResponseEntity<GameDetails> get( final @PathVariable( "code" ) String code ) {
    return service.findGame( code )
      .map( ResponseEntity::ok )
      .orElse( ResponseEntity.notFound().build() );
  }

  @ResponseBody
  @GetMapping( "/game/list" )
  public List<GameResponse> list() {
    return service.listOpenGames();
  }
}
