package demo.games.pvp;

import demo.games.shared.Hand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    final String code = "abcdefgh";

    when( service.create( player1 ) ).thenReturn( new GameResponse( code ) );

    mockMvc.perform( get( String.format( "/game/new/%s", player1.name() ) ) )
      .andExpect( status().isOk() )
      .andExpect( jsonPath( "$.code", is( code ) ) );

    verify( service, times( 1 ) ).create( player1 );
  }
}
