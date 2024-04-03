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
import java.util.Set;
import view.mapper.output.ColorOutput;
import view.mapper.output.PieceOutput;

public class OutputView {

    public void printStartNotice() {
        System.out.println("> 체스 게임을 시작합니다.");
        System.out.println("> 게임 시작 : start");
        System.out.println("> 게임 종료 : end");
        System.out.println("> 게임 이동 : move source위치 target위치 - 예. move b2 b3");
    }

    public void printRooms(Set<Integer> rooms) {
        if (rooms.isEmpty()) {
            System.out.println("방이 존재하지 않습니다. 방을 개설해주세요.");
            return;
        }
        System.out.println("=== 게임방 목록 시작 ===");
        rooms.forEach(roomId -> {
            System.out.printf("%d번 게임방\n", roomId);
        });
        System.out.println("=== 게임방 목록 끝 ===");
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
        String colorOutput = ColorOutput.asOutput(turn);
        System.out.printf("%s 진영의 차례입니다.\n", colorOutput);
    }

    public void printError(String errorMessage) {
        System.out.println(errorMessage);
    }

    public void printResult(ChessResult result) {
        System.out.println("\n> 체스 게임을 종료합니다.\n");
        printScore(result);
        printWinner(result);
    }

    public void printScore(ChessResult result) {
        System.out.println("=== 게임 점수 ===");
        System.out.printf("%s 진영: %1.1f\n", ColorOutput.WHITE.output(), result.getWhiteScore());
        System.out.printf("%s 진영: %1.1f\n", ColorOutput.BLACK.output(), result.getBlackScore());
        System.out.println();
    }

    private void printWinner(ChessResult result) {
        System.out.println("=== 게임 결과 ===");
        Color winner = result.findWinner();
        if (winner.isNone()) {
            System.out.println("무승부입니다.");
            return;
        }
        String colorOutput = ColorOutput.asOutput(winner);
        System.out.printf("우승자는 %s 진영입니다.\n", colorOutput);
    }
}
