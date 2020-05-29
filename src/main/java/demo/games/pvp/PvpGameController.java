package demo.games.pvp;

import demo.games.shared.Hand;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class PvpGameController {

  private final PvpGameService service;

  public PvpGameController( final PvpGameService service ) {
    this.service = service;
  }

  @GetMapping( "/game/new/{player1}" )
  public @ResponseBody GameResponse create( final @PathVariable( "player1" ) Hand player1 ) {
    return service.create( player1 );
  }

  @GetMapping( "/game/list" )
  public @ResponseBody List<GameResponse> list() {
    return service.listOpenGames();
  }
}
