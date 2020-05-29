package demo.games.pvp;

import demo.games.shared.Hand;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/* Just load the following controller and all it needs */
@WebMvcTest( PvpGameController.class )
@DisplayName( "Rock controller" )
public class PvpGameControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private PvpGameService service;

  @Test
  @DisplayName( "should create the new game and return the code" )
  public void shouldCreateGameAndReturnCode() throws Exception {

    final Hand player1 = Hand.ROCK;
    final GameResponse game = createRandomGame();

    when( service.create( player1 ) ).thenReturn( game );

    mockMvc.perform( post( String.format( "/game/new/%s", player1.name() ) ) )
      .andExpect( status().isOk() )
      .andExpect( jsonPath( "$.code", is( game.getCode() ) ) );

    verify( service, times( 1 ) ).create( player1 );
  }

  @Test
  @DisplayName( "should return the list of open games" )
  public void shouldReturnOpenGames() throws Exception {

    final List<GameResponse> games = createRandomGames( 5 );

    when( service.listOpenGames() ).thenReturn( games );

    final ResultActions result = mockMvc.perform( get( "/game/list/" ) )
      .andExpect( status().isOk() )
      .andExpect( jsonPath( "$" ).isArray() )
      .andExpect( jsonPath( "$", hasSize( games.size() ) ) )
      .andExpect( jsonPath( "$[*].code", containsInAnyOrder( toGameCode( games ) ) ) );

    verify( service, times( 1 ) ).listOpenGames();
  }

  private String[] toGameCode( List<GameResponse> games ) {
    final String[] codes = new String[games.size()];
    for ( int i = 0; i < games.size(); i++ ) {
      codes[i] = games.get( i ).getCode();
    }
    return codes;
  }

  private List<GameResponse> createRandomGames( int number ) {
    final List<GameResponse> games = new ArrayList<>( number );
    for ( int i = 0; i < number; i++ ) {
      games.add( createRandomGame() );
    }
    return games;
  }

  private GameResponse createRandomGame() {
    return new GameResponse( RandomStringUtils.randomAlphanumeric( 8 ) );
  }
}
