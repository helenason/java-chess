package controller;

import dao.BoardDao;
import dao.GameDao;
import dao.RealBoardDao;
import dao.RealGameDao;
import domain.Chess;
import domain.board.Board;
import domain.board.Turn;
import domain.piece.Color;
import domain.piece.None;
import domain.piece.Piece;
import domain.position.Position;
import domain.result.ChessResult;
import java.util.Map;
import java.util.Set;
import view.InputView;
import view.OutputView;

public class GameManager {

    private final InputView inputView;
    private final OutputView outputView;
    private final GameDao gameDao;
    private final BoardDao boardDao;

    public GameManager(InputView inputView, OutputView outputView) {
        this.inputView = inputView;
        this.outputView = outputView;
        this.gameDao = new RealGameDao();
        this.boardDao = new RealBoardDao();
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
        Map<Integer, Turn> games = gameDao.findAll();

        outputView.printGames(games);
        String option = requestEnterOption(games);

        int gameId = createGame(option);
        Turn turn = gameDao.findTurnById(gameId).orElseGet(() -> new Turn(Color.NONE));
        Board board = createBoard(option, gameId);

        Chess chess = initChess(board, turn);

        Command command;
        do {
            outputView.printTurn(chess.getTurn());
            command = requestCommand();
            if (command.isMove()) { // TODO: 들여쓰기 줄이기
                tryMoveUntilNoError(chess, gameId);
            }
        } while (chess.canContinue() && wantContinue(chess, command));

        ChessResult result = chess.judge();
        outputView.printResult(result);
        reset(gameId);
    }

    private String requestEnterOption(Map<Integer, Turn> games) {
        try {
            String enterOption = inputView.readEnterOption();
            validateRoomNumberRange(games, enterOption);
            return enterOption;
        } catch (IllegalArgumentException e) {
            outputView.printError(e.getMessage());
            return requestEnterOption(games);
        }
    }

    private void validateRoomNumberRange(Map<Integer, Turn> games, String enterOption) {
        if (enterOption.equals("new")) {
            return;
        }
        int roomNumber = Integer.parseInt(enterOption);
        if (hasRoomNumber(games.keySet(), roomNumber)) {
            throw new IllegalArgumentException("[ERROR] 존재하는 방 번호를 입력해주세요.");
        }
    }

    private boolean hasRoomNumber(Set<Integer> roomNumbers, int roomNumber) {
        return roomNumbers.stream()
                .noneMatch(number -> number == roomNumber);
    }

    private void reset(int gameId) {
        boardDao.deleteByGame(gameId);
        gameDao.deleteById(gameId);
    }

    private Chess initChess(Board board, Turn turn) {
        Chess chess = new Chess(board, turn);
        outputView.printBoard(chess.getBoard());
        return chess;
    }

    private int createGame(String option) {
        if (option.equals("new")) {
            return gameDao.save(new Turn(Color.WHITE));
        }
        return Integer.parseInt(option);
    }

    private Board createBoard(String option, int gameId) {
        if (option.equals("new")) {
            Board board = Board.create();
            boardDao.saveAll(gameId, board);
            return board;
        }
        Map<Position, Piece> squares = boardDao.findSquaresByGame(gameId);
        System.out.println(squares.size());
        return Board.create(squares);
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

    private void tryMoveUntilNoError(Chess chess, int gameId) {
        try {
            tryMove(chess, gameId);
        } catch (IllegalArgumentException e) {
            outputView.printError(e.getMessage());
        } finally {
            inputView.clean();
        }
    }

    private void tryMove(Chess chess, int gameId) {
        Position sourcePosition = inputView.readPosition();
        Position targetPosition = inputView.readPosition();
        Piece targetPiece = chess.tryMove(sourcePosition, targetPosition);
        gameDao.updateById(gameId, chess.getTurn());
        boardDao.updateByGame(gameId, targetPosition, targetPiece);
        boardDao.updateByGame(gameId, sourcePosition, new None(Color.NONE));
        outputView.printBoard(chess.getBoard());
    }
}
