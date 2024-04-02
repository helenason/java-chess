package controller;

import dao.BoardDao;
import dao.GameDao;
import dao.RealBoardDao;
import dao.RealGameDao;
import domain.Chess;
import domain.board.Board;
import domain.board.Turn;
import domain.piece.Color;
import domain.piece.Piece;
import domain.position.Position;
import domain.result.ChessResult;
import java.util.Map;
import view.InputView;
import view.OutputView;

public class GameManager {

    private final InputView inputView;
    private final OutputView outputView;

    public GameManager(InputView inputView, OutputView outputView) {
        this.inputView = inputView;
        this.outputView = outputView;
    }

    public void start() {
        outputView.printStartNotice();
        Command initCommand = requestInitCommand();
        if (initCommand.isStart()) {
            playGame();
        }
    }

    private Command requestInitCommand() {
        try {
            return inputView.readInitCommand();
        } catch (IllegalArgumentException e) {
            outputView.printError(e.getMessage());
            return requestInitCommand();
        }
    }

    private void playGame() {
        Chess chess = initChess();
        Command command;
        do {
            outputView.printTurn(chess.getTurn());
            command = requestCommand();
            if (command.isMove()) { // TODO: 들여쓰기 줄이기
                tryMoveUntilNoError(chess);
            }
        } while (chess.canContinue() && wantContinue(chess, command));

        ChessResult result = chess.judge();
        outputView.printResult(result);
        reset();
    }

    private void reset() {
        GameDao gameDao = new RealGameDao();
        gameDao.delete();
        BoardDao boardDao = new RealBoardDao();
        boardDao.delete();
    }

    private Chess initChess() {
        Board board = createBoard();
        Turn turn = createTurn();
        Chess chess = new Chess(board, turn);
        outputView.printBoard(chess.getBoard());
        return chess;
    }

    private Turn createTurn() {
        GameDao gameDao = new RealGameDao();
        if (isGameExist(gameDao)) {
            return gameDao.findTurn().orElseGet(() -> new Turn(Color.WHITE));
        }
        Turn turn = new Turn(Color.WHITE);
        gameDao.save(turn);
        return turn;
    }

    private boolean isGameExist(GameDao gameDao) {
        return gameDao.countAll() != 0;
    }

    private Board createBoard() {
        BoardDao boardDao = new RealBoardDao();
        if (isBoardExist(boardDao)) {
            Map<Position, Piece> squares = boardDao.findAllSquares();
            return Board.create(squares);
        }
        Board board = Board.create();
        boardDao.saveAll(board);
        return board;
    }

    private boolean isBoardExist(BoardDao boardDao) {
        return boardDao.countAll() != 0;
    }

    private boolean wantContinue(Chess chess, Command command) {
        if (command.isStart()) {
            playGame();
        }
        if (command.isStatus()) {
            ChessResult result = chess.judge();
            outputView.printScore(result);
        }
        return command.wantContinue();
    }

    private Command requestCommand() {
        try {
            return inputView.readCommand();
        } catch (IllegalArgumentException e) {
            outputView.printError(e.getMessage());
            inputView.clean();
            return requestCommand();
        }
    }

    private void tryMoveUntilNoError(Chess chess) {
        try {
            tryMove(chess);
        } catch (IllegalArgumentException e) {
            outputView.printError(e.getMessage());
        } finally {
            inputView.clean();
        }
    }

    private void tryMove(Chess chess) {
        Position sourcePosition = inputView.readPosition();
        Position targetPosition = inputView.readPosition();
        chess.tryMove(sourcePosition, targetPosition);
        GameDao gameDao = new RealGameDao();
        gameDao.update(chess.getTurn());
        outputView.printBoard(chess.getBoard());
    }
}
