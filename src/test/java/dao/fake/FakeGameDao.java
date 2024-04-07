package dao.fake;

import dao.GameDao;
import domain.board.Turn;
import dto.GameData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

public class FakeGameDao implements GameDao {

    private final Map<Integer, Turn> games = new HashMap<>();

    @Override
    public int save(Turn turn) {
        int id = games.size() + 1;
        games.put(id, turn);
        return id;
    }

    @Override
    public List<GameData> findAll() {
        List<GameData> result = new ArrayList<>();
        for (Entry<Integer, Turn> entry : games.entrySet()) {
            int id = entry.getKey();
            Turn turn = entry.getValue();
            result.add(new GameData(id, turn));
        }
        return result;
    }

    @Override
    public Optional<Turn> findTurnById(int id) {
        return Optional.ofNullable(games.get(id));
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
