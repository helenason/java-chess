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

import domain.piece.Bishop;
import domain.piece.King;
import domain.piece.Knight;
import domain.piece.None;
import domain.piece.Pawn;
import domain.piece.Piece;
import domain.piece.Queen;
import domain.piece.Rook;
import domain.position.File;
import domain.position.Position;
import domain.position.PositionGenerator;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class InitBoardGeneratorTest {

    private BoardGenerator boardGenerator;

    @BeforeEach
    void setUp() {
        boardGenerator = new InitBoardGenerator();
    }

    @Test
    @DisplayName("64개의 칸을 생성한다.")
    void generate_SquaresSize() {
        Map<Position, Piece> squares = boardGenerator.generate();

        assertThat(squares).hasSize(64);
    }

    @Test
    @DisplayName("게임 시작 시 폰은 A2 B2 C2 D2 E2 F2 G2 H2에 위치한다.")
    void generate_Pawn() {
        Map<Position, Piece> squares = boardGenerator.generate();

        Piece actual1 = squares.get(A2);
        Piece actual2 = squares.get(B2);
        Piece actual3 = squares.get(C2);
        Piece actual4 = squares.get(D2);
        Piece actual5 = squares.get(E2);
        Piece actual6 = squares.get(F2);
        Piece actual7 = squares.get(G2);
        Piece actual8 = squares.get(H2);
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
        Map<Position, Piece> squares = boardGenerator.generate();

        Piece actual1 = squares.get(A1);
        Piece actual2 = squares.get(A8);
        Piece actual3 = squares.get(A1);
        Piece actual4 = squares.get(A8);
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
        Map<Position, Piece> squares = boardGenerator.generate();

        Piece actual1 = squares.get(B1);
        Piece actual2 = squares.get(B8);
        Piece actual3 = squares.get(G1);
        Piece actual4 = squares.get(G8);
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
        Map<Position, Piece> squares = boardGenerator.generate();

        Piece actual1 = squares.get(C1);
        Piece actual2 = squares.get(C8);
        Piece actual3 = squares.get(F1);
        Piece actual4 = squares.get(F8);
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
        Map<Position, Piece> squares = boardGenerator.generate();

        Piece actual1 = squares.get(D1);
        Piece actual2 = squares.get(D8);
        assertAll(() -> {
            assertThat(actual1).isInstanceOf(Queen.class);
            assertThat(actual2).isInstanceOf(Queen.class);
        });
    }

    @Test
    @DisplayName("게임 시작 시 킹은 E1 E8에 위치한다.")
    void generate_King() {
        Map<Position, Piece> squares = boardGenerator.generate();

        Piece actual1 = squares.get(E1);
        Piece actual2 = squares.get(E8);

        assertAll(() -> {
            assertThat(actual1).isInstanceOf(King.class);
            assertThat(actual2).isInstanceOf(King.class);
        });
    }

    @ParameterizedTest
    @EnumSource(names = {"A", "B", "C", "D", "E", "F", "G", "H"})
    @DisplayName("게임 시작 시 랭크 3, 4, 5, 6은 비어있다.")
    void generate_None(File file) {
        Map<Position, Piece> squares = boardGenerator.generate();

        Piece actual1 = squares.get(PositionGenerator.generate(file, THREE));
        Piece actual2 = squares.get(PositionGenerator.generate(file, FOUR));
        Piece actual3 = squares.get(PositionGenerator.generate(file, FIVE));
        Piece actual4 = squares.get(PositionGenerator.generate(file, SIX));
        assertAll(() -> {
            assertThat(actual1).isInstanceOf(None.class);
            assertThat(actual2).isInstanceOf(None.class);
            assertThat(actual3).isInstanceOf(None.class);
            assertThat(actual4).isInstanceOf(None.class);
        });
    }
}
