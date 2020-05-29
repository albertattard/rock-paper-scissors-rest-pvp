package demo.games;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayResult {
  private Hand computer;
  private Hand player;
  private Outcome outcome;
}
