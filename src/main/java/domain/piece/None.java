package domain.piece;

import domain.position.Position;
import domain.result.Score;

public class None extends Piece {

    public None(Color color) {
        super(color, new Score(0));
    }

    @Override
    public boolean isBlank() {
        return true;
    }

    @Override
    public boolean isNotBlank() {
        return false;
    }

    @Override
    public boolean canMove(Position source, Position target) {
        return false;
    }
}
