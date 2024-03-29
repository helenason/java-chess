package data;

import domain.piece.Color;
import domain.piece.Piece;
import domain.position.Position;
import java.util.Objects;

public class BoardData {

    private final Position position;
    private final Piece piece;

    public BoardData(Position position, Piece piece) {
        this.position = position;
        this.piece = piece;
    }

    public boolean hasColor(Color color) {
        return piece.hasColor(color);
    }

    public Piece getPiece() {
        return piece;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BoardData boardData = (BoardData) o;
        return Objects.equals(position, boardData.position) && Objects.equals(piece, boardData.piece);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, piece);
    }
}
