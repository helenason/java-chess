package dao;

import static domain.piece.PositionFixture.A1;
import static domain.piece.PositionFixture.A2;
import static domain.piece.PositionFixture.A3;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import dao.fake.FakeBoardDao;
import dao.fake.FakeGameDao;
import domain.board.Board;
import domain.board.Turn;
import domain.piece.Bishop;
import domain.piece.Color;
import domain.piece.Knight;
import domain.piece.Piece;
import domain.piece.Rook;
import domain.position.Position;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class FakeBoardDaoTest {

    private int gameId;
    private BoardDao boardDao;

    @BeforeEach
    void setUp() {
        GameDao gameDao = new FakeGameDao();
        gameId = gameDao.save(new Turn(Color.WHITE));
        boardDao = new FakeBoardDao();
    }

    @Test
    @DisplayName("하나의 데이터를 추가하고 추가된 데이터 개수를 반환한다.")
    void save_Success() {
        int savedCount = boardDao.save(gameId, A1, new Rook(Color.WHITE));

        assertThat(savedCount).isEqualTo(1);
    }

    @Test
    @DisplayName("여러 개의 데이터를 추가하고 추가된 데이터 개수를 반환한다.")
    void saveAll_Success() {
        Board board = Board.create();
        int savedCount = boardDao.saveAll(gameId, board);

        assertThat(savedCount).isEqualTo(64);
    }

    @Test
    @DisplayName("해당 게임의 전체 기물들을 반환한다.")
    void findSquaresByGame_Success() {
        Piece actual1 = new Rook(Color.WHITE);
        Piece actual2 = new Bishop(Color.WHITE);
        Piece actual3 = new Knight(Color.WHITE);
        boardDao.save(gameId, A1, actual1);
        boardDao.save(gameId, A2, actual2);
        boardDao.save(gameId, A3, actual3);

        Map<Position, Piece> squares = boardDao.findSquaresByGame(gameId);

        assertAll(() -> {
            assertThat(squares).containsEntry(A1, actual1);
            assertThat(squares).containsEntry(A2, actual2);
            assertThat(squares).containsEntry(A3, actual3);
        });
    }

    @Test
    @DisplayName("데이터를 수정하고 수정된 데이터 개수를 반환한다.")
    void updateByGame_Success() {
        boardDao.save(gameId, A1, new Rook(Color.WHITE));

        int updatedCount = boardDao.updateByGame(gameId, A1, new Rook(Color.BLACK));

        assertThat(updatedCount).isEqualTo(1);
    }

    @Test
    @DisplayName("데이터를 삭제하고 삭제된 데이터 개수를 반환한다.")
    void deleteByGame_Success() {
        boardDao.save(gameId, A1, new Rook(Color.WHITE));
        boardDao.save(gameId, A2, new Knight(Color.WHITE));

        int deletedSize = boardDao.deleteByGame(gameId);

        assertThat(deletedSize).isEqualTo(2);
    }
}
