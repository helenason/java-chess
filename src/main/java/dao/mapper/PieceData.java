package dao.mapper;

import domain.piece.Bishop;
import domain.piece.Color;
import domain.piece.King;
import domain.piece.Knight;
import domain.piece.None;
import domain.piece.Pawn;
import domain.piece.Piece;
import domain.piece.Queen;
import domain.piece.Rook;
import java.util.Arrays;
import java.util.function.Function;

public enum PieceData {

    BISHOP("bishop", Bishop::new),
    ROOK("rook", Rook::new),
    QUEEN("queen", Queen::new),
    KING("king", King::new),
    KNIGHT("knight", Knight::new),
    PAWN("pawn", Pawn::new),
    NONE("none", None::new),
    ;

    private final String dataOutput;
    private final Function<Color, Piece> pieceGenerator;

    PieceData(String dataOutput, Function<Color, Piece> pieceGenerator) {
        this.dataOutput = dataOutput;
        this.pieceGenerator = pieceGenerator;
    }

    public static String asData(Piece piece) {
        return Arrays.stream(values())
                .filter(pieceType -> pieceType.pieceGenerator.apply(piece.color()).equals(piece))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("[DB_ERROR] 저장할 수 없는 데이터입니다."))
                .dataOutput;
    }

    public static Piece asType(String dataOutput, Color color) {
        return Arrays.stream(values())
                .filter(pieceType -> pieceType.dataOutput.equals(dataOutput))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("[DB_ERROR] 저장할 수 없는 데이터입니다."))
                .pieceGenerator.apply(color);
    }
}
