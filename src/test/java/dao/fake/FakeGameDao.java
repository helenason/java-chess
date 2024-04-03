package dao.fake;

import dao.GameDao;
import domain.board.Turn;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FakeGameDao implements GameDao {

    private final Map<Integer, Turn> turns = new HashMap<>();

    @Override
    public int save(Turn turn) {
        int id = turns.size();
        turns.put(id, turn);
        return id;
    }

    @Override
    public Map<Integer, Turn> findAll() {
        return Collections.unmodifiableMap(turns);
    }

    @Override
    public Optional<Turn> findTurnById(int id) {
        return Optional.ofNullable(turns.getOrDefault(id, null));
    }

    @Override
    public int updateById(int id, Turn turn) {
        turns.replace(id, turn);
        return 1;
    }

    @Override
    public int deleteById(int id) {
        turns.remove(id);
        return 1;
    }
}
