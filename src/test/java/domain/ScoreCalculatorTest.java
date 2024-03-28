package domain;

import static org.assertj.core.api.Assertions.assertThat;

import domain.board.Board;
import domain.board.Turn;
import domain.piece.Color;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ScoreCalculatorTest {

    @Test
    @DisplayName("초기 체스판의 경우 흰색 진영의 점수는 38점이다.")
    void calculate_InitWhite() {
        ScoreCalculator scoreCalculator = new ScoreCalculator();
        Turn turn = new Turn(Color.WHITE);
        Board board = Board.create();

        double score = scoreCalculator.calculate(board, turn);

        assertThat(score).isEqualTo(38);
    }

    @Test
    @DisplayName("초기 체스판의 경우 검은색 진영의 점수는 38점이다.")
    void calculate_InitBlack() {
        ScoreCalculator scoreCalculator = new ScoreCalculator();
        Turn turn = new Turn(Color.BLACK);
        Board board = Board.create();

        double score = scoreCalculator.calculate(board, turn);

        assertThat(score).isEqualTo(38);
    }
}
