package domain;

import domain.board.Board;
import domain.board.Turn;
import domain.piece.Bishop;
import domain.piece.Color;
import domain.piece.King;
import domain.piece.Knight;
import domain.piece.Pawn;
import domain.piece.Piece;
import domain.piece.Queen;
import domain.piece.Rook;
import java.util.HashMap;
import java.util.Map;

public class ScoreCalculator {

    private static final Map<Piece, Double> score;

    static {
        score = new HashMap<>();
        score.put(new Queen(Color.WHITE), 9D);
        score.put(new Rook(Color.WHITE), 5D);
        score.put(new Bishop(Color.WHITE), 3D);
        score.put(new Knight(Color.WHITE), 2.5);
        score.put(new Pawn(Color.WHITE), 1D);
        score.put(new King(Color.WHITE), 0D);
        score.put(new Queen(Color.BLACK), 9D);
        score.put(new Rook(Color.BLACK), 5D);
        score.put(new Bishop(Color.BLACK), 3D);
        score.put(new Knight(Color.BLACK), 2.5);
        score.put(new Pawn(Color.BLACK), 1D);
        score.put(new King(Color.BLACK), 0D); // TODO: 색을 나누어 구별하는 방식 리팩토링
    }

    public double calculate(Board board, Turn turn) {
        Color color = decideColor(turn);
        Map<Piece, Integer> remainPieces = board.findRemainPieces(color);
        double total = remainPieces.entrySet().stream()
                .filter(entry -> entry.getKey().isNotPawn())
                .mapToDouble(entry -> ScoreCalculator.score.getOrDefault(entry.getKey(), 0D) * entry.getValue())
                .sum();
        boolean hasSameColorPawnAtSameFile = board.hasSameColorPawnAtSameFile(color);
        Pawn pawn = new Pawn(color);
        double pawnScore = remainPieces.get(pawn) * score.get(pawn);
        if (hasSameColorPawnAtSameFile && remainPieces.containsKey(pawn)) {
            total += pawnScore * 0.5;
        }
        total += pawnScore;
        return total;
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
