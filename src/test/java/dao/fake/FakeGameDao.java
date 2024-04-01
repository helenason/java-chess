package dao.fake;

import dao.GameDao;
import domain.board.Turn;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FakeGameDao implements GameDao {

    private final List<Turn> turns = new ArrayList<>();

    @Override
    public Connection getConnection() {
        return null;
    }

    @Override
    public int save(Turn turn) {
        turns.add(turn);
        return 1;
    }

    @Override
    public Optional<Turn> findTurn() {
        if (turns.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(turns.get(0));
    }

    @Override
    public int update(Turn turn) {
        turns.remove(0);
        turns.add(turn);
        return 1;
    }

    @Override
    public int delete() {
        turns.remove(0);
        return 1;
    }
}
