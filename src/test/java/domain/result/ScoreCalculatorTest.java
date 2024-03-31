package domain.result;

import static domain.piece.PositionFixture.A1;
import static domain.piece.PositionFixture.A2;
import static org.assertj.core.api.Assertions.assertThat;

import dao.BoardDao;
import domain.board.Board;
import domain.board.Turn;
import domain.piece.Bishop;
import domain.piece.Color;
import domain.piece.King;
import domain.piece.Knight;
import domain.piece.Pawn;
import domain.piece.Queen;
import domain.piece.Rook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ScoreCalculatorTest {

    private ScoreCalculator scoreCalculator;

    @BeforeEach
    void setUp() {
        BoardDao boardDao = new BoardDao();
        boardDao.deleteAll();
        scoreCalculator = new ScoreCalculator();
    }

    @Test
    @DisplayName("초기 체스판의 경우 흰색 진영의 점수는 38점이다.")
    void calculate_InitWhite() {
        Turn turn = new Turn(Color.WHITE);
        Board board = Board.create();

        double score = scoreCalculator.calculate(board, turn);

        assertThat(score).isEqualTo(38);
    }

    @Test
    @DisplayName("초기 체스판의 경우 검은색 진영의 점수는 38점이다.")
    void calculate_InitBlack() {
        Turn turn = new Turn(Color.BLACK);
        Board board = Board.create();

        double score = scoreCalculator.calculate(board, turn);

        assertThat(score).isEqualTo(38);
    }

    @Test
    @DisplayName("퀸은 9점으로 계산한다.")
    void calculate_Queen_Nine() {
        Board board = Board.create(() -> {
            BoardDao boardDao = new BoardDao();
            boardDao.deleteAll();
            boardDao.save(A1, new Queen(Color.WHITE));
        });

        Turn turn = new Turn(Color.WHITE);

        double score = scoreCalculator.calculate(board, turn);

        assertThat(score).isEqualTo(9);
    }

    @Test
    @DisplayName("룩은 5점으로 계산한다.")
    void calculate_Rook_Five() {
        Board board = Board.create(() -> {
            BoardDao boardDao = new BoardDao();
            boardDao.deleteAll();
            boardDao.save(A1, new Rook(Color.WHITE));
        });

        Turn turn = new Turn(Color.WHITE);

        double score = scoreCalculator.calculate(board, turn);

        assertThat(score).isEqualTo(5);
    }

    @Test
    @DisplayName("비숍은 3점으로 계산한다.")
    void calculate_Bishop_Three() {
        Board board = Board.create(() -> {
            BoardDao boardDao = new BoardDao();
            boardDao.deleteAll();
            boardDao.save(A1, new Bishop(Color.WHITE));
        });

        Turn turn = new Turn(Color.WHITE);

        double score = scoreCalculator.calculate(board, turn);

        assertThat(score).isEqualTo(3);
    }

    @Test
    @DisplayName("나이트는 2.5점으로 계산한다.")
    void calculate_Bishop_TwoPointFive() {
        Board board = Board.create(() -> {
            BoardDao boardDao = new BoardDao();
            boardDao.deleteAll();
            boardDao.save(A1, new Knight(Color.WHITE));
        });

        Turn turn = new Turn(Color.WHITE);

        double score = scoreCalculator.calculate(board, turn);

        assertThat(score).isEqualTo(2.5);
    }

    @Test
    @DisplayName("킹은 0점으로 계산한다.")
    void calculate_King_Zero() {
        Board board = Board.create(() -> {
            BoardDao boardDao = new BoardDao();
            boardDao.deleteAll();
            boardDao.save(A1, new King(Color.WHITE));
        });

        Turn turn = new Turn(Color.WHITE);

        double score = scoreCalculator.calculate(board, turn);

        assertThat(score).isEqualTo(0);
    }

    @Test
    @DisplayName("폰은 1점으로 계산한다.")
    void calculate_Pawn_One() {
        Board board = Board.create(() -> {
            BoardDao boardDao = new BoardDao();
            boardDao.deleteAll();
            boardDao.save(A1, new Pawn(Color.WHITE));
        });

        Turn turn = new Turn(Color.WHITE);

        double score = scoreCalculator.calculate(board, turn);

        assertThat(score).isEqualTo(1);
    }

    /*
    ........
    ........
    ........
    ........
    ........
    ........
    p.......
    p.......
     */
    @Test
    @DisplayName("하나의 파일에 같은 색 폰이 2개 이상일 경우 폰의 점수는 0.5점으로 계산한다.")
    void calculate_PawnsOnSameFile_ZeroPointFive() {
        Board board = Board.create(() -> {
            BoardDao boardDao = new BoardDao();
            boardDao.deleteAll();
            boardDao.save(A1, new Pawn(Color.WHITE));
            boardDao.save(A2, new Pawn(Color.WHITE));
        });

        Turn turn = new Turn(Color.WHITE);

        double score = scoreCalculator.calculate(board, turn);

        assertThat(score).isEqualTo(0.5 + 0.5);
    }
}
