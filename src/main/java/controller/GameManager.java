package controller;

import dao.RealBoardDao;
import dao.RealGameDao;
import domain.board.Board;
import domain.board.Turn;
import domain.position.Position;
import domain.result.ChessResult;
import java.util.Set;
import service.GameService;
import view.InputView;
import view.OutputView;

public class GameManager {

    private final InputView inputView;
    private final OutputView outputView;
    private final GameService gameService;

    public GameManager(InputView inputView, OutputView outputView) {
        this.inputView = inputView;
        this.outputView = outputView;
        this.gameService = new GameService(new RealGameDao(), new RealBoardDao());
    }

    public void start() {
        outputView.printStartNotice();
        Command initCommand = requestInitCommand();
        if (initCommand.isStart()) {
            play();
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

    private void play() {
        Set<Integer> rooms = gameService.findRooms();
        outputView.printRooms(rooms);

        int gameId = requestGame(rooms);
        playGame(gameId);

        ChessResult result = gameService.judge();
        outputView.printResult(result);
        gameService.resetGame(gameId);
    }

    private int requestGame(Set<Integer> rooms) {
        try {
            RoomCommand roomCommand = inputView.readRoomCommand();
            int gameId = requestRoom(roomCommand, rooms);
            initChess(roomCommand, gameId);
            return gameId;
        } catch (IllegalArgumentException e) {
            outputView.printError(e.getMessage());
            inputView.clean();
            return requestGame(rooms);
        }
    }

    private int requestRoom(RoomCommand roomCommand, Set<Integer> rooms) {
        if (roomCommand.wantCreate()) {
            return gameService.createGame();
        }
        int roomNumber = inputView.readRoomNumber();
        validateRoomNumberRange(rooms, roomNumber);
        return roomNumber;
    }

    private void validateRoomNumberRange(Set<Integer> rooms, int roomNumber) {
        if (hasNotRoomNumber(rooms, roomNumber)) {
            throw new IllegalArgumentException("[ERROR] 존재하는 방 번호를 입력해주세요.");
        }
    }

    private boolean hasNotRoomNumber(Set<Integer> roomNumbers, int roomNumber) {
        return roomNumbers.stream()
                .noneMatch(number -> number == roomNumber);
    }

    private void initChess(RoomCommand roomCommand, int gameId) {
        Board board = initBoard(roomCommand, gameId);
        Turn turn = gameService.findTurn(gameId);
        gameService.initChess(board, turn);
        outputView.printBoard(gameService.getBoard());
    }

    private Board initBoard(RoomCommand roomCommand, int gameId) {
        if (roomCommand.wantCreate()) {
            return gameService.createBoard(gameId);
        }
        return gameService.findBoard(gameId);
    }

    private void playGame(int gameId) {
        Command gameCommand;
        do {
            outputView.printTurn(gameService.getTurn());
            gameCommand = requestCommand();
            if (gameCommand.isMove()) {
                tryMoveUntilNoError(gameId);
            }
        } while (gameService.canContinue() && wantContinue(gameCommand));
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

    private void tryMoveUntilNoError(int gameId) {
        try {
            tryMove(gameId);
        } catch (IllegalArgumentException e) {
            outputView.printError(e.getMessage());
        } finally {
            inputView.clean();
        }
    }

    private void tryMove(int gameId) {
        Position sourcePosition = inputView.readPosition();
        Position targetPosition = inputView.readPosition();
        gameService.updateMovement(gameId, sourcePosition, targetPosition);
        outputView.printBoard(gameService.getBoard());
    }

    private boolean wantContinue(Command command) {
        if (command.isStart()) {
            play();
        }
        if (command.isStatus()) {
            ChessResult result = gameService.judge();
            outputView.printScore(result);
        }
        return command.wantContinue();
    }
}
