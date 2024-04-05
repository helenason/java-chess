package domain.result;

import domain.board.Board;
import domain.board.Turn;
import domain.piece.Color;
import domain.piece.Pawn;
import domain.piece.Piece;
import java.util.Map;

public class ScoreCalculator {

    private static final double HALF_SCORE = 0.5;

    public double calculate(Board board, Turn turn) {
        Color color = decideColor(turn);
        Map<Piece, Integer> remainPieces = board.findRemainPieces(color);
        double totalScoreExceptPawn = scoreExceptPawn(remainPieces);

        Pawn pawn = new Pawn(color);
        if (!remainPieces.containsKey(pawn)) {
            return totalScoreExceptPawn;
        }
        int pawnCount = remainPieces.get(pawn);
        double totalScoreOfPawn = scoreOfPawn(board, pawnCount, pawn);
        return totalScoreExceptPawn + totalScoreOfPawn;
    }

    private double scoreOfPawn(Board board, int pawnCount, Pawn pawn) {
        double score = pawn.calculateScore(pawnCount);
        if (board.hasSameColorPawnAtSameFile(pawn)) {
            return score * HALF_SCORE;
        }
        return score;
    }

    private double scoreExceptPawn(Map<Piece, Integer> remainPieces) {
        return remainPieces.entrySet().stream()
                .filter(entry -> entry.getKey().isNotPawn())
                .mapToDouble(entry -> entry.getKey().calculateScore(entry.getValue()))
                .sum();
    }

    private Color decideColor(Turn turn) {
        if (turn.isBlack()) {
            return Color.BLACK;
        }
        if (turn.isWhite()) {
            return Color.WHITE;
        }
        return Color.NONE;
    }
}
