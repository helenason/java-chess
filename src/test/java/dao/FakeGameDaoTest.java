package dao;

import static org.assertj.core.api.Assertions.assertThat;

import dao.fake.FakeGameDao;
import domain.board.Turn;
import domain.piece.Color;
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
    @DisplayName("데이터를 추가하고 추가된 행의 개수를 반환한다.")
    void save_Success() {
        Turn turn = new Turn(Color.WHITE);

        int gameId = gameDao.save(turn);

        assertThat(gameId).isEqualTo(1);
    }

    @Test
    @DisplayName("게임의 현재 차례를 반환한다.")
    void findTurnById_Success() {
        gameDao.save(new Turn(Color.WHITE));

        Turn turn = gameDao.findTurn().orElseGet(() -> new Turn(Color.NONE));

        assertThat(turn.isWhite()).isTrue();
    }

    @Test
    @DisplayName("차례를 수정한 후 수정된 데이터 개수를 반환한다.")
    void update_Success() {
        gameDao.save(new Turn(Color.WHITE));

        Turn turn = new Turn(Color.WHITE);
        int updatedCount = gameDao.update(turn);

        assertThat(updatedCount).isEqualTo(1);
    }

    @Test
    @DisplayName("데이터를 삭제한 후 삭제된 데이터 개수를 반환한다.")
    void delete_Success() {
        gameDao.save(new Turn(Color.WHITE));

        int deletedCount = gameDao.delete();

        assertThat(deletedCount).isEqualTo(1);
    }
}
