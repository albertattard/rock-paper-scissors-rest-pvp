package demo.games;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class RandomService {

  public int nextInt( int bound ) {
    return randomNumber.nextInt( bound );
  }

  private final Random randomNumber = new Random();
}
