package dto;

import domain.piece.Piece;
import domain.position.Position;
import java.util.Map;

public record BoardData(Map<Position, Piece> squares) {
}
