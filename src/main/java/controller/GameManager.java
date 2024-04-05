package controller;

import domain.Chess;
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

    public GameManager(InputView inputView, OutputView outputView, GameService gameService) {
        this.inputView = inputView;
        this.outputView = outputView;
        this.gameService = gameService;
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

        RoomCommand roomCommand = requestRoomCommand();
        int gameId = requestRoom(roomCommand, rooms);

        Chess chess = initChess(roomCommand, gameId);

        playGame(chess, gameId);

        ChessResult result = chess.judge();
        outputView.printResult(result);
        gameService.resetGame(gameId);
    }

    private void playGame(Chess chess, int gameId) {
        Command gameCommand;
        do {
            outputView.printTurn(chess.getTurn());
            gameCommand = requestCommand();
            if (gameCommand.isMove()) {
                tryMoveUntilNoError(chess, gameId);
            }
        } while (chess.canContinue() && wantContinue(chess, gameCommand));
    }

    private RoomCommand requestRoomCommand() {
        try {
            return inputView.readRoomCommand();
        } catch (IllegalArgumentException e) {
            outputView.printError(e.getMessage());
            return requestRoomCommand();
        }
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

    private Chess initChess(RoomCommand roomCommand, int gameId) {
        Chess chess = gameService.initChess(roomCommand, gameId);
        outputView.printBoard(chess.getBoard());
        return chess;
    }

    private int requestRoom(RoomCommand roomCommand, Set<Integer> rooms) {
        try {
            if (roomCommand.wantCreate()) {
                return gameService.createGame();
            }
            int roomNumber = inputView.readRoomNumber();
            validateRoomNumberRange(rooms, roomNumber);
            return roomNumber;
        } catch (IllegalArgumentException e) {
            outputView.printError(e.getMessage());
            return requestRoom(requestRoomCommand(), rooms);
        }
    }

    private boolean wantContinue(Chess chess, Command command) {
        if (command.isStart()) {
            play();
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
        gameService.updateMovement(gameId, sourcePosition, targetPosition);
        outputView.printBoard(chess.getBoard());
    }
}
