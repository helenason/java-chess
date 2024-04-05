package dao.mapper;

import domain.board.Turn;
import domain.piece.Color;
import domain.piece.Piece;
import java.util.Arrays;

public enum ColorData {

    WHITE(Color.WHITE, "white"),
    BLACK(Color.BLACK, "black"),
    NONE(Color.NONE, "none"),
    ;

    private final Color color;
    private final String dataOutput;

    ColorData(Color color, String dataOutput) {
        this.color = color;
        this.dataOutput = dataOutput;
    }

    public static String asData(Piece piece) {
        return Arrays.stream(values())
                .filter(pieceType -> pieceType.color == piece.color())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("[DB_ERROR] 저장할 수 없는 데이터입니다."))
                .dataOutput;
    }

    public static String asData(Turn turn) {
        return Arrays.stream(values())
                .filter(pieceType -> pieceType.color == turn.color())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("[DB_ERROR] 저장할 수 없는 데이터입니다."))
                .dataOutput;
    }

    public static Color asColor(String dataOutput) {
        return Arrays.stream(values())
                .filter(pieceType -> pieceType.dataOutput.equals(dataOutput))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("[DB_ERROR] 저장할 수 없는 데이터입니다."))
                .color;
    }
}
