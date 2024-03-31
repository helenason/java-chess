package domain.board;

import static domain.piece.PositionFixture.A1;
import static domain.piece.PositionFixture.A2;
import static domain.piece.PositionFixture.A8;
import static domain.piece.PositionFixture.B1;
import static domain.piece.PositionFixture.B2;
import static domain.piece.PositionFixture.B8;
import static domain.piece.PositionFixture.C1;
import static domain.piece.PositionFixture.C2;
import static domain.piece.PositionFixture.C8;
import static domain.piece.PositionFixture.D1;
import static domain.piece.PositionFixture.D2;
import static domain.piece.PositionFixture.D8;
import static domain.piece.PositionFixture.E1;
import static domain.piece.PositionFixture.E2;
import static domain.piece.PositionFixture.E8;
import static domain.piece.PositionFixture.F1;
import static domain.piece.PositionFixture.F2;
import static domain.piece.PositionFixture.F8;
import static domain.piece.PositionFixture.G1;
import static domain.piece.PositionFixture.G2;
import static domain.piece.PositionFixture.G8;
import static domain.piece.PositionFixture.H2;
import static domain.position.Rank.FIVE;
import static domain.position.Rank.FOUR;
import static domain.position.Rank.SIX;
import static domain.position.Rank.THREE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import dao.BoardDao;
import domain.piece.Bishop;
import domain.piece.Color;
import domain.piece.King;
import domain.piece.Knight;
import domain.piece.None;
import domain.piece.Pawn;
import domain.piece.Piece;
import domain.piece.Queen;
import domain.piece.Rook;
import domain.position.File;
import domain.position.PositionGenerator;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class InitBoardGeneratorTest {

    private final BoardDao boardDao = new BoardDao();

    @Test
    @DisplayName("64개의 칸을 생성한다.")
    void generate_SquaresSize() {
        InitBoardGenerator initBoardGenerator = new InitBoardGenerator();

        initBoardGenerator.generate();

        List<Piece> pieces = boardDao.findAllPieces();
        assertThat(pieces).hasSize(64);
    }

    @Test
    @DisplayName("게임 시작 시 폰은 A2 B2 C2 D2 E2 F2 G2 H2에 위치한다.")
    void generate_Pawn() {
        InitBoardGenerator initBoardGenerator = new InitBoardGenerator();
        initBoardGenerator.generate();

        Piece actual1 = boardDao.findPieceByPosition(A2).orElseGet(() -> new None(Color.NONE));
        Piece actual2 = boardDao.findPieceByPosition(B2).orElseGet(() -> new None(Color.NONE));
        Piece actual3 = boardDao.findPieceByPosition(C2).orElseGet(() -> new None(Color.NONE));
        Piece actual4 = boardDao.findPieceByPosition(D2).orElseGet(() -> new None(Color.NONE));
        Piece actual5 = boardDao.findPieceByPosition(E2).orElseGet(() -> new None(Color.NONE));
        Piece actual6 = boardDao.findPieceByPosition(F2).orElseGet(() -> new None(Color.NONE));
        Piece actual7 = boardDao.findPieceByPosition(G2).orElseGet(() -> new None(Color.NONE));
        Piece actual8 = boardDao.findPieceByPosition(H2).orElseGet(() -> new None(Color.NONE));

        assertAll(() -> {
            assertThat(actual1).isInstanceOf(Pawn.class);
            assertThat(actual2).isInstanceOf(Pawn.class);
            assertThat(actual3).isInstanceOf(Pawn.class);
            assertThat(actual4).isInstanceOf(Pawn.class);
            assertThat(actual5).isInstanceOf(Pawn.class);
            assertThat(actual6).isInstanceOf(Pawn.class);
            assertThat(actual7).isInstanceOf(Pawn.class);
            assertThat(actual8).isInstanceOf(Pawn.class);
        });
    }

    @Test
    @DisplayName("게임 시작 시 룩은 A1 A8 H1 H8에 위치한다.")
    void generate_Rook() {
        InitBoardGenerator initBoardGenerator = new InitBoardGenerator();
        initBoardGenerator.generate();

        Piece actual1 = boardDao.findPieceByPosition(A1).orElseGet(() -> new None(Color.NONE));
        Piece actual2 = boardDao.findPieceByPosition(A8).orElseGet(() -> new None(Color.NONE));
        Piece actual3 = boardDao.findPieceByPosition(A1).orElseGet(() -> new None(Color.NONE));
        Piece actual4 = boardDao.findPieceByPosition(A8).orElseGet(() -> new None(Color.NONE));

        assertAll(() -> {
            assertThat(actual1).isInstanceOf(Rook.class);
            assertThat(actual2).isInstanceOf(Rook.class);
            assertThat(actual3).isInstanceOf(Rook.class);
            assertThat(actual4).isInstanceOf(Rook.class);
        });
    }

    @Test
    @DisplayName("게임 시작 시 나이트는 B1 B8 G1 G8에 위치한다.")
    void generate_Knight() {
        InitBoardGenerator initBoardGenerator = new InitBoardGenerator();
        initBoardGenerator.generate();

        Piece actual1 = boardDao.findPieceByPosition(B1).orElseGet(() -> new None(Color.NONE));
        Piece actual2 = boardDao.findPieceByPosition(B8).orElseGet(() -> new None(Color.NONE));
        Piece actual3 = boardDao.findPieceByPosition(G1).orElseGet(() -> new None(Color.NONE));
        Piece actual4 = boardDao.findPieceByPosition(G8).orElseGet(() -> new None(Color.NONE));

        assertAll(() -> {
            assertThat(actual1).isInstanceOf(Knight.class);
            assertThat(actual2).isInstanceOf(Knight.class);
            assertThat(actual3).isInstanceOf(Knight.class);
            assertThat(actual4).isInstanceOf(Knight.class);
        });
    }

    @Test
    @DisplayName("게임 시작 시 비숍은 C1 C8 F1 F8에 위치한다.")
    void generate_Bishop() {
        InitBoardGenerator initBoardGenerator = new InitBoardGenerator();
        initBoardGenerator.generate();

        Piece actual1 = boardDao.findPieceByPosition(C1).orElseGet(() -> new None(Color.NONE));
        Piece actual2 = boardDao.findPieceByPosition(C8).orElseGet(() -> new None(Color.NONE));
        Piece actual3 = boardDao.findPieceByPosition(F1).orElseGet(() -> new None(Color.NONE));
        Piece actual4 = boardDao.findPieceByPosition(F8).orElseGet(() -> new None(Color.NONE));

        assertAll(() -> {
            assertThat(actual1).isInstanceOf(Bishop.class);
            assertThat(actual2).isInstanceOf(Bishop.class);
            assertThat(actual3).isInstanceOf(Bishop.class);
            assertThat(actual4).isInstanceOf(Bishop.class);
        });
    }

    @Test
    @DisplayName("게임 시작 시 퀸은 D1 D8에 위치한다.")
    void generate_Queen() {
        InitBoardGenerator initBoardGenerator = new InitBoardGenerator();
        initBoardGenerator.generate();

        Piece actual1 = boardDao.findPieceByPosition(D1).orElseGet(() -> new None(Color.NONE));
        Piece actual2 = boardDao.findPieceByPosition(D8).orElseGet(() -> new None(Color.NONE));

        assertAll(() -> {
            assertThat(actual1).isInstanceOf(Queen.class);
            assertThat(actual2).isInstanceOf(Queen.class);
        });
    }

    @Test
    @DisplayName("게임 시작 시 킹은 E1 E8에 위치한다.")
    void generate_King() {
        InitBoardGenerator initBoardGenerator = new InitBoardGenerator();
        initBoardGenerator.generate();

        Piece actual1 = boardDao.findPieceByPosition(E1).orElseGet(() -> new None(Color.NONE));
        Piece actual2 = boardDao.findPieceByPosition(E8).orElseGet(() -> new None(Color.NONE));

        assertAll(() -> {
            assertThat(actual1).isInstanceOf(King.class);
            assertThat(actual2).isInstanceOf(King.class);
        });
    }

    @ParameterizedTest
    @EnumSource(names = {"A", "B", "C", "D", "E", "F", "G", "H"})
    @DisplayName("게임 시작 시 랭크 3, 4, 5, 6은 비어있다.")
    void generate_None(File file) {
        InitBoardGenerator initBoardGenerator = new InitBoardGenerator();
        initBoardGenerator.generate();

        Piece actual1 = boardDao.findPieceByPosition(PositionGenerator.generate(file, THREE))
                .orElseGet(() -> new None(Color.NONE));
        Piece actual2 = boardDao.findPieceByPosition(PositionGenerator.generate(file, FOUR))
                .orElseGet(() -> new None(Color.NONE));
        Piece actual3 = boardDao.findPieceByPosition(PositionGenerator.generate(file, FIVE))
                .orElseGet(() -> new None(Color.NONE));
        Piece actual4 = boardDao.findPieceByPosition(PositionGenerator.generate(file, SIX))
                .orElseGet(() -> new None(Color.NONE));

        assertAll(() -> {
            assertThat(actual1).isInstanceOf(None.class);
            assertThat(actual2).isInstanceOf(None.class);
            assertThat(actual3).isInstanceOf(None.class);
            assertThat(actual4).isInstanceOf(None.class);
        });
    }
}
