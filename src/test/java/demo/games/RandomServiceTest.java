package demo.games;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName( "Random service" )
public class RandomServiceTest {

  @Test
  @DisplayName( "should return a random int with a fair probability" )
  public void shouldReturnARandomNumber() {

    final int numberOfCandidates = 10;
    final int sampleSize = numberOfCandidates * 1_000;

    /* Retrieve a random number and count the occurrence */
    final RandomService service = new RandomService();
    final int[] candidateCounts = new int[numberOfCandidates];
    for ( int i = 0; i < sampleSize; i++ ) {
      final int randomNumber = service.nextInt( numberOfCandidates );
      candidateCounts[randomNumber]++;
    }

    /* The expected counts for each hand */
    final int expectedCount = sampleSize / numberOfCandidates;
    final int buffer = Math.round( expectedCount * 0.1F );

    /* Verify that each number has the same probability like any other */
    for ( int i = 0; i < numberOfCandidates; i++ ) {
      assertThat( candidateCounts[i] )
        .isBetween( expectedCount - buffer, expectedCount + buffer );
    }
  }

  @Test
  @DisplayName( "should return a number of random codes without collisions" )
  public void shouldReturnARandomCode() {

    final int testSize = 1_000_000;
    final int length = 8;

    final Set<String> observedCodes = new TreeSet<>( String.CASE_INSENSITIVE_ORDER );

    final RandomService service = new RandomService();
    for ( int i = 0; i < testSize; i++ ) {
      final String code = service.nextCode( length );
      assertTrue( observedCodes.add( code ), String.format( "The code %s was already observed", code ) );
    }

    assertEquals( testSize, observedCodes.size() );
  }
}
