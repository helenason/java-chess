package dao;

import domain.board.Turn;
import java.sql.Connection;
import java.util.Optional;

public interface GameDao {

    Connection getConnection();

    int save(Turn turn);

    Optional<Turn> findTurn();

    int update(Turn turn);

    int delete();
}
