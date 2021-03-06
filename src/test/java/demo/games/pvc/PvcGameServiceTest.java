package demo.games.pvc;

import demo.games.shared.Hand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import static demo.games.pvc.Outcome.COMPUTER_WIN;
import static demo.games.pvc.Outcome.DRAW;
import static demo.games.pvc.Outcome.PLAYER_WIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName( "PvC game service" )
public class PvcGameServiceTest {

  private static final int NUMBER_OF_HANDS = Hand.values().length;

  @Test
  @DisplayName( "should return a random hand based on the random number generated by the service" )
  public void shouldReturnARandomHand() {
    final Hand expectedHand = Hand.ROCK;

    final RandomService randomService = mockRandomService( expectedHand );

    final PvcGameService service = new PvcGameService( randomService );
    final Hand hand = service.random();
    assertSame( expectedHand, hand );

    verifyMocks( randomService );
  }

  @EnumSource( Hand.class )
  @ParameterizedTest( name = "should return DRAW when both players play the same hand {0}" )
  public void shouldReturnDraw( final Hand hand ) {
    playAndAssert( hand, hand, DRAW );
  }

  @CsvSource( { "PAPER,ROCK", "SCISSORS,PAPER", "ROCK,SCISSORS" } )
  @ParameterizedTest( name = "should return COMPUTER_WIN when computer plays {0} and player plays {1}" )
  public void shouldReturnComputerWin( final Hand computer, final Hand player ) {
    playAndAssert( computer, player, COMPUTER_WIN );
  }

  @CsvSource( { "ROCK,PAPER", "PAPER,SCISSORS", "SCISSORS,ROCK" } )
  @ParameterizedTest( name = "should return PLAYER_WIN when computer plays {0} and player plays {1}" )
  public void shouldReturnPlayerWin( final Hand computer, final Hand player ) {
    playAndAssert( computer, player, PLAYER_WIN );
  }

  private void playAndAssert( Hand computer, Hand player, Outcome outcome ) {
    final RandomService randomService = mockRandomService( computer );

    final PvcGameService service = new PvcGameService( randomService );

    final PlayResult result = new PlayResult( computer, player, outcome );
    assertEquals( result, service.play( player ) );

    verifyMocks( randomService );
  }

  private RandomService mockRandomService( final Hand computer ) {
    final RandomService randomService = mock( RandomService.class );
    when( randomService.nextInt( eq( NUMBER_OF_HANDS ) ) ).thenReturn( computer.ordinal() );
    return randomService;
  }

  private void verifyMocks( final RandomService randomService ) {
    verify( randomService, times( 1 ) ).nextInt( NUMBER_OF_HANDS );
  }
}
