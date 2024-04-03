package controller;

import dao.BoardDao;
import dao.GameDao;
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

    public GameManager(InputView inputView, OutputView outputView, GameDao gameDao, BoardDao boardDao) {
        this.inputView = inputView;
        this.outputView = outputView;
        this.gameDao = gameDao;
        this.boardDao = boardDao;
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
        Map<Integer, Turn> games = gameDao.findAll();
        Set<Integer> rooms = games.keySet();
        outputView.printRooms(rooms);

        RoomCommand roomCommand = requestRoomCommand();
        int gameId = requestRoom(roomCommand, rooms);

        Chess chess = initChess(roomCommand, gameId);

        playGame(chess, gameId);

        ChessResult result = chess.judge();
        outputView.printResult(result);
        reset(gameId);
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

    private void reset(int gameId) {
        boardDao.deleteByGame(gameId);
        gameDao.deleteById(gameId);
    }

    private Chess initChess(RoomCommand roomCommand, int gameId) {
        Board board = createBoard(roomCommand, gameId);
        Turn turn = gameDao.findTurnById(gameId).orElseGet(() -> new Turn(Color.NONE));
        Chess chess = new Chess(board, turn);
        outputView.printBoard(chess.getBoard());
        return chess;
    }

    private int requestRoom(RoomCommand roomCommand, Set<Integer> rooms) {
        try {
            if (roomCommand.wantCreate()) {
                return gameDao.save(new Turn(Color.WHITE));
            }
            int roomNumber = inputView.readRoomNumber();
            validateRoomNumberRange(rooms, roomNumber);
            return roomNumber;
        } catch (IllegalArgumentException e) {
            outputView.printError(e.getMessage());
            return requestRoom(requestRoomCommand(), rooms);
        }
    }

    private Board createBoard(RoomCommand roomCommand, int gameId) {
        if (roomCommand.wantCreate()) {
            Board board = Board.create();
            boardDao.saveAll(gameId, board);
            return board;
        }
        Map<Position, Piece> squares = boardDao.findSquaresByGame(gameId);
        return Board.create(squares);
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
        Piece targetPiece = chess.tryMove(sourcePosition, targetPosition);
        gameDao.updateById(gameId, chess.getTurn());
        boardDao.updateByGame(gameId, targetPosition, targetPiece);
        boardDao.updateByGame(gameId, sourcePosition, new None(Color.NONE));
        outputView.printBoard(chess.getBoard());
    }
}
