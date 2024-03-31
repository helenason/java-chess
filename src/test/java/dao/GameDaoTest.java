package dao;

import static org.assertj.core.api.Assertions.assertThat;

import domain.board.Turn;
import domain.piece.Color;
import java.sql.Connection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class GameDaoTest {

    private GameDao gameDao;

    @BeforeEach
    void setUp() {
        gameDao = new GameDao();
        gameDao.deleteAll();
    }

    @Test
    @DisplayName("데이터베이스 연결에 성공한다.")
    void getConnection_Success() {
        Connection connection = gameDao.getConnection();

        assertThat(connection).isNotNull();
    }

    @Test
    @DisplayName("데이터를 추가하고 추가된 데이터 개수를 반환한다.")
    void save_Success() {
        Turn turn = new Turn(Color.WHITE);

        int savedCount = gameDao.save(turn);

        assertThat(savedCount).isEqualTo(1);
    }

    @Test
    @DisplayName("게임의 현재 차례를 반환한다.")
    void findTurnById_Success() {
        gameDao.save(new Turn(Color.WHITE));

        Color turn = gameDao.findTurnById().orElse(Color.NONE);

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

        int deletedCount = gameDao.deleteById();

        assertThat(deletedCount).isEqualTo(1);
    }
}
