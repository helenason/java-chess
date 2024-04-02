package dao;

import domain.board.Turn;
import java.util.Optional;

public interface GameDao {

    int save(Turn turn);

    int countAll();

    Optional<Turn> findTurn();

    int update(Turn turn);

    int delete();
}
