package domain.result;

import domain.piece.Color;
import java.util.Map;

public class ChessResult {

    private final Map<Color, Double> score;
    private final Color deadKing;

    public ChessResult(Map<Color, Double> score, Color deadKing) {
        this.score = score;
        this.deadKing = deadKing;
    }

    public Color findWinner() {
        if (deadKing.isNotNone()) {
            return deadKing.oppositeColor();
        }
        double whiteScore = score.get(Color.WHITE);
        double blackScore = score.get(Color.BLACK);
        if (whiteScore > blackScore) {
            return Color.WHITE;
        }
        if (blackScore > whiteScore) {
            return Color.BLACK;
        }
        return Color.NONE;
    }

    public double getWhiteScore() {
        return score.get(Color.WHITE);
    }

    public double getBlackScore() {
        return score.get(Color.BLACK);
    }
}
