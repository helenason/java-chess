package dao;

import domain.board.Board;
import domain.piece.Piece;
import domain.position.File;
import domain.position.Position;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface BoardDao {

    int save(int gameId, Position position, Piece piece);

    int saveAll(int gameId, Board board);

    int countAll();

    List<Piece> findPiecesByType(int gameId, Class<? extends Piece> pieceType);

    List<Piece> findPiecesByFile(int gameId, File file);

    List<Piece> findPiecesByGame(int gameId);

    Map<Position, Piece> findSquaresByGame(int gameId);

    Optional<Piece> findPieceByPosition(int gameId, Position position);

    int updateByGame(int gameId, Position position, Piece piece);

    int deleteByGame(int gameId);
}
