package dao;

import static domain.piece.PositionFixture.A1;
import static domain.piece.PositionFixture.A2;
import static domain.piece.PositionFixture.A3;
import static domain.piece.PositionFixture.A4;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import data.BoardData;
import domain.piece.Bishop;
import domain.piece.Color;
import domain.piece.Knight;
import domain.piece.Queen;
import domain.piece.Rook;
import java.sql.Connection;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class BoardDaoTest {

    private BoardDao boardDao;

    @BeforeEach
    void setUp() {
        boardDao = new BoardDao();
        boardDao.deleteAll();
    }

    @Test
    @DisplayName("데이터베이스 연결에 성공한다.")
    void getConnection_Success() {
        BoardDao boardDao = new BoardDao();

        Connection connection = boardDao.getConnection();

        assertThat(connection).isNotNull();
    }

    @Test
    @DisplayName("데이터베이스에 데이터를 추가한다.")
    void save_Success() {
        BoardDao boardDao = new BoardDao();

        int savedCount = boardDao.save(A1, new Rook(Color.WHITE));

        assertThat(savedCount).isEqualTo(1);
    }

    @Test
    @DisplayName("데이터베이스에 파일, 랭크가 같은 데이터 추가를 시도할 경우 에러가 발생한다.")
    void save_DuplicatedFileRank_Fail() {
        BoardDao boardDao = new BoardDao();

        boardDao.save(A1, new Rook(Color.WHITE));

        assertThatThrownBy(() -> boardDao.save(A1, new Rook(Color.WHITE)))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("java.sql.SQLIntegrityConstraintViolationException: Duplicate entry");
    }

    @Test
    @DisplayName("저장된 모든 데이터를 반환한다.")
    void findAll_Success() {
        BoardDao boardDao = new BoardDao();
        boardDao.save(A1, new Rook(Color.WHITE));
        boardDao.save(A2, new Knight(Color.WHITE));
        boardDao.save(A3, new Bishop(Color.BLACK));
        boardDao.save(A4, new Queen(Color.BLACK));

        List<BoardData> boards = boardDao.findAll();

        assertThat(boards).contains(
                new BoardData(1, 1, "r"),
                new BoardData(1, 2, "n"),
                new BoardData(1, 3, "B"),
                new BoardData(1, 4, "Q")
        );
    }

    @Test
    @DisplayName("데이터베이스 내 데이터를 모두 삭제한다.")
    void deleteAll_Success() {
        BoardDao boardDao = new BoardDao();

        boardDao.save(A1, new Rook(Color.WHITE));
        boardDao.save(A2, new Knight(Color.WHITE));

        assertThat(boardDao.deleteAll()).isEqualTo(2);
    }
}
