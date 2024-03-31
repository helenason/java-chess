package domain.board;

import static domain.piece.PositionFixture.A1;
import static domain.piece.PositionFixture.A3;
import static domain.piece.PositionFixture.A4;
import static domain.piece.PositionFixture.A5;
import static domain.piece.PositionFixture.A7;
import static domain.piece.PositionFixture.B2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import dao.BoardDao;
import domain.piece.Bishop;
import domain.piece.Color;
import domain.piece.King;
import domain.piece.Knight;
import domain.piece.Pawn;
import domain.piece.Piece;
import domain.piece.Queen;
import domain.piece.Rook;
import domain.position.File;
import domain.position.Position;
import domain.position.PositionGenerator;
import domain.position.Rank;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class BoardTest {

    private Board board;

    @BeforeEach
    void setUp() {
        board = Board.create();
    }

    @Test
    @DisplayName("해당 위치의 기물을 가져온다.")
    void findPieceByPosition() {
        Position position = PositionGenerator.generate(File.A, Rank.ONE);

        Piece piece = board.findPieceByPosition(position);

        assertThat(piece).isEqualTo(new Rook(Color.WHITE));
    }

    @Test
    @DisplayName("해당 위치로 기물을 옮긴다.")
    void movePiece() {
        Position source = PositionGenerator.generate(File.A, Rank.TWO);
        Position target = PositionGenerator.generate(File.A, Rank.THREE);
        Piece expected = new Pawn(Color.WHITE);

        board.movePiece(source, target);

        Piece actual = board.findPieceByPosition(target);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("경로에 다른 기물이 있는 경우 참을 반환한다. - 직선 경로")
    void isBlocked_Straight_True() {
        Position source = PositionGenerator.generate(File.A, Rank.ONE);
        Position target = PositionGenerator.generate(File.A, Rank.THREE);

        boolean actual = board.isBlocked(source, target);

        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("경로에 다른 기물이 없는 경우 거짓을 반환한다. - 직선 경로")
    void isBlocked_Straight_False() {
        Position source = PositionGenerator.generate(File.A, Rank.TWO);
        Position target = PositionGenerator.generate(File.A, Rank.FOUR);

        boolean actual = board.isBlocked(source, target);

        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("경로에 다른 기물이 있는 경우 참을 반환한다. - 대각선 경로")
    void isBlocked_Diagonal_True() {
        Position source = PositionGenerator.generate(File.C, Rank.ONE);
        Position target = PositionGenerator.generate(File.H, Rank.SIX);

        boolean actual = board.isBlocked(source, target);

        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("경로에 다른 기물이 없는 경우 거짓을 반환한다. - 대각선 경로")
    void isBlocked_Diagonal_False() {
        board.movePiece(PositionGenerator.generate(File.D, Rank.TWO), PositionGenerator.generate(File.D, Rank.THREE));
        Position source = PositionGenerator.generate(File.C, Rank.ONE);
        Position target = PositionGenerator.generate(File.H, Rank.SIX);

        boolean actual = board.isBlocked(source, target);

        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("남아있는 하얀 기물에 대한 정보를 맵 형태로 반환한다.")
    void findRemainPieces_White() {
        Map<Piece, Integer> actual = board.findRemainPieces(Color.WHITE);

        assertAll(() -> {
            assertThat(actual).containsEntry(new Bishop(Color.WHITE), 2);
            assertThat(actual).containsEntry(new King(Color.WHITE), 1);
            assertThat(actual).containsEntry(new Knight(Color.WHITE), 2);
            assertThat(actual).containsEntry(new Queen(Color.WHITE), 1);
            assertThat(actual).containsEntry(new Rook(Color.WHITE), 2);
            assertThat(actual).containsEntry(new Pawn(Color.WHITE), 8);
        });
    }

    @Test
    @DisplayName("남아있는 검정 기물에 대한 정보를 맵 형태로 반환한다.")
    void findRemainPieces_Black() {
        Map<Piece, Integer> actual = board.findRemainPieces(Color.BLACK);

        assertAll(() -> {
            assertThat(actual).containsEntry(new Bishop(Color.BLACK), 2);
            assertThat(actual).containsEntry(new King(Color.BLACK), 1);
            assertThat(actual).containsEntry(new Knight(Color.BLACK), 2);
            assertThat(actual).containsEntry(new Queen(Color.BLACK), 1);
            assertThat(actual).containsEntry(new Rook(Color.BLACK), 2);
            assertThat(actual).containsEntry(new Pawn(Color.BLACK), 8);
        });
    }

    @Test
    @DisplayName("같은 세로줄(파일)에 같은 색 폰이 있는 경우 참을 반환한다.")
    void hasSameColorPawnAtSameFile_True() {
        board.movePiece(A7, A5);
        board.movePiece(A5, A4);
        board.movePiece(A4, A3);
        board.movePiece(B2, A3);

        boolean hasSameColorPawnAtSameFile = board.hasSameColorPawnAtSameFile(Color.WHITE);
        assertThat(hasSameColorPawnAtSameFile).isTrue();
    }

    @Test
    @DisplayName("같은 세로줄(파일)에 같은 색 폰이 없는 경우 거짓을 반환한다.")
    void hasSameColorPawnAtSameFile_False() {
        boolean hasSameColorPawnAtSameFile = board.hasSameColorPawnAtSameFile(Color.WHITE);
        assertThat(hasSameColorPawnAtSameFile).isFalse();
    }

    @Test
    @DisplayName("보드에 남아있는 킹의 개수가 2개이다.")
    void countKing_All_True() {
        int countKing = board.countKing();

        assertThat(countKing).isEqualTo(2);
    }

    @Test
    @DisplayName("보드에 남아있는 킹의 개수가 1개이다.")
    void countKing_One_False() {
        board = Board.create(() -> {
            BoardDao boardDao = new BoardDao();
            boardDao.deleteAll();
            Map<Position, Piece> squares = new HashMap<>();
            squares.put(A1, new King(Color.BLACK));
            boardDao.save(A1, new King(Color.BLACK));
            return squares;
        });

        int countKing = board.countKing();

        assertThat(countKing).isEqualTo(1);
    }

    @Test
    @DisplayName("보드에 남아있는 킹의 개수가 0개이다.")
    void countKing_No_False() {
        board = Board.create(() -> {
            BoardDao boardDao = new BoardDao();
            boardDao.deleteAll();
            return new HashMap<>();
        });

        int countKing = board.countKing();

        assertThat(countKing).isEqualTo(0);
    }
}
