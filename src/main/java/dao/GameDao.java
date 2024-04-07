package dao;

import domain.board.Turn;
import dto.GameData;
import java.util.List;
import java.util.Optional;

public interface GameDao {

    int save(Turn turn);

    List<GameData> findAll();

    Optional<Turn> findTurnById(int id);

    int updateById(int id, Turn turn);

    int deleteById(int id);
}
