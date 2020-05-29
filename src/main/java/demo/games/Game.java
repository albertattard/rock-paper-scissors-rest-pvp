package demo.games;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table( name = "games" )
public class Game {

  @Id
  private String code;

  @Enumerated( EnumType.STRING)
  private Hand player1;

  @Enumerated( EnumType.STRING)
  private Hand player2;
}
