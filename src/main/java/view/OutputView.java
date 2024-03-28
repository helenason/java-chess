package view;

import domain.board.Board;
import domain.board.Turn;
import domain.piece.Color;
import domain.piece.Piece;
import domain.position.File;
import domain.position.Rank;
import domain.result.ChessResult;
import java.util.Arrays;
import java.util.List;
import view.mapper.PieceOutput;

public class OutputView {

    public void printStartNotice() {
        System.out.println("> 체스 게임을 시작합니다.");
        System.out.println("> 게임 시작 : start");
        System.out.println("> 게임 종료 : end");
        System.out.println("> 게임 이동 : move source위치 target위치 - 예. move b2 b3");
    }

    public void printBoard(Board board) {
        List<Piece> pieces = resolvePiecesByOrder(board);
        for (int i = 0; i < pieces.size(); i++) {
            Piece piece = pieces.get(i);
            String pieceOutput = PieceOutput.asOutput(piece);
            System.out.print(pieceOutput);
            separateLineByFileIndex(i);
        }
        printNewLine();
    }

    private List<Piece> resolvePiecesByOrder(Board board) {
        return Rank.reversedValues().stream()
                .flatMap(rank -> Arrays.stream(File.values()).map(file -> board.findPieceByPosition(file, rank)))
                .toList();
    }

    private void separateLineByFileIndex(int fileIndex) {
        if (isLastFile(fileIndex)) {
            printNewLine();
        }
    }

    private boolean isLastFile(int fileIndex) {
        return fileIndex % 8 == 7;
    }

    private void printNewLine() {
        System.out.println();
    }

    public void printTurn(Turn turn) {
        if (turn.isBlack()) {
            System.out.println("블랙(대문자) 진영의 차례입니다.");
        }
        if (turn.isWhite()) {
            System.out.println("화이트(소문자) 진영의 차례입니다.");
        }
    }

    public void printError(String errorMessage) {
        System.out.println(errorMessage);
    }

    public void printResult(ChessResult result) {
        System.out.println("\n> 체스 게임을 종료합니다.\n");
        System.out.println("=== 게임 점수 ===");

        System.out.printf("화이트(소문자) 진영: %1.1f\n", result.getWhiteScore());
        System.out.printf("블랙(대문자) 진영: %1.1f\n", result.getBlackScore());
        System.out.println();

        System.out.println("=== 게임 결과 ===");
        Color winner = result.findWinner();
        if (winner.isWhite()) {
            System.out.println("우승자는 화이트(소문자) 진영입니다.");
            return;
        }
        if (winner.isBlack()) {
            System.out.println("우승자는 블랙(대문자) 진영입니다.");
            return;
        }
        System.out.println("무승부입니다.");
    }
}
