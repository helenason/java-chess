package dao;

import domain.board.Turn;
import java.util.Optional;

public interface GameDao {

    int save(Turn turn);

    Optional<Turn> findTurn();

    int update(Turn turn);

    int delete();
}
