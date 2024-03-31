package view.mapper.output;

import domain.board.Turn;
import domain.piece.Color;
import java.util.Arrays;

public enum ColorOutput {

    WHITE(Color.WHITE, "화이트(소문자)"),
    BLACK(Color.BLACK, "블랙(대문자)"),
    NONE(Color.NONE, "");

    private final Color color;
    private final String output;

    ColorOutput(Color color, String output) {
        this.color = color;
        this.output = output;
    }

    public static String asOutput(Color color) {
        return Arrays.stream(values())
                .filter(colorOutput -> colorOutput.color == color)
                .findFirst()
                .orElse(NONE)
                .output;
    }

    public static String asOutput(Turn turn) {
        return Arrays.stream(values())
                .filter(colorOutput -> turn.hasColor(colorOutput.color))
                .findFirst()
                .orElse(NONE)
                .output;
    }

    public String output() {
        return output;
    }
}
