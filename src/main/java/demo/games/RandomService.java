package demo.games;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class RandomService {

  public int nextInt( int bound ) {
    return random.nextInt( bound );
  }

  public String nextCode( int length ) {
    return RandomStringUtils.randomAlphanumeric( length );
  }

  private final Random random = new Random();
}
