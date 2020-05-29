package demo.games.pvp;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class PvpGameController {

  private final PvpGameService service;

  public PvpGameController( final PvpGameService service ) {
    this.service = service;
  }

  @ResponseBody
  @PostMapping( "/game" )
  public GameResponse create( @RequestBody CreateGame game ) {
    return service.create( game.getPlayer1() );
  }

  @ResponseBody
  @GetMapping( "/game/list" )
  public List<GameResponse> list() {
    return service.listOpenGames();
  }
}
