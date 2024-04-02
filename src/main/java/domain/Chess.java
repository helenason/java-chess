package domain;

import domain.board.Board;
import domain.board.Turn;
import domain.piece.Color;
import domain.piece.Piece;
import domain.position.Position;
import domain.result.ChessResult;
import domain.result.ScoreCalculator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Chess {

    private static final int KING_COUNT = 2;

    private final Board board;
    private Turn turn;

    public Chess(Board board, Turn turn) {
        this.board = board;
        this.turn = turn;
    }

    public void tryMove(Position sourcePosition, Position targetPosition) {
        validateMovement(sourcePosition, targetPosition);
        if (canAttack(sourcePosition, targetPosition)) {
            attack(sourcePosition, targetPosition);
            return;
        }
        validateCanMove(sourcePosition, targetPosition);
        move(sourcePosition, targetPosition);
    }

    private void validateMovement(Position sourcePosition, Position targetPosition) {
        validate(sourcePosition.equals(targetPosition), "[ERROR] 제자리에 있을 수 없습니다.");

        Piece sourcePiece = board.findPieceByPosition(sourcePosition);
        Piece targetPiece = board.findPieceByPosition(targetPosition);
        validate(sourcePiece.isBlank(), "[ERROR] 출발지에 기물이 없어 이동하지 못했습니다.");
        validate(sourcePiece.isNotTurn(turn), "[ERROR] 차례가 아니므로 이동하지 못했습니다.");
        validate(board.isBlocked(sourcePosition, targetPosition), "[ERROR] 다른 기물에 막혀 이동하지 못했습니다.");
        validate(targetPiece.isSameColor(sourcePiece), "[ERROR] 도착지에 같은 편 기물이 있어 이동하지 못했습니다.");
    }

    private boolean canAttack(Position sourcePosition, Position targetPosition) {
        Piece sourcePiece = board.findPieceByPosition(sourcePosition);
        Piece targetPiece = board.findPieceByPosition(targetPosition);
        return sourcePiece.canAttack(sourcePosition, targetPosition) && targetPiece.isOppositeSide(sourcePiece);
    }

    private void attack(Position sourcePosition, Position targetPosition) {
        move(sourcePosition, targetPosition);
        // move()와 같은 역할의 함수지만 확장성을 위해 분리하였습니다.
    }

    private void validateCanMove(Position sourcePosition, Position targetPosition) {
        Piece sourcePiece = board.findPieceByPosition(sourcePosition);
        Piece targetPiece = board.findPieceByPosition(targetPosition);
        validate(!sourcePiece.canMove(sourcePosition, targetPosition), "[ERROR] 해당 기물이 움직일 수 없는 방식입니다.");
        validate(targetPiece.isNotBlank(), "[ERROR] 도착지에 기물이 있어 움직일 수 없습니다.");
    }

    private void move(Position sourcePosition, Position targetPosition) {
        board.movePiece(sourcePosition, targetPosition);
        turn = turn.opponent();
    }

    private void validate(boolean condition, String errorMessage) {
        if (condition) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public boolean canContinue() {
        return board.countKing() == KING_COUNT;
    }

    public ChessResult judge() {
        ScoreCalculator scoreCalculator = new ScoreCalculator();
        Map<Color, Double> score = new HashMap<>();
        double own = scoreCalculator.calculate(board, turn);
        double opponent = scoreCalculator.calculate(board, turn.opponent());

        if (turn.isWhite()) {
            score.put(Color.WHITE, own);
            score.put(Color.BLACK, opponent);
            if (board.countKing() == 1) {
                List<Piece> aliveKings = board.findKings();
                Piece aliveKing = aliveKings.get(0);
                return new ChessResult(score, aliveKing.color().oppositeColor());
            }
            return new ChessResult(score, Color.NONE);
        }
        score.put(Color.BLACK, own);
        score.put(Color.WHITE, opponent);
        if (board.countKing() == 1) {
            List<Piece> aliveKings = board.findKings();
            Piece aliveKing = aliveKings.get(0);
            return new ChessResult(score, aliveKing.color().oppositeColor());
        }
        return new ChessResult(score, Color.NONE);
    }

    public Board getBoard() {
        return board;
    }

    public Turn getTurn() {
        return turn;
    }
}
