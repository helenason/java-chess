package service;

import controller.RoomCommand;
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
import java.util.Map;
import java.util.Set;

public class GameService {

    private final GameDao gameDao;
    private final BoardDao boardDao;
    private Chess chess;

    public GameService() {
        this.gameDao = new RealGameDao();
        this.boardDao = new RealBoardDao();
    }

    public Set<Integer> findRooms() {
        Map<Integer, Turn> games = gameDao.findAll();
        return games.keySet();
    }

    public int createGame() {
        return gameDao.save(new Turn(Color.WHITE));
    }

    public Chess initChess(RoomCommand roomCommand, int gameId) {
        Board board = createBoard(roomCommand, gameId);
        Turn turn = createTurn(gameId);
        chess = new Chess(board, turn);
        return chess;
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

    private Turn createTurn(int gameId) {
        return gameDao.findTurnById(gameId).orElseThrow(() -> new IllegalArgumentException("[ERROR] 유효하지 않은 차례입니다."));
    }

    public void updateMovement(int gameId, Position sourcePosition, Position targetPosition) {
        Piece targetPiece = chess.tryMove(sourcePosition, targetPosition);
        Piece sourcePiece = chess.getBoard().findPieceByPosition(sourcePosition);
        gameDao.updateById(gameId, chess.getTurn());
        boardDao.updateByGame(gameId, targetPosition, targetPiece);
        boardDao.updateByGame(gameId, sourcePosition, sourcePiece);
    }

    public void resetGame(int gameId) {
        boardDao.deleteByGame(gameId);
        gameDao.deleteById(gameId);
    }
}
