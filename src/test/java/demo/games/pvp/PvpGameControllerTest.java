package demo.games.pvp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import demo.games.shared.Hand;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/* Just load the following controller and all it needs */
@WebMvcTest( PvpGameController.class )
@DisplayName( "PvP game controller" )
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

    when( service.create( eq( player1 ) ) ).thenReturn( game );

    mockMvc.perform(
      post( "/game" )
        .contentType( APPLICATION_JSON )
        .content( createGameAsJson( player1 ) )
    )
      .andExpect( status().isCreated() )
      .andExpect( redirectedUrl( String.format( "/game/%s", game.getCode() ) ) );

    verify( service, times( 1 ) ).create( player1 );
  }

  @Test
  @DisplayName( "should return the list of open games" )
  public void shouldReturnOpenGames() throws Exception {

    final List<GameResponse> games = createRandomGames( 5 );

    when( service.listOpenGames() ).thenReturn( games );

    mockMvc.perform( get( "/game/list/" ) )
      .andExpect( status().isOk() )
      .andExpect( jsonPath( "$" ).isArray() )
      .andExpect( jsonPath( "$", hasSize( games.size() ) ) )
      .andExpect( jsonPath( "$[*].code", containsInAnyOrder( toGameCode( games ) ) ) );

    verify( service, times( 1 ) ).listOpenGames();
  }

  @Test
  @DisplayName( "should return 404 when game is not found" )
  public void shouldReturnNotFoundGameDetails() throws Exception {
    final String code = "00000000";

    when( service.findGame( eq( code ) ) ).thenReturn( Optional.empty() );

    mockMvc.perform( get( String.format( "/game/%s", code ) ) )
      .andExpect( status().isNotFound() );

    verify( service, times( 1 ) ).findGame( code );
  }

  private String toJson( CreateGame game ) throws JsonProcessingException {
    return new ObjectMapper()
      .writer()
      .withDefaultPrettyPrinter()
      .writeValueAsString( game );
  }

  private String createGameAsJson( Hand player1 ) throws JsonProcessingException {
    return toJson( new CreateGame( player1 ) );
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
