package demo.games.pvp;

import demo.games.shared.Hand;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
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