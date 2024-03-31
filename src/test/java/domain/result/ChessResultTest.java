package domain.result;

import static org.assertj.core.api.Assertions.assertThat;

import domain.piece.Color;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ChessResultTest {

    @Test
    @DisplayName("화이트 진영 킹이 죽었다면 블랙 진영이 승리한다.")
    void findWinner_WhiteKingIsDead_Black() {
        Map<Color, Double> score = new HashMap<>();
        score.put(Color.WHITE, 0D);
        score.put(Color.BLACK, 0D);
        Color deadKing = Color.WHITE;
        ChessResult chessResult = new ChessResult(score, deadKing);

        Color winner = chessResult.findWinner();

        assertThat(winner).isEqualTo(Color.BLACK);
    }

    @Test
    @DisplayName("블랙 진영 킹이 죽었다면 화이트 진영이 승리한다.")
    void findWinner_BlackKingIsDead_White() {
        Map<Color, Double> score = new HashMap<>();
        score.put(Color.WHITE, 0D);
        score.put(Color.BLACK, 0D);
        Color deadKing = Color.BLACK;
        ChessResult chessResult = new ChessResult(score, deadKing);

        Color winner = chessResult.findWinner();

        assertThat(winner).isEqualTo(Color.WHITE);
    }

    @Test
    @DisplayName("죽은 킹이 없는 경우 화이트 진영이 더 높은 점수를 가진다면 화이트 진영이 승리한다.")
    void findWinner_NoDeadKing_White() {
        Map<Color, Double> score = new HashMap<>();
        score.put(Color.WHITE, 10D);
        score.put(Color.BLACK, 0D);
        Color deadKing = Color.NONE;
        ChessResult chessResult = new ChessResult(score, deadKing);

        Color winner = chessResult.findWinner();

        assertThat(winner).isEqualTo(Color.WHITE);
    }

    @Test
    @DisplayName("죽은 킹이 없는 경우 블랙 진영이 더 높은 점수를 가진다면 블랙 진영이 승리한다.")
    void findWinner_NoDeadKing_Black() {
        Map<Color, Double> score = new HashMap<>();
        score.put(Color.WHITE, 0D);
        score.put(Color.BLACK, 10D);
        Color deadKing = Color.NONE;
        ChessResult chessResult = new ChessResult(score, deadKing);

        Color winner = chessResult.findWinner();

        assertThat(winner).isEqualTo(Color.BLACK);
    }
}
