package dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import dao.fake.FakeGameDao;
import domain.board.Turn;
import domain.piece.Color;
import dto.GameData;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class FakeGameDaoTest {

    private GameDao gameDao;

    @BeforeEach
    void setUp() {
        gameDao = new FakeGameDao();
    }

    @Test
    @DisplayName("데이터를 추가하고 추가된 행의 id는 정수이다.")
    void save_Success() {
        Turn turn = new Turn(Color.WHITE);

        int gameId = gameDao.save(turn);

        assertThat(gameId).isInstanceOf(Integer.class);
    }

    @Test
    @DisplayName("전체 데이터를 반환한다.")
    void findAll_Success() {
        Turn turn = new Turn(Color.WHITE);
        int gameId1 = gameDao.save(turn);
        int gameId2 = gameDao.save(turn);
        int gameId3 = gameDao.save(turn);

        List<GameData> games = gameDao.findAll();

        assertAll(() -> {
            assertThat(games).containsExactly(
                    new GameData(gameId1, turn),
                    new GameData(gameId2, turn),
                    new GameData(gameId3, turn)
            );
        });
    }

    @Test
    @DisplayName("게임의 현재 차례를 찾아 반환한다.")
    void findTurnById_Success() {
        int gameId = gameDao.save(new Turn(Color.WHITE));

        Turn turn = gameDao.findTurnById(gameId).orElseGet(() -> new Turn(Color.NONE));

        assertThat(turn.isWhite()).isTrue();
    }

    @Test
    @DisplayName("게임의 현재 차례가 없다면 빈 Optional을 반환한다.")
    void findTurnById_Fail() {
        Optional<Turn> turn = gameDao.findTurnById(0);

        assertThat(turn).isEqualTo(Optional.empty());
    }

    @Test
    @DisplayName("차례를 수정한 후 수정된 데이터 개수를 반환한다.")
    void updateById_Success() {
        int gameId = gameDao.save(new Turn(Color.WHITE));

        int updatedCount = gameDao.updateById(gameId, new Turn(Color.BLACK));

        assertThat(updatedCount).isEqualTo(1);
    }

    @Test
    @DisplayName("데이터를 삭제한 후 삭제된 데이터 개수를 반환한다.")
    void deleteById_Success() {
        int gameId = gameDao.save(new Turn(Color.WHITE));

        int deletedCount = gameDao.deleteById(gameId);

        assertThat(deletedCount).isEqualTo(1);
    }
}
