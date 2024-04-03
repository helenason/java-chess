package dao.fake;

import dao.GameDao;
import domain.board.Turn;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FakeGameDao implements GameDao {

    private final Map<Integer, Turn> games = new HashMap<>();

    @Override
    public int save(Turn turn) {
        int id = games.size();
        games.put(id, turn);
        return id;
    }

    @Override
    public Map<Integer, Turn> findAll() {
        return Collections.unmodifiableMap(games);
    }

    @Override
    public Optional<Turn> findTurnById(int id) {
        return Optional.ofNullable(games.getOrDefault(id, null));
    }

    @Override
    public int updateById(int id, Turn turn) {
        games.replace(id, turn);
        return 1;
    }

    @Override
    public int deleteById(int id) {
        games.remove(id);
        return 1;
    }
}
