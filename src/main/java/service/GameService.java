package service;

import dao.BoardDao;
import dao.GameDao;
import domain.Chess;
import domain.board.Board;
import domain.board.Turn;
import domain.piece.Color;
import domain.piece.Piece;
import domain.position.Position;
import dto.BoardData;
import dto.GameData;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GameService {

    private final GameDao gameDao;
    private final BoardDao boardDao;
    private Chess chess;

    public GameService(GameDao gameDao, BoardDao boardDao) {
        this.gameDao = gameDao;
        this.boardDao = boardDao;
    }

    public Set<Integer> findRooms() {
        List<GameData> games = gameDao.findAll();
        return games.stream()
                .map(GameData::id)
                .collect(Collectors.toUnmodifiableSet());
    }

    public int createGame() {
        return gameDao.save(new Turn(Color.WHITE));
    }

    public Board createBoard(int gameId) {
        Board board = Board.create();
        boardDao.saveAll(gameId, board);
        return board;
    }

    public Board findBoard(int gameId) {
        BoardData boardData = boardDao.findSquaresByGame(gameId);
        return Board.create(boardData.squares());
    }

    public Turn findTurn(int gameId) {
        return gameDao.findTurnById(gameId).orElseThrow(() -> new IllegalArgumentException("[ERROR] 유효하지 않은 차례입니다."));
    }

    public Chess initChess(Board board, Turn turn) {
        chess = new Chess(board, turn);
        return chess;
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
