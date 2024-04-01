package dao;

import domain.piece.Piece;
import domain.position.File;
import domain.position.Position;
import java.util.List;
import java.util.Optional;

public interface BoardDao {

    int save(Position position, Piece piece);

    int countAll();

    List<Piece> findPiecesByType(Class<? extends Piece> pieceType);

    List<Piece> findPiecesByFile(File file);

    List<Piece> findAllPieces();

    Optional<Piece> findPieceByPosition(Position position);

    int update(Position position, Piece piece);

    int delete();
}
