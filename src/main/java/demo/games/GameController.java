package demo.games;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class GameController {

  private final GameService service;

  public GameController( final GameService service ) {
    this.service = service;
  }

  @GetMapping( "/hand" )
  public @ResponseBody HandResponse hand() {
    final Hand hand = service.random();
    return new HandResponse( hand );
  }

  @GetMapping( "/play/{player}" )
  public @ResponseBody PlayResult play( final @PathVariable( "player" ) Hand player ) {
    return service.play( player );
  }
}
