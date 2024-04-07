package dao.fake;

import dao.GameDao;
import domain.board.Turn;
import dto.GameData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class FakeGameDao implements GameDao {

    private final List<GameData> games = new ArrayList<>();

    @Override
    public int save(Turn turn) {
        int id = games.size() + 1;
        games.add(new GameData(id, turn));
        return id;
    }

    @Override
    public List<GameData> findAll() {
        return Collections.unmodifiableList(games);
    }

    @Override
    public Optional<Turn> findTurnById(int id) {
        return games.stream()
                .filter(gameData -> gameData.id() == id)
                .findFirst()
                .map(GameData::turn);
    }

    @Override
    public int updateById(int id, Turn turn) {
        deleteById(id);
        games.add(new GameData(id, turn));
        return 1;
    }

    @Override
    public int deleteById(int id) {
        GameData target = games.stream()
                .filter(gameData -> gameData.id() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("[ERROR]"));
        games.remove(target);
        return 1;
    }
}
