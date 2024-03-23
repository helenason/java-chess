package domain.piece;

import domain.position.Position;

public class None extends Piece {

    public None(Color color) {
        super(color);
    }

    @Override
    public boolean canMove(Position source, Position target) {
        return false;
    }

    @Override
    public String display() {
        return ".";
    }
}