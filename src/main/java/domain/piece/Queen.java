package domain.piece;

import domain.position.Direction;
import domain.position.Position;
import domain.result.Score;
import java.util.Set;

public class Queen extends Piece {

    private static final Set<Direction> VALID_DIRECTIONS = Direction.allDirections();

    public Queen(Color color) {
        super(color, new Score(9));
    }

    @Override
    public boolean canMove(Position source, Position target) {
        Direction direction = Direction.asDirection(source, target);
        return VALID_DIRECTIONS.contains(direction);
    }
}
