package demo.games;

import org.springframework.stereotype.Service;

@Service
public class GameService {

  private final RandomService randomService;

  public GameService( final RandomService randomService ) {
    this.randomService = randomService;
  }

  public PlayResult play( final Hand player ) {
    final Hand computer = random();
    final Outcome outcome = determineOutcome( computer, player );
    return new PlayResult( computer, player, outcome );
  }

  private Outcome determineOutcome( Hand computer, final Hand player ) {
    return computer == player ? Outcome.DRAW :
      computer.beatenBy() == player ? Outcome.PLAYER_WIN :
        Outcome.COMPUTER_WIN;
  }

  public Hand random() {
    final Hand[] candidates = Hand.values();
    return candidates[randomService.nextInt( candidates.length )];
  }
}
