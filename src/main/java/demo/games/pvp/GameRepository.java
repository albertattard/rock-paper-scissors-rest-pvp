package demo.games.pvp;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends CrudRepository<Game, String> {

  List<Game> findByStateEquals( GameState state );
}
