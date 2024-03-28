package domain.result;

import domain.piece.Color;

public class ChessResult {

    private final double whiteScore;
    private final double blackScore;

    public ChessResult(double whiteScore, double blackScore) {
        this.whiteScore = whiteScore;
        this.blackScore = blackScore;
    }

    public Color findWinner() {
        if (whiteScore > blackScore) {
            return Color.WHITE;
        }
        if (blackScore > whiteScore) {
            return Color.BLACK;
        }
        return Color.NONE;
    }

    public double getWhiteScore() {
        return whiteScore;
    }

    public double getBlackScore() {
        return blackScore;
    }
}
