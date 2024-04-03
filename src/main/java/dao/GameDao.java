package dao;

import domain.board.Turn;
import java.util.Map;
import java.util.Optional;

public interface GameDao {

    int save(Turn turn);

    Map<Integer, Turn> findAll();

    Optional<Turn> findTurnById(int id);

    int updateById(int id, Turn turn);

    int deleteById(int id);
}
