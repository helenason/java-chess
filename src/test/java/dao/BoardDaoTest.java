package dao;

import static domain.piece.PositionFixture.A1;
import static domain.piece.PositionFixture.A2;
import static domain.piece.PositionFixture.A3;
import static domain.piece.PositionFixture.A4;
import static org.assertj.core.api.Assertions.assertThat;

import domain.piece.Bishop;
import domain.piece.Color;
import domain.piece.Knight;
import domain.piece.None;
import domain.piece.Piece;
import domain.piece.Queen;
import domain.piece.Rook;
import domain.position.File;
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
        Connection connection = boardDao.getConnection();

        assertThat(connection).isNotNull();
    }

    @Test
    @DisplayName("데이터를 추가하고 추가된 데이터 개수를 반환한다.")
    void save_Success() {
        int savedCount = boardDao.save(A1, new Rook(Color.WHITE));

        assertThat(savedCount).isEqualTo(1);
    }

    @Test
    @DisplayName("해당 파일에 위치한 기물들을 리스트로 담아 반환한다.")
    void findPiecesByFile_Success() {
        Piece actual1 = new Rook(Color.WHITE);
        Piece actual2 = new Bishop(Color.WHITE);
        Piece actual3 = new Knight(Color.WHITE);
        boardDao.save(A1, actual1);
        boardDao.save(A2, actual2);
        boardDao.save(A3, actual3);

        List<Piece> pieces = boardDao.findPiecesByFile(File.A);

        assertThat(pieces).containsExactly(actual1, actual2, actual3);
    }

    @Test
    @DisplayName("해당 위치에 기물이 있는 경우 기물을 Optional로 감싸 반환한다.")
    void findPieceByPosition_Success() {
        Piece expected = new Rook(Color.WHITE);
        boardDao.save(A1, expected);

        Piece actual = boardDao.findPieceByPosition(A1).orElseGet(() -> new None(Color.NONE));

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("해당 위치에 기물이 없는 경우 빈 Optional을 반환한다.")
    void findPieceByPosition_Success_Empty() {
        boolean actual = boardDao.findPieceByPosition(A1).isEmpty();

        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("저장된 모든 기물 데이터를 반환한다.")
    void findAllPieces_Success() {
        boardDao.save(A1, new Rook(Color.WHITE));
        boardDao.save(A2, new Knight(Color.WHITE));
        boardDao.save(A3, new Bishop(Color.BLACK));
        boardDao.save(A4, new Queen(Color.BLACK));

        List<Piece> pieces = boardDao.findAllPieces();

        assertThat(pieces).containsExactly(
                new Rook(Color.WHITE),
                new Knight(Color.WHITE),
                new Bishop(Color.BLACK),
                new Queen(Color.BLACK)
        );
    }

    @Test
    @DisplayName("데이터를 수정하고 수정된 데이터 개수를 반환한다.")
    void update_Success() {
        boardDao.save(A1, new Rook(Color.WHITE));
        int updatedCount = boardDao.update(A1, new Knight(Color.BLACK));

        assertThat(updatedCount).isEqualTo(1);
    }

    @Test
    @DisplayName("데이터를 수정하고 수정된 데이터를 확인한다.")
    void update_Success_Check() {
        Piece before = new Rook(Color.WHITE);
        Piece after = new Knight(Color.BLACK);
        boardDao.save(A1, before);

        boardDao.update(A1, after);

        assertThat(boardDao.findPieceByPosition(A1).orElseGet(() -> new None(Color.NONE)))
                .isEqualTo(after);
    }

    @Test
    @DisplayName("데이터베이스 내 데이터를 모두 삭제한다.")
    void deleteAll_Success() {
        boardDao.save(A1, new Rook(Color.WHITE));
        boardDao.save(A2, new Knight(Color.WHITE));

        assertThat(boardDao.deleteAll()).isEqualTo(2);
        assertThat(boardDao.findAllPieces()).hasSize(0);
    }
}
